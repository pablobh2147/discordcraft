package com.pablobh.discordcraft.spigot.message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;

import com.pablobh.discordcraft.spigot.config.Configuration;

public class MessageService {
   
    private Configuration config;

    public MessageService(Configuration config) {
        this.config = config;
    }

    @Nullable
    public Message getMessage(@Nonnull String key) {
        String messageContent = config.getString(key);
        
        if (messageContent == null || messageContent.isEmpty()) {
            return null;
        }
        
        return new StringMessage(messageContent);
    }

    @Nonnull
    public Message getMessageOrEmpty(@Nonnull String key) {
        return getMessageOrDefault(key, "");
    }

    @Nonnull
    public Message getMessageOrDefault(@Nonnull String key, @Nonnull String defaultValue) {
        Message message = getMessage(key);
        return message != null ? message : new StringMessage(defaultValue);
    }

    @Nullable
    public Message getDiscordMessage(@Nonnull String key) {
        if (config.getConfigObj().isString(key)) {
            String messageContent = config.getConfigObj().getString(key);
            if (messageContent == null || messageContent.isEmpty()) {
                return null;
            }
            return new StringMessage(messageContent);
        }
        
        if (config.getConfigObj().isConfigurationSection(key)) {
            ConfigurationSection section = config.getConfigObj().getConfigurationSection(key);
            if (section == null) {
                return null;
            }
            return new DiscordMessage(section);
        }
        
        return null;
    }

    @Nonnull
    public Message getDiscordMessageOrDefault(@Nonnull String key, @Nonnull String defaultValue) {
        Message message = getDiscordMessage(key);    
        return message != null ? message : new StringMessage(defaultValue);
    }

    @Nullable
    public String getPlainMessage(@Nonnull String key) {
        return config.getString(key, null);
    }

    @Nonnull
    public String getPlainMessageOrDefault(@Nonnull String key, @Nonnull String defaultValue) {
        return config.getString(key, defaultValue);
    }
}
