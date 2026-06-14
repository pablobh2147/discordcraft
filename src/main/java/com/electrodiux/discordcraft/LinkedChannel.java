package com.electrodiux.discordcraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class LinkedChannel {

    // Config Keys

    public static final String DEFAULT_OPTIONS = "channel-defaults";
    public static final String CHANNEL_LIST = "channels";


    public static final String CHANNEL_ID = "channel-id";

    public static final String MINECRAFT_CHAT_MESSAGES = "minecraft-chat-messages";
    public static final String PLAYER_JOIN_MESSAGES = "player-join-messages";
    public static final String PLAYER_LEAVE_MESSAGES = "player-leave-messages";
    public static final String PLAYER_DEATH_MESSAGES = "player-death-messages";
    public static final String PLAYER_MURDER_MENSAGES = "player-murder-messages";

    public static final String DISCORD_MESSAGES = "discord-messages";
    public static final String DISCORD_BOT_MESSAGES = "discord-bot-messages";
    public static final String DISCORD_SYSTEM_MESSAGES = "discord-system-messages";

    public static final String SERVER_START = "server-start";
    public static final String SERVER_STOP = "server-stop";

    private ConfigurationSection config;

    private TextChannel channel;

    private boolean minecraftChatMessages;
    private boolean playerJoinMessages;
    private boolean playerLeaveMessages;
    private boolean playerDeathMessages;
    private boolean playerMurderMessages;

    private boolean discordMessages;
    private boolean botMessages;
    private boolean discordSystemMessages;

    private boolean serverStartMessages;
    private boolean serverStopMessages;

    // Get flags

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

    public boolean canSendBotMessages() {
        return botMessages;
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

    public TextChannel getChannel() {
        return channel;
    }

    // Set flags

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

    public void setSendBotMessages(boolean value) {
        botMessages = value;
        config.set(DISCORD_BOT_MESSAGES, value);
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

    private void setChannel(TextChannel channel) {
        this.channel = channel;
        config.set(CHANNEL_ID, channel.getIdLong());
    }

    // Config

    private static LinkedChannel loadChannel(ConfigurationSection config) {
        if (config == null) {
            return null;
        }

        TextChannel channel = Discord.getTextChannel(config.getLong(CHANNEL_ID, 0));
        
        if (channel == null) {
            return null;
        }

        LinkedChannel data = new LinkedChannel();

        ConfigurationSection defaults = DiscordCraft.instance().getMainConfigManager().getSection(DEFAULT_OPTIONS);

        data.config = config;

        data.setChannel(channel);

        data.setSendMinecraftChatMessages(config.getBoolean(MINECRAFT_CHAT_MESSAGES, defaults.getBoolean(MINECRAFT_CHAT_MESSAGES, true)));
        data.setSendPlayerJoinMessages(config.getBoolean(PLAYER_JOIN_MESSAGES, defaults.getBoolean(PLAYER_JOIN_MESSAGES, true)));
        data.setSendPlayerLeaveMessages(config.getBoolean(PLAYER_LEAVE_MESSAGES, defaults.getBoolean(PLAYER_LEAVE_MESSAGES, true)));
        data.setSendPlayerDeathMessages(config.getBoolean(PLAYER_DEATH_MESSAGES, defaults.getBoolean(PLAYER_DEATH_MESSAGES, true)));
        data.setSendPlayerMurderMessages(config.getBoolean(PLAYER_MURDER_MENSAGES, defaults.getBoolean(PLAYER_MURDER_MENSAGES, true)));

        data.setSendDiscordMessages(config.getBoolean(DISCORD_MESSAGES, defaults.getBoolean(DISCORD_MESSAGES, true)));
        data.setSendBotMessages(config.getBoolean(DISCORD_BOT_MESSAGES, defaults.getBoolean(DISCORD_BOT_MESSAGES, true)));
        data.setSendDiscordSystemMessages(config.getBoolean(DISCORD_SYSTEM_MESSAGES, defaults.getBoolean(DISCORD_SYSTEM_MESSAGES, true)));

        data.setSendServerStartMessages(config.getBoolean(SERVER_START, defaults.getBoolean(SERVER_START, true)));
        data.setSendServerStopMessages(config.getBoolean(SERVER_STOP, defaults.getBoolean(SERVER_STOP, true)));

        return data;
    }

    public static LinkedChannel create(TextChannel channel) {

        if (channel == null) {
            return null;
        }

        LinkedChannel data = new LinkedChannel();

        ConfigurationSection defaults = DiscordCraft.instance().getMainConfigManager().getSection(DEFAULT_OPTIONS);

        data.config = Discord.getBotConfig().createSection(CHANNEL_LIST + ".c" + channel.getIdLong());

        data.setChannel(channel);

        // Set defaults
        data.setSendMinecraftChatMessages(defaults.getBoolean(MINECRAFT_CHAT_MESSAGES, true));
        data.setSendPlayerJoinMessages(defaults.getBoolean(PLAYER_JOIN_MESSAGES, true));
        data.setSendPlayerLeaveMessages(defaults.getBoolean(PLAYER_LEAVE_MESSAGES, true));
        data.setSendPlayerDeathMessages(defaults.getBoolean(PLAYER_DEATH_MESSAGES, true));
        data.setSendPlayerMurderMessages(defaults.getBoolean(PLAYER_MURDER_MENSAGES, true));

        data.setSendDiscordMessages(defaults.getBoolean(DISCORD_MESSAGES, true));
        data.setSendBotMessages(defaults.getBoolean(DISCORD_BOT_MESSAGES, true));
        data.setSendDiscordSystemMessages(defaults.getBoolean(DISCORD_SYSTEM_MESSAGES, true));

        data.setSendServerStartMessages(defaults.getBoolean(SERVER_START, true));
        data.setSendServerStopMessages(defaults.getBoolean(SERVER_STOP, true));

        return data;
    }

    public static List<LinkedChannel> loadAllChannels() {

        ConfigurationSection config = Discord.getBotConfig().getConfigurationSection(CHANNEL_LIST);

        List<LinkedChannel> list = new ArrayList<>();

        if (config == null) {
            return list;
        }

        DiscordCraft.logInfo("Loading " + config.getKeys(false).size() + " linked channels.");

        for (String key : config.getKeys(false)) {

            DiscordCraft.logInfo("Loading linked channel: " + key);

            ConfigurationSection section = config.getConfigurationSection(key);

            if (section == null) {
                continue;
            }

            DiscordCraft.logInfo("Loading linked channel: " + section.getName());

            LinkedChannel data = loadChannel(section);

            if (data != null) {
                list.add(data);
            }
        }

        return list;
    }

    public static void saveChannelsConfig() {
        DiscordCraft.instance().getBotConfigManager().saveConfig();
    }

    // Delete

    void delete() {
        config.getParent().set(config.getName(), null);
    }

}
