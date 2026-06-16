package com.pablobh.discordcraft.discord;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.commands.discord.CommandManager;
import com.pablobh.discordcraft.config.Configuration;
import com.pablobh.discordcraft.listeners.DiscordChatListener;

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

public class DiscordService {

    public static final String BOT_TOKEN = "token";

    public static final String GUILD_ID = "guild";
    public static final String LOG_CHANNEL = "log-channel";

    public static final String ACTIVITY_ENABLED = "activity.show";
    public static final String ACTIVITY_TYPE = "activity.type";
    public static final String ACTIVITY_NAME = "activity.name";

    private JDA jda;

    private Guild mainGuild;

    private Configuration config;

    private List<LinkedChannel> linkedChannels;

    private TextChannel logChannel;

    public boolean setup(Configuration config) {
        this.config = config;

        String token = config.getString(BOT_TOKEN);

        if (token == null || token.isBlank()) {
            DiscordCraft.logWarning("No bot token was found in the config! Please add one and restart the server.");
            return false;
        }

        boolean settedUp = setupJDA(token);

        if (!settedUp) {
            DiscordCraft.logWarning("An error occurred while setting up Discord JDA!");
            return false;
        }

        mainGuild = getGuild(config.getLong(GUILD_ID, 0));

        if (mainGuild == null) {
            DiscordCraft.logWarning("No server was found with the ID provided in the config! Please check the ID or run /setup command on Discord.");
        }

        linkedChannels = loadChannelLinks();

        logChannel = getTextChannel(config.getLong(LOG_CHANNEL, 0));

        DiscordCraft.logInfo("Loaded " + linkedChannels.size() + " linked channels.");
        for (LinkedChannel linkedChannel : linkedChannels) {
            DiscordCraft.logInfo("Loaded linked channel: " + linkedChannel.getChannel().getName() + " in guild: " + linkedChannel.getChannel().getGuild().getName());
        }

        return true;
    }

    private void configureMemoryUsage(JDABuilder builder) {
        builder.disableCache(CacheFlag.ACTIVITY);

        builder.enableIntents(GatewayIntent.GUILD_MESSAGES);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.enableIntents(GatewayIntent.GUILD_WEBHOOKS);
    }

    private void configureActivity(JDABuilder builder) {
        boolean showActivity = config.getBoolean(ACTIVITY_ENABLED, true);

        if (!showActivity) {
            return;
        }

        String activityType = config.getString(ACTIVITY_TYPE);
        String activityName = config.getString(ACTIVITY_NAME);

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

    private boolean setupJDA(String token) {
        try {
            JDABuilder builder = JDABuilder.createDefault(token);

            configureMemoryUsage(builder);
            configureActivity(builder);

            builder.addEventListeners(new DiscordChatListener(this));
            builder.addEventListeners(new CommandManager(this));

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

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }

    public TextChannel getTextChannel(long id) {

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

    public VoiceChannel getVoiceChannel(long id) {

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

    public Category getCategory(long id) {

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

    public Guild getGuild(long id) {

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

    public Role getRole(long id) {

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

    public JDA getJDA() {
        return jda;
    }

    public Guild getMainGuild() {
        return mainGuild;
    }

    public void addLinkedChannel(TextChannel channel) {
        if (isLinkedChannel(channel)) {
            return;
        }

        linkedChannels.add(createChannelLink(channel));

        DiscordCraft.instance().getBotConfiguration().save();
    }

    public boolean isLinkedChannel(TextChannel channel) {
        for (LinkedChannel linkedChannel : linkedChannels) {
            if (linkedChannel.getChannel().getIdLong() == channel.getIdLong()) {
                return true;
            }
        }

        return false;
    }

    public List<LinkedChannel> getLinkedChannels() {
        return linkedChannels;
    }

    public LinkedChannel getLinkedChannel(TextChannel channel) {
        for (LinkedChannel linkedChannel : linkedChannels) {
            if (linkedChannel.getChannel().getIdLong() == channel.getIdLong()) {
                return linkedChannel;
            }
        }

        return null;
    }

    public boolean isMessageInALinkedChannel(@Nonnull Message message) {
        for (LinkedChannel linkedChannel : linkedChannels) {
            if (linkedChannel.getChannel().getIdLong() == message.getChannel().getIdLong()) {
                return true;
            }
        }
        return false;
    }

    public TextChannel getLogChannel() {
        return logChannel;
    }

    public SelfUser getSelfUser() {
        return jda.getSelfUser();
    }

    public Configuration getConfig() {
        return config;
    }

    // --------------------- Channel Link Management ---------------------

    private String getChannelConfigPath(long channelId) {
        return LinkedChannel.CHANNEL_LIST + ".c" + channelId;
    }

    public LinkedChannel createChannelLink(@NonNull TextChannel textChannel) {
        Objects.requireNonNull(textChannel, "TextChannel cannot be null");
        
        ConfigurationSection defaultConfig = DiscordCraft.instance().getGlobalConfiguration().getSection(LinkedChannel.DEFAULT_OPTIONS);
        ConfigurationSection channelConfig = config.createSection(getChannelConfigPath(textChannel.getIdLong()));

        return new LinkedChannel(channelConfig, defaultConfig, textChannel);
    }

    public void removeChannelLink(@Nonnull TextChannel textChannel) {
        Objects.requireNonNull(textChannel, "TextChannel cannot be null");
        
        LinkedChannel channel = getLinkedChannel(textChannel);
        if (channel == null) {
            return;
        }
        
        removeChannelLink(channel);
    }

    public void removeChannelLink(@NonNull LinkedChannel channel) {
        Objects.requireNonNull(channel, "LinkedChannel cannot be null");

        config.set(getChannelConfigPath(channel.getChannel().getIdLong()), null);
        linkedChannels.remove(channel);
        config.save();

        channel.deleteWebhook();
    }

    private List<LinkedChannel> loadChannelLinks() {
        ConfigurationSection channelsConfig = config.getSection(LinkedChannel.CHANNEL_LIST);

        List<LinkedChannel> list = new ArrayList<>();

        if (channelsConfig == null) {
            return list;
        }

        DiscordCraft.logInfo("Loading " + channelsConfig.getKeys(false).size() + " linked channels.");

        for (String key : channelsConfig.getKeys(false)) {
            ConfigurationSection section = channelsConfig.getConfigurationSection(key);
            if (section == null) {
                continue;
            }

            LinkedChannel channel = loadChannelLinkFromConfig(section);
            if (channel != null) {
                list.add(channel);
            } else {
                DiscordCraft.logWarning("Failed to load linked channel: " + key);
            }
        }

        return list;
    }

    private LinkedChannel loadChannelLinkFromConfig(@NonNull ConfigurationSection channelConfig) {
        Objects.requireNonNull(channelConfig, "Config cannot be null");

        long channelId = channelConfig.getLong(LinkedChannel.CHANNEL_ID, 0);
        if (channelId == 0) {
            return null;
        }
        
        TextChannel textChannel = getTextChannel(channelId);
        if (textChannel == null) {
            return null;
        }
        
        ConfigurationSection defaultConfig = DiscordCraft.instance().getGlobalConfiguration().getSection(LinkedChannel.DEFAULT_OPTIONS);

        return new LinkedChannel(channelConfig, defaultConfig, textChannel);
    }

}
