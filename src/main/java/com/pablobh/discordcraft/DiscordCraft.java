package com.pablobh.discordcraft;

import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.pablobh.discordcraft.config.Configuration;
import com.pablobh.discordcraft.config.GlobalConfiguration;
import com.pablobh.discordcraft.discord.DiscordService;
import com.pablobh.discordcraft.discord.LinkedChannel;
import com.pablobh.discordcraft.listeners.MinecraftChatListener;
import com.pablobh.discordcraft.listeners.PlayerEventsListener;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DiscordCraft extends JavaPlugin {
    
    // Instance

    private static DiscordCraft instance;
    
    // Configurations
    
    private Configuration messagesConfig;
    private Configuration botConfig;
    private Configuration discordCommandsConfig;

    private GlobalConfiguration globalConfiguration;

    private DiscordService discordService;

    private boolean enabled = false;

    @Override
    public void onEnable() {
        instance = this;

        globalConfiguration = new GlobalConfiguration(this, "config.yml");
        messagesConfig = new Configuration(this, "messages.yml");
        botConfig = new Configuration(this, "bot.yml");
        discordCommandsConfig = new Configuration(this, "discord-commands.yml");

        Messages.setup(messagesConfig);

        discordService = new DiscordService();
        if (!discordService.setup(botConfig)) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        registerSpigotListeners();
        serverStartMessages();

        enabled = true;
        DiscordCraft.logInfo(getDescription().getName() + " v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        if (!enabled) {
            return;
        }

        serverStopMessages();
        if (discordService != null) {
            discordService.shutdown();
        }

        saveAllConfigurations();

        DiscordCraft.logInfo(getDescription().getName() + " v" + getDescription().getVersion() + " has been disabled!");

        instance = null;
        enabled = false;
    }

    public void saveAllConfigurations() {
        globalConfiguration.save();
        messagesConfig.save();
        botConfig.save();
        discordCommandsConfig.save();
    }

    public void reloadConfig() {
        globalConfiguration.load();
        DiscordCraft.logInfo("Configuration reloaded.");
    }

    public GlobalConfiguration getGlobalConfiguration() {
        return globalConfiguration;
    }

    public Configuration getMessagesConfiguration() {
        return messagesConfig;
    }

    public Configuration getBotConfiguration() {
        return botConfig;
    }

    public Configuration getDiscordCommandsConfiguration() {
        return discordCommandsConfig;
    }

    public DiscordService getDiscordService() {
        return discordService;
    }

    // Notifications

    private void serverStartMessages() {
        String message = Messages.getMessage("server.start");

        for (LinkedChannel linkedChannel : discordService.getLinkedChannels()) {
            if (linkedChannel.canSendServerStartMessages()) {
                linkedChannel.sendMessage(message);
            }
        }
    }

    private void serverStopMessages() {
        String message = Messages.getMessage("server.stop");

        for (LinkedChannel linkedChannel : discordService.getLinkedChannels()) {
            if (linkedChannel.canSendServerStopMessages()) {
                linkedChannel.sendMessage(message);
            }
        }
    }

    // Instance

    public static DiscordCraft instance() {
        return instance;
    }

    // Listeners

    public void registerSpigotListeners() {
        Bukkit.getPluginManager().registerEvents(new MinecraftChatListener(globalConfiguration, discordService), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEventsListener(discordService), this);
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

        DiscordService discordService = instance != null ? instance.discordService : null;
        TextChannel logChannel = discordService != null ? discordService.getLogChannel() : null;
        if (logChannel != null) {
            logChannel.sendMessage("[INFO]: " + message).queue();
        }

        logInfo(message);
    }

    public static void discordLogWarning(@NotNull String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        DiscordService discordService = instance != null ? instance.discordService : null;
        TextChannel logChannel = discordService != null ? discordService.getLogChannel() : null;
        if (logChannel != null) {
            logChannel.sendMessage("[WARNING]: " + message).queue();
        }

        logWarning(message);
    }

    public static void discordLogSevere(@NotNull String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        DiscordService discordService = instance != null ? instance.discordService : null;
        TextChannel logChannel = discordService != null ? discordService.getLogChannel() : null;
        if (logChannel != null) {
            logChannel.sendMessage("[ERROR]: " + message).queue();
        }

        logSevere(message);
    }

    public static void discordLogException(@NotNull Exception e, @NotNull String message) {
        if (e == null) {
            return;
        }

        DiscordService discordService = instance != null ? instance.discordService : null;
        TextChannel logChannel = discordService != null ? discordService.getLogChannel() : null;
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