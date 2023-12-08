package me.rodrigo.messagebroadcaster;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.rodrigo.messagebroadcaster.http.Http;
import me.rodrigo.messagebroadcaster.lib.Parser;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "messagebroadcaster",
        name = "MessageBroadcaster",
        version = "1.0",
        description = "Message broadcaster",
        authors = { "Rodrigo R." }
)
public class MessageBroadcaster {

    @Inject
    private final Logger logger;
    private final ProxyServer proxyServer;
    private List<String> messages;
    private String lastBroadcastedMessage = "";
    private Parser parser;

    @Inject
    public MessageBroadcaster(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        if (!dataDirectory.toFile().exists() && !dataDirectory.toFile().mkdirs()) {
            logger.error("Failed to create data directory");
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
        } catch (IOException e) {
            logger.error("Failed to download config.yml due to: " + e.getMessage());
        }
    }

    private String getRandomMessage() {
        String message = messages.get((int) (Math.random() * messages.size()));
        while (message.equals(lastBroadcastedMessage)) {
            message = messages.get((int) (Math.random() * messages.size()));
        }
        lastBroadcastedMessage = message;
        return message;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        if (parser == null) return;
        if (messages.size() < 2) {
            logger.error("Messages list must have at least 2 messages");
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
        proxyServer.getScheduler().buildTask(this, () -> {
            String message = getRandomMessage().replaceAll("&", "§");
            proxyServer.sendMessage(Component.text(message));
        }).repeat(amount, unit).schedule();
    }
}
