package com.electrodiux.discordcraft;

import java.util.List;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import com.electrodiux.discordcraft.commands.discord.CommandManager;
import com.electrodiux.discordcraft.listeners.DiscordChatListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Discord {

    // Config keys

    public static final String BOT_TOKEN = "token";

    public static final String GUILD_ID = "guild";
    public static final String LOG_CHANNEL = "log-channel";

    public static final String ACTIVITY_ENABLED = "activity.show";
    public static final String ACTIVITY_TYPE = "activity.type";
    public static final String ACTIVITY_NAME = "activity.name";

    // Data

    private static JDA jda;

    private static Guild mainGuild;

    private static ConfigurationSection config;

    private static List<LinkedChannel> linkedChannels;

    private static TextChannel logChannel;

    // Discord setup

    public static boolean setup() {

        config = DiscordCraft.instance().getBotConfigManager().getConfig();

        String token = getBotConfig().getString(BOT_TOKEN);

        if (token == null || token.isBlank()) {
            DiscordCraft.logWarning("No bot token was found in the config! Please add one and restart the server.");
            return false;
        }

        boolean settedUp = setputJDA(token);

        if (!settedUp) {
            DiscordCraft.logWarning("An error occurred while setting up Discord JDA!");
            return false;
        }

        mainGuild = getGuild(getBotConfig().getLong(GUILD_ID, 0));

        if (mainGuild == null) {
            DiscordCraft.logWarning("No server was found with the ID provided in the config! Please check the ID or run /setup command on Discord.");
        }

        linkedChannels = LinkedChannel.loadAllChannels();

        logChannel = getTextChannel(getBotConfig().getLong(LOG_CHANNEL, 0));

        DiscordCraft.logInfo("Loaded " + linkedChannels.size() + " linked channels.");
        for (LinkedChannel linkedChannel : linkedChannels) {
            DiscordCraft.logInfo("Loaded linked channel: " + linkedChannel.getChannel().getName() + " in guild: " + linkedChannel.getChannel().getGuild().getName());
        }

        return true;
    }

    // Discord JDA setup

    private static void configureMemoryUsage(JDABuilder builder) {
        builder.disableCache(CacheFlag.ACTIVITY);

        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.enableIntents(GatewayIntent.DIRECT_MESSAGES);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
    }

    private static void configureActivity(JDABuilder builder) {
        boolean showActivity = getBotConfig().getBoolean(ACTIVITY_ENABLED, true);

        if (!showActivity) {
            return;
        }

        String activityType = getBotConfig().getString(ACTIVITY_TYPE);
        String activityName = getBotConfig().getString(ACTIVITY_NAME);

        DiscordCraft.logInfo("Activity type: " + activityType);
        DiscordCraft.logInfo("Activity name: " + activityName);

        if (activityName != null) {
            ActivityType type = ActivityType.valueOf(activityType);

            if (type != null) {
                builder.setActivity(Activity.of(type, activityName));
            } else {
                builder.setActivity(Activity.playing(activityName));
            }

        }
    }

    private static boolean setputJDA(String token) {
        try {
            JDABuilder builder = JDABuilder.createDefault(token);

            configureMemoryUsage(builder);
            configureActivity(builder);

            builder.addEventListeners(new DiscordChatListener());
            builder.addEventListeners(new CommandManager());

            jda = builder.build();
            if (jda == null) {
                throw new LoginException("Couldn't login in to Discord!");
            }

            jda.awaitReady();

            return true;
        } catch (Exception e) {
            DiscordCraft.logWarning("An error occurred while setting up Discord!");
            e.printStackTrace();
            return false;
        }
    }

    // Shutdown

    public static void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    // Discord element getters

    public static TextChannel getTextChannel(long id) {

        if (id != 0) {
            TextChannel channel = mainGuild.getTextChannelById(id);
            if (channel != null) {
                Bukkit.getConsoleSender().sendMessage("Found text channel: " + channel.getName());
                return channel;
            }
            Bukkit.getConsoleSender().sendMessage("Could not find text channel with ID: " + id);
        }

        return null;
    }

    public static VoiceChannel getVoiceChannel(long id) {

        if (id != 0) {
            VoiceChannel channel = mainGuild.getVoiceChannelById(id);
            if (channel != null) {
                Bukkit.getConsoleSender().sendMessage("Found voice channel: " + channel.getName());
                return channel;
            }
            Bukkit.getConsoleSender().sendMessage("Could not find voice channel with ID: " + id);
        }

        return null;
    }

    public static Category getCategory(long id) {

        if (id != 0) {
            Category category = mainGuild.getCategoryById(id);
            if (category != null) {
                Bukkit.getConsoleSender().sendMessage("Found category: " + category.getName());
                return category;
            }
            Bukkit.getConsoleSender().sendMessage("Could not find category with ID: " + id);
        }

        return null;
    }

    public static Guild getGuild(long id) {

        if (id != 0) {
            Guild guild = jda.getGuildById(id);
            if (guild != null) {
                Bukkit.getConsoleSender().sendMessage("Found guild: " + guild.getName());
                return guild;
            }
            Bukkit.getConsoleSender().sendMessage("Could not find guild with ID: " + id);
        }

        return null;
    }

    public static Role getRole(long id) {

        if (id != 0) {
            Role role = mainGuild.getRoleById(id);
            if (role != null) {
                Bukkit.getConsoleSender().sendMessage("Found role: " + role.getName());
                return role;
            }
            Bukkit.getConsoleSender().sendMessage("Could not find role with ID: " + id);
        }

        return null;
    }

    // Getters

    public static JDA getJDA() {
        return jda;
    }

    public static Guild getMainGuild() {
        return mainGuild;
    }

    public static ConfigurationSection getBotConfig() {
        return config;
    }

    // Linked channels

    public static void addLinkedChannel(TextChannel channel) {
        if (isLinkedChannel(channel)) {
            return;
        }

        linkedChannels.add(LinkedChannel.create(channel));

        DiscordCraft.instance().getBotConfigManager().saveConfig();
    }

    public static void removeLinkedChannel(TextChannel channel) {
        for (LinkedChannel linkedChannel : linkedChannels) {
            if (linkedChannel.getChannel().getIdLong() == channel.getIdLong()) {

                linkedChannel.delete();
                linkedChannels.remove(linkedChannel);

                DiscordCraft.instance().getBotConfigManager().saveConfig();

                return;
            }
        }
    }

    public static boolean isLinkedChannel(TextChannel channel) {
        for (LinkedChannel linkedChannel : linkedChannels) {
            if (linkedChannel.getChannel().getIdLong() == channel.getIdLong()) {
                return true;
            }
        }

        return false;
    }

    public static List<LinkedChannel> getLinkedChannels() {
        return linkedChannels;
    }

    public static LinkedChannel getLinkedChannel(TextChannel channel) {
        for (LinkedChannel linkedChannel : linkedChannels) {
            if (linkedChannel.getChannel().getIdLong() == channel.getIdLong()) {
                return linkedChannel;
            }
        }

        return null;
    }

    public static boolean isMessageInALinkedChannel(@Nonnull Message message) {
        for (LinkedChannel linkedChannel : linkedChannels) {
            if (linkedChannel.getChannel().getIdLong() == message.getChannel().getIdLong()) {
                return true;
            }
        }
        return false;
    }

    // Log channel

    public static TextChannel getLogChannel() {
        return logChannel;
    }

    // Self user

    public static SelfUser getSelfUser() {
        return jda.getSelfUser();
    }

}
