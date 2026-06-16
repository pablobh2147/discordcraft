package com.pablobh.discordcraft.discord.commands;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.StringUtils;
import com.pablobh.discordcraft.config.Configuration;
import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;
import com.pablobh.discordcraft.discord.DiscordService;
import com.pablobh.discordcraft.message.MessageService;

import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SetupCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "setup";
    private static final String COMMAND_CONFIG_KEY = "setup";

    private final DiscordService discordService;
    private final MessageService messageService;

    public SetupCommand(@Nonnull DiscordCommandManager manager, @Nonnull DiscordService discordService, @Nonnull MessageService messageService) {
        super(COMMAND_NAME, manager.getCommandConfig(COMMAND_CONFIG_KEY));

        this.discordService = discordService;
        this.messageService = messageService;

        setGlobal(true);

        // Log Channel

        OptionData logChannel = addOption(OptionType.CHANNEL, "log-channel", "A channel to log errors and notify operator information", false);
        logChannel.setChannelTypes(ChannelType.TEXT);
        
        // Activity

        OptionData optionActivityType = addOption(OptionType.STRING, "activity-type", "The type of activity to show in the bot status", false);
        for (ActivityType activityType : ActivityType.values()) {
            if (activityType == ActivityType.CUSTOM_STATUS) {
                continue;
            }
            optionActivityType.addChoice(StringUtils.capitalize(activityType.name()), activityType.name());
        }

        addOption(OptionType.STRING, "activity-name", "The name of the activity to show in the bot status", false);
        addOption(OptionType.BOOLEAN, "show-activity", "Whether to show the activity in the bot status", false);
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {
        
        if (discordService.getMainGuild() != null) {
            event.reply(messageService.getDiscordMessage("setup.already").toDiscordMessage()).setEphemeral(true).queue();
            return;
        }

        Configuration botConfig = DiscordCraft.instance().getBotConfiguration();

        // Set guild

        botConfig.set(DiscordService.GUILD_ID, event.getGuild().getIdLong());

        // Activity

        OptionMapping showActivity = event.getOption("show-activity");
        OptionMapping activityType = event.getOption("activity-type");
        OptionMapping activityName = event.getOption("activity-name");

        if (showActivity != null) {
            botConfig.set(DiscordService.ACTIVITY_ENABLED, showActivity.getAsBoolean());
        }

        if (activityType != null) {
            botConfig.set(DiscordService.ACTIVITY_TYPE, activityType.getAsString());
        }

        if (activityName != null) {
            botConfig.set(DiscordService.ACTIVITY_NAME, activityName.getAsString());
        }

        // Log Channel

        OptionMapping logChannel = event.getOption("log-channel");

        if (logChannel != null) {
            botConfig.set(DiscordService.LOG_CHANNEL, logChannel.getAsChannel().getIdLong());
        }

        botConfig.save();

        
        event.reply(messageService.getDiscordMessage("setup.complete").toDiscordMessage()).setEphemeral(true).queue();

        Bukkit.getScheduler().runTaskLater(DiscordCraft.instance(), () -> Bukkit.shutdown(), 3 * 20); // 3 seconds delay to stop the server, because the bot needs to send the message
    }
    


}