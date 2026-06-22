package com.pablobh.discordcraft.spigot.discord.commands;

import javax.annotation.Nonnull;

import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;
import com.pablobh.discordcraft.spigot.DiscordCraft;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ConfigCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "config";

    public ConfigCommand(@Nonnull DiscordCommandManager manager) {
        super(COMMAND_NAME, manager);

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

    private void subcommandReload(SlashCommandInteractionEvent event) {
        DiscordCraft.getInstance().reloadConfig();
        event.reply(getMessageService().getDiscordMessageOrDefault(getMessageKey("reload"), "Configuration reloaded successfully!").toDiscordMessage()).setEphemeral(true).queue();
    }

}
