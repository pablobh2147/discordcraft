package com.pablobh.discordcraft.spigot.discord.commands;

import javax.annotation.Nonnull;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ConfigCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "config";

    private final DiscordCraft discordCraft;

    public ConfigCommand(@Nonnull DiscordCommandManager manager, @Nonnull DiscordCraft discordCraft) {
        super(COMMAND_NAME, manager);
        this.discordCraft = discordCraft;

        addSubcommand("reload", "Reloads the plugin configuration");
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "reload":
                subcommandReload(event);
                break;
            default:
                event.reply(getMessageService().getDiscordMessage("commands.invalid-subcommand").toDiscordMessage()).setEphemeral(true).queue();
                break;
        }
    }

    private void reply(SlashCommandInteractionEvent event, String messageKey, String defaultMessage) {
        event.reply(
            getMessageService().getDiscordMessageOrDefault(
                getMessageKey(messageKey), 
                defaultMessage
            ).toDiscordMessage()
        ).setEphemeral(true).queue();
    }

    private void subcommandReload(SlashCommandInteractionEvent event) {
        boolean success = discordCraft.reloadConfigurations();
        if (!success) {
            reply(event, "reload-error", "Failed to reload configuration");
        } else {
            reply(event, "reload", "Configuration reloaded successfully!");
        }
    }

}
