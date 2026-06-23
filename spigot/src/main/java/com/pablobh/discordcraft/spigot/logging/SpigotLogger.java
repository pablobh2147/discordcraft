package com.pablobh.discordcraft.spigot.logging;

import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pablobh.discordcraft.logging.PluginLogger;

public class SpigotLogger implements PluginLogger {

    private final Logger logger;

    public SpigotLogger(@Nonnull Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(@Nonnull String message) {
        logger.info(message);
    }

    @Override
    public void warning(@Nonnull String message) {
        logger.warning(message);
    }

    @Override
    public void severe(@Nonnull String message) {
        logger.severe(message);
    }

    @Override
    public void exception(@Nonnull Exception e, @Nullable String message) {
        if (message != null && message.isBlank()) {
            logger.severe(message);
        }
        e.printStackTrace();
    }
    
}
