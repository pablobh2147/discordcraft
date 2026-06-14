package com.electrodiux.discordcraft;

import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.electrodiux.discordcraft.links.AccountLink;
import com.electrodiux.discordcraft.listeners.MinecraftChatListener;
import com.electrodiux.discordcraft.listeners.PlayerEventsListener;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DiscordCraft extends JavaPlugin {
    
    // Instance

    private static DiscordCraft instance;
    
    // Flags

    private boolean isDiscordCraftEnabled = false;
    
    // Configurations
    
    private ConfigManager mainConfig;
    private ConfigManager messagesConfig;
    private ConfigManager botConfig;
    private ConfigManager discordCommandsConfig;

    @Override
    public void onEnable() {
        instance = this;

        // Load Configurations
        mainConfig = new ConfigManager("config.yml", true);
        messagesConfig = new ConfigManager("messages.yml", true);
        botConfig = new ConfigManager("bot.yml", true);
        discordCommandsConfig = new ConfigManager("discord-commands.yml", true);

        // Setup Messages
        Messages.setup();

        // Load Discord JDA
        if (!Discord.setup()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        AccountLink.initialize();

        // Register Listeners
        registerSpigotListeners();

        isDiscordCraftEnabled = true;

        // Start Messages
        serverStartMessages();

        DiscordCraft.logInfo(getDescription().getName() + " v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        // Check if DiscordCraft is enabled
        if (!DiscordCraft.isDiscordCraftEnabled()) {
            return;
        }

        // Stop Messages
        serverStopMessages();

        // Save Account Links
        AccountLink.save();

        // Shutdown Discord JDA
        Discord.shutdown();

        // Save Configurations
        mainConfig.saveConfig();
        messagesConfig.saveConfig();
        botConfig.saveConfig();
        discordCommandsConfig.saveConfig();

        // Disable
        DiscordCraft.logInfo(getDescription().getName() + " v" + getDescription().getVersion() + " has been disabled!");

        instance = null;
        isDiscordCraftEnabled = false;
    }

    // Notifications

    private void serverStartMessages() {
        String message = Messages.getMessage("server.start");

        for (LinkedChannel linkedChannel : Discord.getLinkedChannels()) {
            if (linkedChannel.canSendServerStartMessages()) {
                linkedChannel.getChannel().sendMessage(message).queue();
            }
        }
    }

    private void serverStopMessages() {
        String message = Messages.getMessage("server.stop");

        for (LinkedChannel linkedChannel : Discord.getLinkedChannels()) {
            if (linkedChannel.canSendServerStopMessages()) {
                linkedChannel.getChannel().sendMessage(message).queue();
            }
        }
    }

    // Instance

    public static DiscordCraft instance() {
        return instance;
    }

    // Listeners

    public void registerSpigotListeners() {
        Bukkit.getPluginManager().registerEvents(new MinecraftChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEventsListener(), this);
    }

    // Configurations

    public ConfigManager getMainConfigManager() {
        return mainConfig;
    }

    public ConfigManager getMessagesConfigManager() {
        return messagesConfig;
    }

    public ConfigManager getBotConfigManager() {
        return botConfig;
    }

    public ConfigManager getDiscordCommandsConfigManager() {
        return discordCommandsConfig;
    }

    // Checkers

    public static boolean isDiscordCraftEnabled() {
        return instance != null && instance.isDiscordCraftEnabled;
    }

    // Logging

    public static Logger getDiscordCraftLogger() {
        if (instance == null) {
            return null;
        }
        return instance.getLogger();
    }

    public static void logInfo(@NotNull String message) {
        if (message == null) {
            return;
        }
        
        Logger logger = getDiscordCraftLogger();
        if (logger != null) {
            logger.info(message);
        }
    }

    public static void logWarning(@NotNull String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        Logger logger = getDiscordCraftLogger();
        if (logger != null) {
            logger.warning(message);
        }
    }

    public static void logSevere(@NotNull String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        Logger logger = getDiscordCraftLogger();
        if (logger != null) {
            logger.severe(message);
        }
    }

    public static void logException(@NotNull Exception e, @Nullable String message) {
        if (e == null) {
            return;
        }

        Logger logger = getDiscordCraftLogger();
        if (logger != null) {
            if (message != null && message.isBlank()) {
                logger.severe(message);
            }
            e.printStackTrace();
        }
    }

    // Discord Logging

    public static void discordLogInfo(@NotNull String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        TextChannel logChannel = Discord.getLogChannel();
        if (logChannel != null) {
            logChannel.sendMessage("[INFO]: " + message).queue();
        }

        logInfo(message);
    }

    public static void discordLogWarning(@NotNull String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        TextChannel logChannel = Discord.getLogChannel();
        if (logChannel != null) {
            logChannel.sendMessage("[WARNING]: " + message).queue();
        }

        logWarning(message);
    }

    public static void discordLogSevere(@NotNull String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        TextChannel logChannel = Discord.getLogChannel();
        if (logChannel != null) {
            logChannel.sendMessage("[ERROR]: " + message).queue();
        }

        logSevere(message);
    }

    public static void discordLogException(@NotNull Exception e, @NotNull String message) {
        if (e == null) {
            return;
        }

        TextChannel logChannel = Discord.getLogChannel();
        if (logChannel != null) {
            if (message != null && !message.isBlank()) {
                logChannel.sendMessage("[ERROR]: " + message + " (Check Console for more Details)").queue();
            } else {
                logChannel.sendMessage("[ERROR]: An internal server error occurred! (Check Console for more Details)").queue();
            }
        }

        logException(e, message);
    }

}