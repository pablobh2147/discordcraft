/*
 * This file is part of DiscordCraft.
 *
 * Copyright (c) 2025 Pablo Bermejo Hernández
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.pablobh.discordcraft.discord;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.configuration.ConfigurationSection;
import com.pablobh.discordcraft.discord.command.BanCommand;
import com.pablobh.discordcraft.discord.command.ChannelLinkCommand;
import com.pablobh.discordcraft.discord.command.ConfigCommand;
import com.pablobh.discordcraft.discord.command.HelpCommand;
import com.pablobh.discordcraft.discord.command.PardonCommand;
import com.pablobh.discordcraft.discord.command.PlayerListCommand;
import com.pablobh.discordcraft.discord.command.SetupCommand;
import com.pablobh.discordcraft.discord.command.StopServerCommand;
import com.pablobh.discordcraft.discord.command.WhitelistCommand;
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

    private final DiscordCraft discordCraft;

    public DiscordCommandManager(@Nonnull DiscordCraft discordCraft) {
        this.discordCraft = discordCraft;
        
        try {
            registerCommand(new SetupCommand(this, discordCraft));
            registerCommand(new HelpCommand(this));
            registerCommand(new PlayerListCommand(this, discordCraft.getServer()));
            registerCommand(new StopServerCommand(this, discordCraft));
            registerCommand(new BanCommand(this, discordCraft.getServer()));
            registerCommand(new PardonCommand(this, discordCraft.getServer()));
            registerCommand(new WhitelistCommand(this, discordCraft.getServer()));
            registerCommand(new ChannelLinkCommand(this, discordCraft));
            registerCommand(new ConfigCommand(this, discordCraft));
        } catch (Exception e) {
            discordCraft.getLogger().exception(e, "Failed to register commands");
        }

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        for (DiscordCommand command : commands) {
            if (command.getName().equals(event.getName())) {
                if (command.isEnabled()) {

                    if (command.isAdministratorOnly()) {

                        // Check if the command was executed in the main server

                        if (!command.isGlobal() && !event.getGuild().equals(discordCraft.getDiscordService().getMainGuild())) {
                            event.reply(discordCraft.getMessageService().getPlainMessage("commands.main-guild-only")).setEphemeral(true).queue();
                            return;
                        }

                        // Check if the user has the required permissions
                        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                            event.reply(discordCraft.getMessageService().getPlainMessage("commands.no-permission")).setEphemeral(true).queue();
                            return;
                        }
                    }

                    // Catch any exceptions that occur while executing the command
                    try {
                        // Execute the command
                        command.onCommandInteraction(event);
                        return;
                    } catch (Exception e) {
                        discordCraft.getLogger().exception(e, "Failed to execute command: " + command.getName());
                        // DiscordCraft.discordLogException(e,
                        //     messageService.getMessage("errors.command-error")
                        //         .replace("command_name", command.getName())
                        //         .replace("member", event.getMember())
                        //         .toString()
                        // );

                        if (event.isAcknowledged()) {
                            event.getHook().sendMessage(discordCraft.getMessageService().getDiscordMessage("commands.internal-error").toDiscordMessage()).queue();
                        } else {
                            event.reply(discordCraft.getMessageService().getDiscordMessage("commands.internal-error").toDiscordMessage()).setEphemeral(true).queue();
                        }
                    }

                    return;
                } else {
                    // Command is disabled
                    // Should never happen because the command should not be registered
                    event.reply(discordCraft.getMessageService().getPlainMessage("commands.disabled")).setEphemeral(true).queue();
                    return;
                }
            }
        }

        // Command not found
        // Should never happen because the command should not be registered
        event.reply(discordCraft.getMessageService().getPlainMessage("commands.not-found")).setEphemeral(true).queue();
    }

    private void registerCommands(Guild guild) {
        // Register commands

        discordCraft.getLogger().info("Registering commands for guild \"" + guild.getName() + "\"");

        List<CommandData> commandDataList = new ArrayList<>();

        for (DiscordCommand command : commands) {

            if (!command.isGlobal() && guild.getIdLong() != discordCraft.getBotConfig().getLong(DiscordService.GUILD_ID)) {
                // Skip if the command is not global and the guild is not the main server
                continue;
            }

            discordCraft.getLogger().info("Registering command: \"" + command.getName() + "\" is enabled: \"" + command.isEnabled() + "\"");

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

    public ConfigurationSection getCommandConfig(String name) {
        String path = "commands." + name;

        ConfigurationSection configuration = discordCraft.getCommandsConfig();

        ConfigurationSection section = configuration.getSection(path);
        if (section == null) {
            configuration.createSection(path);
            section = configuration.getSection(path);
        }

        return section;
    }

    public MessageService getMessageService() {
        return discordCraft.getMessageService();
    }

}