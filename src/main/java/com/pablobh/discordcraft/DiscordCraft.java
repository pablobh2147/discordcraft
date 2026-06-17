package com.pablobh.discordcraft;

import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.pablobh.discordcraft.config.Configuration;
import com.pablobh.discordcraft.config.GlobalConfiguration;
import com.pablobh.discordcraft.discord.DiscordService;
import com.pablobh.discordcraft.discord.LinkedChannel;
import com.pablobh.discordcraft.listeners.MinecraftChatListener;
import com.pablobh.discordcraft.listeners.PlayerEventsListener;
import com.pablobh.discordcraft.message.Message;
import com.pablobh.discordcraft.message.MessageService;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DiscordCraft extends JavaPlugin {

    private static final String TOKEN_ENV_VAR_NAME = "DISCORDCRAFT_BOT_TOKEN";

    private static DiscordCraft instance;
    
    private GlobalConfiguration globalConfiguration;
    private Configuration messagesConfig;
    private Configuration botConfig;
    private Configuration discordCommandsConfig;

    private DiscordService discordService;
    private MessageService messageService;

    private boolean enabled = false;

    // --------------------- Lifecycle ---------------------

    @Override
    public void onEnable() {
        instance = this;

        globalConfiguration = new GlobalConfiguration(this, "config.yml");
        messagesConfig = new Configuration(this, "messages.yml");
        botConfig = new Configuration(this, "bot.yml");
        discordCommandsConfig = new Configuration(this, "discord-commands.yml");

        messageService = new MessageService(messagesConfig);

        if (!initializeDiscordService()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        registerSpigotListeners();
        sendServerStartNotification();

        DiscordCraft.logInfo(getDescription().getName() + " v" + getDescription().getVersion() + " has been enabled!");
    
        enabled = true;
    }

    @Override
    public void onDisable() {
        if (!enabled) {
            return;
        }

        sendServerStopNotification();

        if (discordService != null) {
            discordService.shutdown();
        }

        saveAllConfigurations();

        DiscordCraft.logInfo(getDescription().getName() + " v" + getDescription().getVersion() + " has been disabled!");

        instance = null;
        enabled = false;
    }

    // --------------------- Initialization ---------------------

    private boolean initializeDiscordService() {
        String token = botConfig.getString("token", null);
        if (token == null || token.isEmpty()) {
            try {
                token = System.getenv(TOKEN_ENV_VAR_NAME);
                DiscordCraft.logInfo("Using bot token from environment variables.");
            } catch (SecurityException e) {
                DiscordCraft.logSevere("Security exception while reading " + TOKEN_ENV_VAR_NAME + " from environment variables.");
            }
        } else {
            DiscordCraft.logInfo("Using bot token from config.");
        }

        if (token == null || token.isEmpty()) {
            DiscordCraft.logSevere("Bot token is not set in bot.yml nor in environment variables. Please set it and restart the server.");
            return false;
        }

        try {
            discordService = new DiscordService(token, globalConfiguration, botConfig, discordCommandsConfig, messageService);
        } catch (LoginException e) {
            DiscordCraft.logSevere("Failed to initialize Discord service: " + e.getMessage());
            return false;
        }

        return true;
    }

    private void registerSpigotListeners() {
        Bukkit.getPluginManager().registerEvents(new MinecraftChatListener(globalConfiguration, discordService), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEventsListener(discordService, messageService), this);
    }

    // --------------------- Configuration ---------------------

    public void saveAllConfigurations() {
        globalConfiguration.save();
        messagesConfig.save();
        botConfig.save();
        discordCommandsConfig.save();
    }

    public void reloadConfig() {
        globalConfiguration.load();
        messagesConfig.load();
        botConfig.load();
        discordCommandsConfig.load();
        DiscordCraft.logInfo("Configuration reloaded.");
    }

    // --------------------- Notifications ---------------------

    private void sendServerStartNotification() {
        Message msg = messageService.getDiscordMessage("server.start");
        
        for (LinkedChannel channel : discordService.getLinkedChannels()) {
            if (channel.canSendServerStartMessages()) {
                channel.sendMessage(msg);
            }
        }
    }

    private void sendServerStopNotification() {
        Message msg = messageService.getDiscordMessage("server.stop");

        for (LinkedChannel channel : discordService.getLinkedChannels()) {
            if (channel.canSendServerStopMessages()) {
                channel.sendMessage(msg);
            }
        }
    }

    // --------------------- Logging ---------------------

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

    // --------------------- Discord logging ---------------------

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

    // --------------------- Instance ---------------------

    public static DiscordCraft getInstance() {
        return instance;
    }

}