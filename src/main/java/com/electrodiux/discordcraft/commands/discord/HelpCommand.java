package com.electrodiux.discordcraft.commands.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class HelpCommand extends DiscordCommand {

    private CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        super("help");
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
