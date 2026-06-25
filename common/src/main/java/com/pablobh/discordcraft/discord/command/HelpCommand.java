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
