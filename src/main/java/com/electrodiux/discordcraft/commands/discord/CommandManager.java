package com.electrodiux.discordcraft.commands.discord;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.electrodiux.discordcraft.Discord;
import com.electrodiux.discordcraft.DiscordCraft;
import com.electrodiux.discordcraft.Messages;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class CommandManager extends ListenerAdapter {

    // Message keys

    public static final String COMMAND_MAIN_GUILD_ONLY = "commands.main-guild-only";
    public static final String COMMAND_NO_PERMISSION = "commands.no-permission";
    public static final String COMMAND_INTERNAL_ERROR = "commands.internal-error";
    public static final String COMMAND_DISABLED = "commands.disabled";
    public static final String COMMAND_NOT_FOUND = "commands.not-found";
    public static final String COMMAND_INVALID_SUBCOMMAND = "commands.invalid-subcommand";
    

    private List<DiscordCommand> commands = new ArrayList<>();

    public CommandManager() {

        try {
            this.addCommands(new SetupCommand());

            // Add commands here
            this.addCommands(new HelpCommand(this));
            this.addCommands(new PlayerListCommand());
            this.addCommands(new StopServerCommand());
            this.addCommands(new BanCommand());
            this.addCommands(new PardonCommand());
            this.addCommands(new WhitelistCommand());

            // Unify all to one command
            this.addCommands(new ChannelLinkCommand());
        } catch (Exception e) {
            DiscordCraft.logException(e, Messages.getMessage("errors.command-registration-error"));
        }

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        for (DiscordCommand command : commands) {
            if (command.getName().equals(event.getName())) {
                if (command.isEnabled()) {

                    if (command.isAdministratorOnly()) {

                        // Check if the command was executed in the main server

                        if (!command.isGlobal() && !event.getGuild().equals(Discord.getMainGuild())) {
                            event.reply(Messages.getMessage(CommandManager.COMMAND_MAIN_GUILD_ONLY)).setEphemeral(true).queue();
                            return;
                        }

                        // Check if the user has the required permissions
                        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                            event.reply(Messages.getMessage(CommandManager.COMMAND_NO_PERMISSION)).setEphemeral(true).queue();
                            return;
                        }
                    }

                    // Catch any exceptions that occur while executing the command
                    try {
                        // Execute the command
                        command.onCommandInteraction(event);
                        return;
                    } catch (Exception e) {
                        DiscordCraft.discordLogException(e, Messages.getMessage("errors.command-error", "command_name", command.getName(), "member", event.getMember()));
                        event.reply(Messages.getMessage(CommandManager.COMMAND_INTERNAL_ERROR)).setEphemeral(true).queue();
                    }

                    return;
                } else {
                    // Command is disabled
                    event.reply(Messages.getMessage(CommandManager.COMMAND_DISABLED)).setEphemeral(true).queue(); // Should never happen because the command should not be registered
                    return;
                }
            }
        }

        // Command not found
        event.reply(Messages.getMessage(CommandManager.COMMAND_NOT_FOUND)).setEphemeral(true).queue(); // Should never happen because the command should not be registered
    }

    private void registerCommands(Guild guild) {
        // Register commands

        DiscordCraft.logInfo("Registering commands for guild \"" + guild.getName() + "\"");

        List<CommandData> commandDataList = new ArrayList<>();

        for (DiscordCommand command : commands) {

            if (!command.isGlobal() && guild.getIdLong() != Discord.getBotConfig().getLong(Discord.GUILD_ID)) {
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

    public void addCommands(@NotNull DiscordCommand... commands) {
        for (DiscordCommand command : commands) {
            if (command != null) {
                this.commands.add(command);
            }
        }
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

}