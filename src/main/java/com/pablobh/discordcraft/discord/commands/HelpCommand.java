package com.pablobh.discordcraft.discord.commands;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class HelpCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "help";
    private static final String COMMAND_CONFIG_KEY = "help";

    private DiscordCommandManager commandManager;

    public HelpCommand(@NonNull DiscordCommandManager commandManager) {
        super(COMMAND_NAME, commandManager.getCommandConfig(COMMAND_CONFIG_KEY));
        
        this.commandManager = commandManager;
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {
        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", true);
        String rowFormat = getConfig().getString("row-format", "- %command%: %message%");

        StringBuilder message = new StringBuilder(
            getConfig().getString("message", "List of available commands:")
        );

        for (DiscordCommand command : commandManager.getCommands()) {
            if (command.isEnabled()) {
                String helpMessage = command.getHelp();

                if (helpMessage == null || helpMessage.isBlank()) {
                    continue; // Skip commands without help messages
                }

                message.append("\n").append(rowFormat.replace("%command%", command.getName()).replace("%message%", helpMessage));
            }
        }

        event.reply(message.toString()).setEphemeral(isEphemeral).queue();
    }
}
