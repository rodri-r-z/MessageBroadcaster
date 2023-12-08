package me.rodrigo.messagebroadcaster.bukkit;

import me.rodrigo.messagebroadcaster.lib.Parser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class Bukkit extends JavaPlugin {
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
                saveResource("config.yml", false);
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
        long multiplier;
        switch (parser.AsString("every.unit").toLowerCase()) {
            case "minutes":
                multiplier = 20L * 60L;
                break;
            case "hours":
                multiplier = 20L * 60L * 60L;
                break;
            case "days":
                multiplier = 20L * 60L * 60L * 24L;
                break;
            default:
                multiplier = 20L;
                break;
        }
        long amount = Long.parseLong(parser.AsObject("every.amount").toString());
        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            String message = getRandomMessage().replaceAll("&", "§");
            for (Player player : getServer().getOnlinePlayers()) {
                player.sendMessage(
                        message
                                .replaceAll("(?i)\\{player\\}", player.getName())
                );
            }
            getServer().broadcastMessage(message);
        }, 0, amount * multiplier);
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
