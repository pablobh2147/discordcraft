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

    private List<LinkedChannel> linkedChannels;

    private TextChannel logChannel;

    private Configuration botConfig;
    private Configuration commandConfig;

    public DiscordService(@NonNull String token, @NonNull Configuration botConfig, @NonNull Configuration commandConfig) throws LoginException {
        this.botConfig = botConfig;
        this.commandConfig = commandConfig;

        boolean jdaLoaded = setupJDA(token);
        if (!jdaLoaded) {
            throw new LoginException("Failed to load JDA");
        }

        mainGuild = getGuild(botConfig.getLong(GUILD_ID, 0));
        if (mainGuild == null) {
            throw new LoginException("No server was found with the ID provided in the config! Please check the ID or run /setup command on Discord.");
        }

        logChannel = getTextChannel(botConfig.getLong(LOG_CHANNEL, 0));

        linkedChannels = loadChannelLinks();

        DiscordCraft.logInfo("Loaded " + linkedChannels.size() + " linked channels.");
        for (LinkedChannel channel : linkedChannels) {
            DiscordCraft.logInfo("Loaded linked channel: " + channel.getChannel().getName() + " in guild: " + channel.getChannel().getGuild().getName());
        }
    }

    private boolean setupJDA(String token) {
        try {
            JDABuilder builder = JDABuilder.createDefault(token);

            configureMemoryUsage(builder);
            configureActivity(builder);

            builder.addEventListeners(new DiscordChatListener(this));
            builder.addEventListeners(new DiscordCommandManager(this, commandConfig));

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

    private void configureMemoryUsage(JDABuilder builder) {
        builder.disableCache(CacheFlag.ACTIVITY);

        builder.enableIntents(GatewayIntent.GUILD_MESSAGES);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.enableIntents(GatewayIntent.GUILD_WEBHOOKS);
    }

    private void configureActivity(JDABuilder builder) {
        boolean showActivity = botConfig.getBoolean(ACTIVITY_ENABLED, true);

        if (!showActivity) {
            return;
        }

        String activityType = botConfig.getString(ACTIVITY_TYPE);
        String activityName = botConfig.getString(ACTIVITY_NAME);

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
        if (isChannelLinked(channel)) {
            return;
        }

        linkedChannels.add(createChannelLink(channel));

        DiscordCraft.instance().getBotConfiguration().save();
    }

    public List<LinkedChannel> getLinkedChannels() {
        return linkedChannels;
    }

    public LinkedChannel getLinkedChannel(TextChannel textChannel) {
        for (LinkedChannel channel : linkedChannels) {
            if (channel.getChannel().getIdLong() == textChannel.getIdLong()) {
                return channel;
            }
        }
        return null;
    }

    public boolean isChannelLinked(TextChannel textChannel) {
        return linkedChannels.stream().anyMatch(channel -> channel.getChannel().getIdLong() == textChannel.getIdLong());
    }

    public boolean isMessageInALinkedChannel(@Nonnull Message message) {
        return linkedChannels.stream().anyMatch(channel -> channel.getChannel().getIdLong() == message.getChannel().getIdLong());
    }

    public TextChannel getLogChannel() {
        return logChannel;
    }

    public SelfUser getSelfUser() {
        return jda.getSelfUser();
    }

    public Configuration getConfig() {
        return botConfig;
    }

    // --------------------- Channel Link Management ---------------------

    private String getChannelConfigPath(long channelId) {
        return LinkedChannel.CHANNEL_LIST + ".c" + channelId;
    }

    public LinkedChannel createChannelLink(@NonNull TextChannel textChannel) {
        Objects.requireNonNull(textChannel, "TextChannel cannot be null");
        
        ConfigurationSection defaultConfig = DiscordCraft.instance().getGlobalConfiguration().getSection(LinkedChannel.DEFAULT_OPTIONS);
        ConfigurationSection channelConfig = botConfig.createSection(getChannelConfigPath(textChannel.getIdLong()));

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

        botConfig.set(getChannelConfigPath(channel.getChannel().getIdLong()), null);
        linkedChannels.remove(channel);
        botConfig.save();

        channel.deleteWebhook();
    }

    private List<LinkedChannel> loadChannelLinks() {
        ConfigurationSection channelsConfig = botConfig.getSection(LinkedChannel.CHANNEL_LIST);

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
