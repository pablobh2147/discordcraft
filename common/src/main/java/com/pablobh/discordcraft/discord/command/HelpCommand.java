package com.pablobh.discordcraft.discord.command;

import javax.annotation.Nonnull;

import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class HelpCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "help";

    private final DiscordCommandManager commandManager;

    public HelpCommand(@Nonnull DiscordCommandManager commandManager) {
        super(COMMAND_NAME, commandManager);
        
        this.commandManager = commandManager;
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {
        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", true);

        String header = getMessageService().getPlainMessageOrDefault(getMessageKey("header"), "Here is the list of commands:");
        String rowFormat = getMessageService().getPlainMessageOrDefault(getMessageKey("row-format"), "- **%command%**: %message%");

        StringBuilder message = new StringBuilder(header);

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
