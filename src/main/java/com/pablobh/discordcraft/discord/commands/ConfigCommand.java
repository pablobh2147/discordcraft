package com.pablobh.discordcraft.discord.commands;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.Messages;
import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ConfigCommand extends DiscordCommand {

    public ConfigCommand() {
        super("configuration");

        addSubcommand("reload", "Reloads the plugin configuration");
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "reload":
                subcommandReload(event);
                break;
            default:
                event.reply(Messages.getMessage(DiscordCommandManager.MSG_KEY_COMMAND_INVALID_SUBCOMMAND)).setEphemeral(true).queue();
                break;
        }
    }

    private void subcommandReload(SlashCommandInteractionEvent event) {
        DiscordCraft.instance().reloadConfig();
        event.reply("Configuration reloaded successfully.").setEphemeral(true).queue();
    }

}
