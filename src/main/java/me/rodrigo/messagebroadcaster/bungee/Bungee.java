package me.rodrigo.messagebroadcaster.bungee;

import me.rodrigo.messagebroadcaster.http.Http;
import me.rodrigo.messagebroadcaster.lib.Parser;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Bungee extends Plugin {
    private List<String> messages;
    private String lastBroadcastedMessage = "";
    private Parser parser;
    private Logger logger;

    @Override
    public void onEnable() {
        final Path dataDirectory = getDataFolder().toPath();
        final Logger logger = getLogger();
        this.logger = logger;
        if (!dataDirectory.toFile().exists() && !dataDirectory.toFile().mkdirs()) {
            logger.severe("Failed to create data directory");
            return;
        }
        try {
            if (!dataDirectory.resolve("config.yml").toFile().exists()) {
                Http.DownloadFile(
                        "https://raw.githubusercontent.com/rodri-r-z/MessageBroadcaster/main/src/main/resources/config.yml",
                        dataDirectory.resolve("config.yml").toString()
                );
            }
            this.parser = new Parser(dataDirectory.resolve("config.yml"));
            messages = parser.AsStringList("messages");
            Enable();
        } catch (IOException e) {
            logger.severe("Failed to download config.yml due to: " + e.getMessage());
        }

    }

    public void Enable() {
        if (parser == null) return;
        if (messages.size() < 2) {
            logger.severe("Messages list must have at least 2 messages");
            return;
        }
        TimeUnit unit;
        switch (parser.AsString("every.unit").toLowerCase()) {
            case "minutes":
                unit = TimeUnit.MINUTES;
                break;
            case "hours":
                unit = TimeUnit.HOURS;
                break;
            case "days":
                unit = TimeUnit.DAYS;
                break;
            default:
                unit = TimeUnit.SECONDS;
                break;
        }
        long amount = Long.parseLong(parser.AsObject("every.amount").toString());
        getProxy().getScheduler().schedule(this, () -> {
            String message = getRandomMessage().replaceAll("&", "§");
            getProxy().broadcast(TextComponent.fromLegacyText(message));
        }, amount, unit);
    }

    private String getRandomMessage() {
        String message = messages.get((int) (Math.random() * messages.size()));
        while (message.equals(lastBroadcastedMessage)) {
            message = messages.get((int) (Math.random() * messages.size()));
        }
        lastBroadcastedMessage = message;
        return message;
    }
}
