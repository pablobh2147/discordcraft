package com.pablobh.discordcraft.discord;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.configuration.ConfigurationSection;
import com.pablobh.discordcraft.listener.DiscordChatListener;
import com.pablobh.discordcraft.logging.DiscordLogger;

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
    private final DiscordCraft discordCraft;

    private Guild mainGuild;
    private DiscordLogger discordLogger;

    private List<LinkedChannel> linkedChannels;

    public DiscordService(@Nonnull DiscordCraft discordCraft, @Nonnull String token) throws LoginException {
        this.discordCraft = discordCraft;

        boolean jdaLoaded = initializeJDA(token);
        if (!jdaLoaded) {
            throw new LoginException("Failed to load JDA");
        }

        mainGuild = getGuild(discordCraft.getBotConfig().getLong(GUILD_ID, 0));
        if (mainGuild == null) {
            discordCraft.getLogger().warning("No server was found with the ID provided in the config. Please run /setup command on Discord.");
            linkedChannels = new ArrayList<>();
            return;
        }

        discordLogger = new DiscordLogger(getTextChannel(discordCraft.getBotConfig().getLong(LOG_CHANNEL, 0)));

        linkedChannels = loadChannelLinks();

        discordCraft.getLogger().info("Loaded " + linkedChannels.size() + " linked channels.");
        for (LinkedChannel channel : linkedChannels) {
            discordCraft.getLogger().info("Loaded linked channel: " + channel.getChannel().getName() + " in guild: " + channel.getChannel().getGuild().getName());
        }
    }

    private boolean initializeJDA(String token) {
        try {
            JDABuilder builder = JDABuilder.createDefault(token);

            configureMemoryUsage(builder);
            configureActivity(builder);

            builder.addEventListeners(new DiscordChatListener(this, discordCraft.getMessageService()));
            builder.addEventListeners(new DiscordCommandManager(discordCraft));

            jda = builder.build();
            if (jda == null) {
                throw new LoginException("Couldn't login in to Discord!");
            }

            jda.awaitReady();

            return true;
        } catch (Exception e) {
            discordCraft.getLogger().warning("An error occurred while setting up Discord!");
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
        boolean showActivity = discordCraft.getBotConfig().getBoolean(ACTIVITY_ENABLED, true);

        if (!showActivity) {
            return;
        }

        String activityType = discordCraft.getBotConfig().getString(ACTIVITY_TYPE);
        String activityName = discordCraft.getBotConfig().getString(ACTIVITY_NAME);

        discordCraft.getLogger().info("Activity type: " + activityType);
        discordCraft.getLogger().info("Activity name: " + activityName);

        if (activityName != null) {
            ActivityType type = ActivityType.valueOf(activityType);

            if (type != null) {
                type = ActivityType.PLAYING;
            }

            builder.setActivity(Activity.of(type, activityName));
        }
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
            try {
                if (!jda.awaitShutdown(java.time.Duration.ofSeconds(10))) {
                    jda.shutdownNow();
                }
            } catch (InterruptedException e) {
                jda.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public TextChannel getTextChannel(long id) {
        if (id != 0) {
            TextChannel channel = mainGuild.getTextChannelById(id);
            if (channel != null) {
                discordCraft.getLogger().info("Found text channel: " + channel.getName());
                return channel;
            }
            discordCraft.getLogger().warning("Could not find text channel with ID: " + id);
        }
        return null;
    }

    public VoiceChannel getVoiceChannel(long id) {
        if (id != 0) {
            VoiceChannel channel = mainGuild.getVoiceChannelById(id);
            if (channel != null) {
                discordCraft.getLogger().info("Found voice channel: " + channel.getName());
                return channel;
            }
            discordCraft.getLogger().warning("Could not find voice channel with ID: " + id);
        }
        return null;
    }

    public Category getCategory(long id) {
        if (id != 0) {
            Category category = mainGuild.getCategoryById(id);
            if (category != null) {
                discordCraft.getLogger().info("Found category: " + category.getName());
                return category;
            }
            discordCraft.getLogger().warning("Could not find category with ID: " + id);
        }
        return null;
    }

    public Guild getGuild(long id) {
        if (id != 0) {
            Guild guild = jda.getGuildById(id);
            if (guild != null) {
                discordCraft.getLogger().info("Found guild: " + guild.getName());
                return guild;
            }
            discordCraft.getLogger().warning("Could not find guild with ID: " + id);
        }
        return null;
    }

    public Role getRole(long id) {
        if (id != 0) {
            Role role = mainGuild.getRoleById(id);
            if (role != null) {
                discordCraft.getLogger().info("Found role: " + role.getName());
                return role;
            }
            discordCraft.getLogger().warning("Could not find role with ID: " + id);
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
        if (!isChannelLinked(channel)) {
            linkedChannels.add(createChannelLink(channel));
            discordCraft.getBotConfig().save();
        }
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

    public SelfUser getSelfUser() {
        return jda.getSelfUser();
    }

    // --------------------- Channel Link Management ---------------------

    private String getChannelConfigPath(long channelId) {
        return LinkedChannel.CHANNEL_LIST + ".c" + channelId;
    }

    public LinkedChannel createChannelLink(@Nonnull TextChannel textChannel) {
        Objects.requireNonNull(textChannel, "TextChannel cannot be null");
        
        
        ConfigurationSection defaultConfig = discordCraft.getGlobalConfig().getSection("channel-defaults");
        ConfigurationSection channelConfig = discordCraft.getBotConfig().createSection(getChannelConfigPath(textChannel.getIdLong()));

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

    public void removeChannelLink(@Nonnull LinkedChannel channel) {
        Objects.requireNonNull(channel, "LinkedChannel cannot be null");

        discordCraft.getBotConfig().set(getChannelConfigPath(channel.getChannel().getIdLong()), null);
        linkedChannels.remove(channel);
        discordCraft.getBotConfig().save();

        channel.deleteWebhook();
    }

    private List<LinkedChannel> loadChannelLinks() {
        ConfigurationSection channelsConfig = discordCraft.getBotConfig().getSection(LinkedChannel.CHANNEL_LIST);

        List<LinkedChannel> list = new ArrayList<>();

        if (channelsConfig == null) {
            return list;
        }

        discordCraft.getLogger().info("Loading " + channelsConfig.getKeys(false).size() + " linked channels.");

        for (String key : channelsConfig.getKeys(false)) {
            ConfigurationSection section = channelsConfig.getSection(key);
            if (section == null) {
                continue;
            }

            LinkedChannel channel = loadChannelLinkFromConfig(section);
            if (channel != null) {
                list.add(channel);
            } else {
                discordCraft.getLogger().warning("Failed to load linked channel: " + key);
            }
        }

        return list;
    }

    private LinkedChannel loadChannelLinkFromConfig(@Nonnull ConfigurationSection channelConfig) {
        Objects.requireNonNull(channelConfig, "Config cannot be null");

        long channelId = channelConfig.getLong(LinkedChannel.CHANNEL_ID, 0);
        if (channelId == 0) {
            return null;
        }
        
        TextChannel textChannel = getTextChannel(channelId);
        if (textChannel == null) {
            return null;
        }
        
        ConfigurationSection defaultConfig = discordCraft.getGlobalConfig().getSection("channel-defaults");

        return new LinkedChannel(channelConfig, defaultConfig, textChannel);
    }

    // --------------------- Logging ---------------------

    public DiscordLogger getDiscordLogger() {
        return discordLogger;
    }

}
