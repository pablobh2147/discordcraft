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
