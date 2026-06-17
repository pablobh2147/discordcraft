package com.pablobh.discordcraft.discord.commands;

import javax.annotation.Nonnull;

import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;
import com.pablobh.discordcraft.message.MessageService;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class HelpCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "help";

    private final DiscordCommandManager commandManager;
    private final MessageService messageService;

    public HelpCommand(@Nonnull DiscordCommandManager commandManager, @Nonnull MessageService messageService) {
        super(COMMAND_NAME, commandManager.getCommandConfig(COMMAND_NAME));
        
        this.commandManager = commandManager;
        this.messageService = messageService;
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {
        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", true);

        String header = messageService.getPlainMessageOrDefault("commands.help.header", "Here is the list of commands:");
        String rowFormat = messageService.getPlainMessageOrDefault("commands.help.row-format", "- **%command%**: %message%");

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
