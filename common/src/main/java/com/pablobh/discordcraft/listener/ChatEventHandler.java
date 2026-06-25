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

import java.net.URL;
import java.util.UUID;

import com.pablobh.discordcraft.avatar.AvatarProvider;
import com.pablobh.discordcraft.avatar.AvatarStyle;
import com.pablobh.discordcraft.discord.DiscordService;
import com.pablobh.discordcraft.discord.LinkedChannel;

public class ChatEventHandler {

    private static final int AVATAR_SIZE = 128;

    private final AvatarProvider avatarProvider;
    private final DiscordService discordService;
    private final AvatarStyle avatarStyle;

    public ChatEventHandler(DiscordService discordService, AvatarStyle avatarStyle) {
        this.discordService = discordService;
        this.avatarStyle = avatarStyle;
        this.avatarProvider = new AvatarProvider();
    }

    public void onPlayerChat(String playerName, UUID playerUUID, String message) {
        URL avatarUrl = avatarProvider.getAvatarUrl(playerUUID, avatarStyle, AVATAR_SIZE);
        for (LinkedChannel channel : discordService.getLinkedChannels()) {
            if (channel.canSendMinecraftChatMessages()) {
                channel.sendMessage(playerName, avatarUrl, message);
            }
        }
    }

}
