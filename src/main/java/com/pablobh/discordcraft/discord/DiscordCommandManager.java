package com.pablobh.discordcraft.discord;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.config.Configuration;
import com.pablobh.discordcraft.discord.commands.BanCommand;
import com.pablobh.discordcraft.discord.commands.ChannelLinkCommand;
import com.pablobh.discordcraft.discord.commands.ConfigCommand;
import com.pablobh.discordcraft.discord.commands.HelpCommand;
import com.pablobh.discordcraft.discord.commands.PardonCommand;
import com.pablobh.discordcraft.discord.commands.PlayerListCommand;
import com.pablobh.discordcraft.discord.commands.SetupCommand;
import com.pablobh.discordcraft.discord.commands.StopServerCommand;
import com.pablobh.discordcraft.discord.commands.WhitelistCommand;
import com.pablobh.discordcraft.message.MessageService;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class DiscordCommandManager extends ListenerAdapter {
    
    private List<DiscordCommand> commands = new ArrayList<>();

    private final Configuration config;
    
    private final DiscordService discordService;
    private final MessageService messageService;

    public DiscordCommandManager(DiscordService discordService, MessageService messageService, Configuration config) {
        this.config = config;

        this.discordService = discordService;
        this.messageService = messageService;
        
        try {
            registerCommand(new SetupCommand(this, discordService));
            registerCommand(new HelpCommand(this));
            registerCommand(new PlayerListCommand(this));
            registerCommand(new StopServerCommand(this));
            registerCommand(new BanCommand(this));
            registerCommand(new PardonCommand(this));
            registerCommand(new WhitelistCommand(this));
            registerCommand(new ChannelLinkCommand(this, discordService));
            registerCommand(new ConfigCommand(this));
        } catch (Exception e) {
            DiscordCraft.logException(e, messageService.getPlainMessage("errors.command-registration-error"));
        }

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        for (DiscordCommand command : commands) {
            if (command.getName().equals(event.getName())) {
                if (command.isEnabled()) {

                    if (command.isAdministratorOnly()) {

                        // Check if the command was executed in the main server

                        if (!command.isGlobal() && !event.getGuild().equals(discordService.getMainGuild())) {
                            event.reply(messageService.getPlainMessage("commands.main-guild-only")).setEphemeral(true).queue();
                            return;
                        }

                        // Check if the user has the required permissions
                        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                            event.reply(messageService.getPlainMessage("commands.no-permission")).setEphemeral(true).queue();
                            return;
                        }
                    }

                    // Catch any exceptions that occur while executing the command
                    try {
                        // Execute the command
                        command.onCommandInteraction(event);
                        return;
                    } catch (Exception e) {
                        
                        DiscordCraft.discordLogException(e,
                            messageService.getMessage("errors.command-error")
                                .replace("command_name", command.getName())
                                .replace("member", event.getMember())
                                .toString()
                        );

                        event.reply(messageService.getDiscordMessage("commands.internal-error").toDiscordMessage()).setEphemeral(true).queue();
                    }

                    return;
                } else {
                    // Command is disabled
                    event.reply(messageService.getPlainMessage("commands.disabled")).setEphemeral(true).queue(); // Should never happen because the command should not be registered
                    return;
                }
            }
        }

        // Command not found
        event.reply(messageService.getPlainMessage("commands.not-found")).setEphemeral(true).queue(); // Should never happen because the command should not be registered
    }

    private void registerCommands(Guild guild) {
        // Register commands

        DiscordCraft.logInfo("Registering commands for guild \"" + guild.getName() + "\"");

        List<CommandData> commandDataList = new ArrayList<>();

        for (DiscordCommand command : commands) {

            if (!command.isGlobal() && guild.getIdLong() != discordService.getBotConfig().getLong(DiscordService.GUILD_ID)) {
                // Skip if the command is not global and the guild is not the main server
                continue;
            }

            DiscordCraft.logInfo("Registering command: \"" + command.getName() + "\" is enabled: \"" + command.isEnabled() + "\"");

            if (command.isEnabled()) {
                SlashCommandData data = Commands.slash(command.getName(), command.getDescription());

                if (command.hasOptions()) {
                    data.addOptions(command.getOptions());
                }

                if (command.hasSubcommands()) {
                    data.addSubcommands(command.getSubcommands());
                }

                commandDataList.add(data);
            }
        }

        guild.updateCommands().addCommands(commandDataList).queue();

    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        registerCommands(event.getGuild());
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        registerCommands(event.getGuild());
    }

    // Command management

    public void registerCommand(@NotNull DiscordCommand command) {
        Objects.requireNonNull(command, "Command cannot be null");
        commands.add(command);
    }

    @Nullable
    public List<DiscordCommand> getCommands() {
        return commands;
    }

    @Nullable
    public DiscordCommand getCommand(String name) {
        for (DiscordCommand command : commands) {
            if (command.getName().equals(name)) {
                return command;
            }
        }

        return null;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public ConfigurationSection getCommandConfig(String name) {
        String key = "commands." + name;

        ConfigurationSection section = config.getSection(key);
        if (section == null) {
            config.createSection(key);
            section = config.getSection(key);
        }

        return section;
    }

}