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

package com.pablobh.discordcraft.discord.command;

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
