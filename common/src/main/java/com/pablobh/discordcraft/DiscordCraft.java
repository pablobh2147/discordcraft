package com.pablobh.discordcraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;

import com.pablobh.discordcraft.configuration.Configuration;
import com.pablobh.discordcraft.discord.DiscordService;
import com.pablobh.discordcraft.discord.LinkedChannel;
import com.pablobh.discordcraft.logging.PluginLogger;
import com.pablobh.discordcraft.message.Message;
import com.pablobh.discordcraft.message.MessageService;

public class DiscordCraft {

    private static final String TOKEN_ENV_VAR_NAME = "DISCORDCRAFT_BOT_TOKEN";

    private final PluginLogger logger;

    private final Configuration globalConfig;
    private final Configuration messagesConfig;
    private final Configuration botConfig;
    private final Configuration commandsConfig;

    private final DiscordService discordService;
    private final MessageService messageService;
   
    public DiscordCraft(
        PluginLogger logger,
        Configuration globalConfig,
        Configuration messagesConfig,
        Configuration botConfig,
        Configuration commandsConfig
    ) throws LoginException {
        this.logger = logger;
        
        this.globalConfig = globalConfig;
        this.messagesConfig = messagesConfig;
        this.botConfig = botConfig;
        this.commandsConfig = commandsConfig;

        String token = getBotToken();
        if (token == null || token.isEmpty()) {
            throw new LoginException("Bot token is not set. Please set it in the config or as an environment variable.");
        }
        
        this.discordService = new DiscordService(this, token);
        this.messageService = new MessageService(messagesConfig);
    }

    // --------------------- Logging ---------------------

    @Nonnull
    public PluginLogger getLogger() {
        return logger;
    }

    // --------------------- Configuration ---------------------

    public void saveConfigurations() {
        globalConfig.save();
        messagesConfig.save();
        botConfig.save();
        commandsConfig.save();

        logger.info("All configurations saved.");
    }

    public void reloadConfigurations() {
        globalConfig.reload();
        messagesConfig.reload();
        botConfig.reload();
        commandsConfig.reload();

        logger.info("All configurations reloaded.");
    }

    @Nonnull
    public Configuration getGlobalConfig() {
        return globalConfig;
    }

    @Nonnull
    public Configuration getMessagesConfig() {
        return messagesConfig;
    }

    @Nonnull
    public Configuration getBotConfig() {
        return botConfig;
    }

    @Nonnull
    public Configuration getCommandsConfig() {
        return commandsConfig;
    }

    // --------------------- Notifications ---------------------

    public void notifyServerStart() {
        Message msg = messageService.getDiscordMessage("server.start");
        
        for (LinkedChannel channel : discordService.getLinkedChannels()) {
            if (channel.canSendServerStartMessages()) {
                channel.sendMessage(msg);
            }
        }
    }

    public void notifyServerStop() {
        Message msg = messageService.getDiscordMessage("server.stop");

        for (LinkedChannel channel : discordService.getLinkedChannels()) {
            if (channel.canSendServerStopMessages()) {
                channel.sendMessage(msg);
            }
        }
    }

    // --------------------- Services ---------------------

    @Nonnull
    public DiscordService getDiscordService() {
        return discordService;
    }

    @Nonnull
    public MessageService getMessageService() {
        return messageService;
    }

    // --------------------- Bot Token ---------------------

    private static boolean isBotTokenValid(@Nullable String token) {
        return token != null && !token.isEmpty();
    }

    @Nullable
    private String getBotTokenFromConfig() {
        return botConfig.getString("token", null);
    }

    @Nullable
    private String getBotTokenFromEnv() {
        try {
            return System.getenv(TOKEN_ENV_VAR_NAME);
        } catch (SecurityException e) {
            logger.severe("Security exception while reading " + TOKEN_ENV_VAR_NAME + " from environment variables.");
            return null;
        }
    }

    @Nullable
    public String getBotToken() {
        String token = getBotTokenFromConfig();
        if (isBotTokenValid(token)) {
            logger.info("Using bot token from config.");
            return token;
        }

        token = getBotTokenFromEnv();
        if (isBotTokenValid(token)) {
            logger.info("Using bot token from environment variables.");
            return token;
        }
        
        logger.severe("No valid bot token found in config or environment variables.");
        return null;
    }

    // --------------------- Shutdown ---------------------

    public void shutdown() {
        discordService.shutdown();
    }

}
