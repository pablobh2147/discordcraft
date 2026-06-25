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
import com.pablobh.discordcraft.message.Message;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class StopServerCommand extends DiscordCommand {

    private static final String COMMAND_NAME = "stop";

    public static final int MINIMUM_DELAY = 5;
    public static final int MAXIMUM_DELAY = 60 * 10; // 10 minutes
    
    private final DiscordCraft discordCraft;

    public StopServerCommand(@Nonnull DiscordCommandManager manager, @Nonnull DiscordCraft discordCraft) {
        super(COMMAND_NAME, manager);
        this.discordCraft = discordCraft;

        addOption(OptionType.INTEGER, "delay", "Delay in seconds", false)
            .setMinValue(MINIMUM_DELAY)
            .setMaxValue(MAXIMUM_DELAY);
    }

    @Override
    public void onCommandInteraction(SlashCommandInteractionEvent event) {

        boolean isEphemeral = getConfig().getBoolean("is-ephemeral", false);
        boolean showTitle = getConfig().getBoolean("show-title", true);

        // Delay
        
        int delay = getConfig().getInt("delay", MINIMUM_DELAY);

        OptionMapping delayOption = event.getOption("delay");
        
        if (delayOption != null) {
            delay = delayOption.getAsInt();
        }

        if (delay < MINIMUM_DELAY) {
            delay = MINIMUM_DELAY;
            getConfig().set("delay", MINIMUM_DELAY);
        }

        if (delay > MAXIMUM_DELAY) {
            delay = MAXIMUM_DELAY;
            getConfig().set("delay", MAXIMUM_DELAY);
        }

        String seconds = String.valueOf(delay);

        // Discord reply
        Message discordMsg = getMessageService().getDiscordMessageOrDefault(getMessageKey("message"), "The server is stopping in %seconds% seconds");
        discordMsg.replace("seconds", seconds);
        event.reply(discordMsg.toDiscordMessage()).setEphemeral(isEphemeral).queue();

        // Minecraft title
        if (showTitle) {
            String title = getMessageService().getPlainMessageOrDefault(getMessageKey("minecraft-title"), "Stopping Server");
            String subtitle = getMessageService().getPlainMessageOrDefault(getMessageKey("minecraft-subtitle"), "The server is stopping in %seconds% seconds");
            subtitle = subtitle.replace("%seconds%", seconds);

            discordCraft.getServer().broadcastTitle(title, subtitle);
        }

        discordCraft.getDiscordLogger().info("Stopping server in " + delay + " seconds, requested by " + event.getUser().getAsMention());

        discordCraft.getServer().runTaskLater(this::stopServer, delay * 20);
    }

    private void stopServer() {
        discordCraft.getServer().shutdown();
    }

}
