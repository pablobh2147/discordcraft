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

package com.pablobh.discordcraft.listener;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nullable;

import com.pablobh.discordcraft.discord.DiscordService;
import com.pablobh.discordcraft.discord.LinkedChannel;
import com.pablobh.discordcraft.message.Message;
import com.pablobh.discordcraft.message.MessageService;
import com.pablobh.discordcraft.platform.MinecraftAdvancement;
import com.pablobh.discordcraft.platform.MinecraftPlayer;

public class PlayerEventHandler {

    private static final String ADVANCEMENT_API_ENDPOINT = "https://minecraft-api.com/api/achivements/{block}/{title}/{desc}";

    private final DiscordService discordService;
    private final MessageService messageService;

    public PlayerEventHandler(DiscordService discordService, MessageService messageService) {
        this.discordService = discordService;
        this.messageService = messageService;
    }

    public void onPlayerJoin(MinecraftPlayer player, boolean firstJoin) {
        Message message = messageService.getDiscordMessage("player.join");

        if (firstJoin) {
            Message firstJoinMessage = messageService.getDiscordMessage("player.welcome");
            if (firstJoinMessage != null) {
                message = firstJoinMessage;
            }
        }

        if (message == null) {
            return;
        }

        message.replace("player", player);

        for (LinkedChannel linkedChannel : discordService.getLinkedChannels()) {
            if (linkedChannel.canSendPlayerJoinMessages()) {
                linkedChannel.sendMessage(message);
            }
        }
    }

    public void onPlayerQuit(MinecraftPlayer player) {
        Message message = messageService.getDiscordMessage("player.left");

        if (message == null) {
            return;
        }

        message.replace("player", player);

        for (LinkedChannel linkedChannel : discordService.getLinkedChannels()) {
            if (linkedChannel.canSendPlayerLeaveMessages()) {
                linkedChannel.sendMessage(message);
            }
        }
    }

    public void onPlayerDeath(MinecraftPlayer player, String deathMessage, @Nullable String damageCause) {
        Message customDeathMessage = null;
        if (damageCause != null) {
            String deathMessageKey = "custom-death-messages." + damageCause.toLowerCase();
            customDeathMessage = messageService.getDiscordMessage(deathMessageKey);
        }

        String deathMessageStr = deathMessage;

        if (customDeathMessage != null) {
            customDeathMessage.replace("player", player);
            customDeathMessage.replace("death_message", deathMessageStr);
            deathMessageStr = customDeathMessage.toString();
        }

        Message finalDeathMessage = messageService.getDiscordMessage("player.death");
        String finalDeathMessageStr = deathMessageStr;

        if (finalDeathMessage != null) {
            finalDeathMessage.replace("player", player);
            finalDeathMessage.replace("death_message", deathMessageStr);
            finalDeathMessageStr = finalDeathMessage.toString();
        } else {
            finalDeathMessageStr = deathMessageStr;
        }

        for (LinkedChannel channel : discordService.getLinkedChannels()) {
            if (channel.canSendPlayerDeathMessages()) {
                if (finalDeathMessage != null) {
                    channel.sendMessage(finalDeathMessage);
                } else {
                    channel.sendMessage(finalDeathMessageStr);
                }
            }
        }
    }

    public void onPlayerKillPlayer(MinecraftPlayer killer, MinecraftPlayer victim) {
        Message message = messageService.getDiscordMessage("player.murder");

        if (message == null) {
            return;
        }

        message.replace("killer", killer);
        message.replace("victim", victim);

        for (LinkedChannel channel : discordService.getLinkedChannels()) {
            if (channel.canSendPlayerMurderMessages()) {
                channel.sendMessage(message.toString());
            }
        }
    }

    public void onPlayerAdvancement(MinecraftPlayer player, MinecraftAdvancement advancement) {
        Message message = messageService.getDiscordMessage("player.achivement-unlock");

        if (message == null) {
            return;
        }

        message.replace("advancement", advancement);
        message.replace("player", player);
        message.replace("display_url", getAdvancementDisplayURL(advancement));

        for (LinkedChannel channel : discordService.getLinkedChannels()) {
            if (channel.canSendPlayerAdvancementMessages()) {
                channel.sendMessage(message);
            }
        }
    }

    private String getAdvancementDisplayURL(MinecraftAdvancement advancement) {
        String block = advancement.getIconMaterial().toLowerCase();
        String title = "Advancement..Made";
        String name = URLEncoder.encode(advancement.getTitle().replace(" ", ".."), StandardCharsets.UTF_8);

        String url = ADVANCEMENT_API_ENDPOINT
            .replace("{block}", block)
            .replace("{title}", title)
            .replace("{desc}", name);
        
        return url;
    }

}
