package com.pablobh.discordcraft.discord;

import java.net.URL;
import java.util.List;
import java.util.Objects;

import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class LinkedChannel {

    // --------------------- Config Keys ---------------------

    public static final String DEFAULT_OPTIONS = "channel-defaults";
    public static final String CHANNEL_LIST = "channels";

    public static final String CHANNEL_ID = "channel-id";

    public static final String MINECRAFT_CHAT_MESSAGES = "minecraft-chat-messages";
    public static final String PLAYER_JOIN_MESSAGES = "player-join-messages";
    public static final String PLAYER_LEAVE_MESSAGES = "player-leave-messages";
    public static final String PLAYER_DEATH_MESSAGES = "player-death-messages";
    public static final String PLAYER_MURDER_MENSAGES = "player-murder-messages";

    public static final String DISCORD_MESSAGES = "discord-messages";
    public static final String DISCORD_SYSTEM_MESSAGES = "discord-system-messages";

    public static final String SERVER_START = "server-start";
    public static final String SERVER_STOP = "server-stop";

    private static final String WEBHOOK_NAME = "mc-chat-relay";

    // --------------------- Fields ---------------------

    private ConfigurationSection config;
    private TextChannel channel;
    private JDAWebhookClient webhookClient;
    private Webhook webhook;

    // --------------------- Flags ---------------------

    private boolean minecraftChatMessages;
    private boolean playerJoinMessages;
    private boolean playerLeaveMessages;
    private boolean playerDeathMessages;
    private boolean playerMurderMessages;

    private boolean discordMessages;
    private boolean discordSystemMessages;

    private boolean serverStartMessages;
    private boolean serverStopMessages;

    public LinkedChannel(@NonNull ConfigurationSection config, @NonNull ConfigurationSection defaultConfig, @NonNull TextChannel channel) {
        setConfiguration(config, defaultConfig);
        setChannel(channel);
    }

    // --------------------- Methods ---------------------

    public void sendMessage(@Nullable String username, @Nullable URL avatarUrl, @NonNull String message) {
        sendMessage(username, avatarUrl, message, AllowedMentions.none());
    }

    public void sendMessage(@Nullable String username, @Nullable URL avatarUrl, @NonNull String message, @NonNull AllowedMentions mentions) {
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(mentions, "Mentions cannot be null");

        if (webhookClient != null) {
            WebhookMessageBuilder msg = new WebhookMessageBuilder();

            msg.setUsername(username);
            msg.setAvatarUrl(avatarUrl != null ? avatarUrl.toString() : null);
            msg.setContent(message);
            msg.setAllowedMentions(mentions);
            
            webhookClient.send(msg.build());
        }

    }

    public void sendMessage(@NonNull String message) {
        Objects.requireNonNull(message, "Message cannot be null");

        channel.sendMessage(message).queue();
    }

    public TextChannel getChannel() {
        return channel;
    }

    private void setChannel(@NonNull TextChannel channel) {
        Objects.requireNonNull(channel, "Channel cannot be null");

        this.channel = channel;
        config.set(CHANNEL_ID, channel.getIdLong());

        loadWebhook();
    }

    private void loadWebhook() {
        List<Webhook> webhooks = channel.retrieveWebhooks().complete();

        webhook = webhooks.stream()
            .filter(w -> w.getName().equals(WEBHOOK_NAME))
            .findFirst()
            .orElseGet(() -> channel.createWebhook(WEBHOOK_NAME).complete());

        webhookClient = JDAWebhookClient.from(webhook);
    }

    public void deleteWebhook() {
        if (webhookClient != null) {
            webhookClient.close();
            channel.deleteWebhookById(webhook.getId()).complete();
            webhookClient = null;
            webhook = null;
        }
    }

    private void setConfiguration(ConfigurationSection config, ConfigurationSection defaultConfig) {
        Objects.requireNonNull(config, "Config cannot be null");
        Objects.requireNonNull(defaultConfig, "Default config cannot be null");
        
        this.config = config;
        
        this.minecraftChatMessages = config.getBoolean(MINECRAFT_CHAT_MESSAGES, defaultConfig.getBoolean(MINECRAFT_CHAT_MESSAGES, true));
        this.playerJoinMessages = config.getBoolean(PLAYER_JOIN_MESSAGES, defaultConfig.getBoolean(PLAYER_JOIN_MESSAGES, true));
        this.playerLeaveMessages = config.getBoolean(PLAYER_LEAVE_MESSAGES, defaultConfig.getBoolean(PLAYER_LEAVE_MESSAGES, true));
        this.playerDeathMessages = config.getBoolean(PLAYER_DEATH_MESSAGES, defaultConfig.getBoolean(PLAYER_DEATH_MESSAGES, true));
        this.playerMurderMessages = config.getBoolean(PLAYER_MURDER_MENSAGES, defaultConfig.getBoolean(PLAYER_MURDER_MENSAGES, true));
        this.discordMessages = config.getBoolean(DISCORD_MESSAGES, defaultConfig.getBoolean(DISCORD_MESSAGES, true));
        this.discordSystemMessages = config.getBoolean(DISCORD_SYSTEM_MESSAGES, defaultConfig.getBoolean(DISCORD_SYSTEM_MESSAGES, true));
        this.serverStartMessages = config.getBoolean(SERVER_START, defaultConfig.getBoolean(SERVER_START, true));
        this.serverStopMessages = config.getBoolean(SERVER_STOP, defaultConfig.getBoolean(SERVER_STOP, true));
    }

    // --------------------- Flag Getters ---------------------

    public boolean canSendMinecraftChatMessages() {
        return minecraftChatMessages;
    }

    public boolean canSendPlayerJoinMessages() {
        return playerJoinMessages;
    }

    public boolean canSendPlayerLeaveMessages() {
        return playerLeaveMessages;
    }

    public boolean canSendPlayerDeathMessages() {
        return playerDeathMessages;
    }

    public boolean canSendPlayerMurderMessages() {
        return playerMurderMessages;
    }

    public boolean canSendDiscordMessages() {
        return discordMessages;
    }

    public boolean canSendDiscordSystemMessages() {
        return discordSystemMessages;
    }

    public boolean canSendServerStartMessages() {
        return serverStartMessages;
    }

    public boolean canSendServerStopMessages() {
        return serverStopMessages;
    }

    // --------------------- Flag Setters ---------------------

    public void setSendMinecraftChatMessages(boolean value) {
        minecraftChatMessages = value;
        config.set(MINECRAFT_CHAT_MESSAGES, value);
    }

    public void setSendPlayerJoinMessages(boolean value) {
        playerJoinMessages = value;
        config.set(PLAYER_JOIN_MESSAGES, value);
    }

    public void setSendPlayerLeaveMessages(boolean value) {
        playerLeaveMessages = value;
        config.set(PLAYER_LEAVE_MESSAGES, value);
    }

    public void setSendPlayerDeathMessages(boolean value) {
        playerDeathMessages = value;
        config.set(PLAYER_DEATH_MESSAGES, value);
    }

    public void setSendPlayerMurderMessages(boolean value) {
        playerMurderMessages = value;
        config.set(PLAYER_MURDER_MENSAGES, value);
    }

    public void setSendDiscordMessages(boolean value) {
        discordMessages = value;
        config.set(DISCORD_MESSAGES, value);
    }

    public void setSendDiscordSystemMessages(boolean value) {
        discordSystemMessages = value;
        config.set(DISCORD_SYSTEM_MESSAGES, value);
    }

    public void setSendServerStartMessages(boolean value) {
        serverStartMessages = value;
        config.set(SERVER_START, value);
    }

    public void setSendServerStopMessages(boolean value) {
        serverStopMessages = value;
        config.set(SERVER_STOP, value);
    }

}
