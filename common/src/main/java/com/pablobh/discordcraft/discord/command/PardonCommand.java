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
import com.pablobh.discordcraft.message.Message;
import com.pablobh.discordcraft.platform.MinecraftPlayerProfile;
import com.pablobh.discordcraft.platform.MinecraftServer;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class PardonCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "pardon";

    private final MinecraftServer minecraftServer;

    public PardonCommand(@Nonnull DiscordCommandManager manager, @Nonnull MinecraftServer minecraftServer) {
        super(COMMAND_NAME, manager);
        this.minecraftServer = minecraftServer;

        addOption(OptionType.STRING, "player", "The player to pardon", true);
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", false);

        String playerName = event.getOption("player").getAsString();

        MinecraftPlayerProfile playerProfile = minecraftServer.getPlayerProfile(playerName);
        if (playerProfile == null) {
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("not-found"), "The player %player% was not found!");
            msg.replace("player", playerName);
            event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
            return;
        }

        if (!playerProfile.isBanned()) {
            Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("not-banned"), "The player %player_name% is not banned!");
            msg.replace("player", playerProfile);
            event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
            return;
        }

        playerProfile.setBanned(false, null);

        Message msg = getMessageService().getDiscordMessageOrDefault(getMessageKey("success"), "The player %player_name% has been unbanned!");
        msg.replace("player", playerProfile);
        event.reply(msg.toDiscordMessage()).setEphemeral(isEphemeral).queue();
    }
    
}
