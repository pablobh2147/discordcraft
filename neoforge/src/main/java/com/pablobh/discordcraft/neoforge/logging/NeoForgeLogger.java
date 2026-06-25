package com.pablobh.discordcraft.neoforge.logging;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pablobh.discordcraft.logging.PluginLogger;

public class NeoForgeLogger implements PluginLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger("DiscordCraft");

    @Override
    public void info(@Nonnull String message) {
        LOGGER.info(message);
    }

    @Override
    public void warning(@Nonnull String message) {
        LOGGER.warn(message);
    }

    @Override
    public void severe(@Nonnull String message) {
        LOGGER.error(message);
    }

    @Override
    public void exception(@Nonnull Exception e, @Nullable String message) {
        if (message != null && !message.isBlank()) {
            LOGGER.error(message, e);
        } else {
            LOGGER.error("An exception occurred", e);
        }
    }
    
}
