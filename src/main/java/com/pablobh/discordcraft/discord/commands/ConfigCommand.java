package com.pablobh.discordcraft.discord.commands;

import javax.annotation.Nonnull;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.discord.DiscordCommand;
import com.pablobh.discordcraft.discord.DiscordCommandManager;
import com.pablobh.discordcraft.message.MessageService;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ConfigCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "config";
    private static final String COMMAND_CONFIG_KEY = "configuration";

    private final MessageService messageService;

    public ConfigCommand(@Nonnull DiscordCommandManager manager, @Nonnull MessageService messageService) {
        super(COMMAND_NAME, manager.getCommandConfig(COMMAND_CONFIG_KEY));

        this.messageService = messageService;

        addSubcommand("reload", "Reloads the plugin configuration");
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "reload":
                subcommandReload(event);
                break;
            default:
                event.reply(messageService.getDiscordMessage("commands.invalid-subcommand").toDiscordMessage()).setEphemeral(true).queue();
                break;
        }
    }

    private void subcommandReload(SlashCommandInteractionEvent event) {
        DiscordCraft.instance().reloadConfig();
        event.reply(messageService.getDiscordMessage("config.reload").toDiscordMessage()).setEphemeral(true).queue();
    }

}
