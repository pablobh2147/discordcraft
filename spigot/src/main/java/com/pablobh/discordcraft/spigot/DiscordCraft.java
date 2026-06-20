package com.pablobh.discordcraft.spigot;

import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.pablobh.discordcraft.spigot.config.Configuration;
import com.pablobh.discordcraft.spigot.config.GlobalConfiguration;
import com.pablobh.discordcraft.spigot.discord.DiscordService;
import com.pablobh.discordcraft.spigot.discord.LinkedChannel;
import com.pablobh.discordcraft.spigot.listeners.MinecraftChatListener;
import com.pablobh.discordcraft.spigot.listeners.PlayerEventsListener;
import com.pablobh.discordcraft.spigot.message.Message;
import com.pablobh.discordcraft.spigot.message.MessageService;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DiscordCraft extends JavaPlugin {

    private static final String TOKEN_ENV_VAR_NAME = "DISCORDCRAFT_BOT_TOKEN";

    private static DiscordCraft instance;
    
    private GlobalConfiguration globalConfiguration;
    private Configuration messagesConfig;
    private Configuration botConfig;
    private Configuration commandsConfig;

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
        commandsConfig = new Configuration(this, "commands.yml");

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

    private boolean isTokenValid(@Nullable String token) {
        return token != null && !token.isEmpty();
    }

    private String getBotToken() {
        String token = botConfig.getString("token", null);

        if (isTokenValid(token)) {
            DiscordCraft.logInfo("Using bot token from config.");
            return token;
        }

        try {
            token = System.getenv(TOKEN_ENV_VAR_NAME);

            if (isTokenValid(token)) {
                DiscordCraft.logInfo("Using bot token from environment variables.");
                return token;
            }
        } catch (SecurityException e) {
            DiscordCraft.logSevere("Security exception while reading " + TOKEN_ENV_VAR_NAME + " from environment variables.");
        }

        return null;
    }

    private boolean initializeDiscordService() {
        String token = getBotToken();

        if (token == null) {
            DiscordCraft.logSevere("Bot token is not set in bot.yml nor in environment variables. Please set it and restart the server.");
            return false;
        }

        try {
            discordService = new DiscordService(token, globalConfiguration, botConfig, commandsConfig, messageService);
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
        commandsConfig.save();

        DiscordCraft.logInfo("All configurations saved.");
    }

    public void reloadConfig() {
        globalConfiguration.load();
        messagesConfig.load();
        botConfig.load();
        commandsConfig.load();

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