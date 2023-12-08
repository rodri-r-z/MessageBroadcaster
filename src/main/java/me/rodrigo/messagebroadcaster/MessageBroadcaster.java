package me.rodrigo.messagebroadcaster;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.rodrigo.messagebroadcaster.lib.Parser;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "messagebroadcaster",
        name = "MessageBroadcaster",
        version = "1.0",
        description = "Message broadcaster",
        authors = { "Rodrigo R." }
)
public class MessageBroadcaster {

    @Inject
    private Logger logger;
    private ProxyServer proxyServer;
    private Parser parser;

    @Inject
    public MessageBroadcaster(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        if (!dataDirectory.toFile().exists() && !dataDirectory.toFile().mkdirs()) {
            logger.error("Failed to create data directory");
            return;
        }
        if (!dataDirectory.resolve("config.yml").toFile().exists()) {

        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
