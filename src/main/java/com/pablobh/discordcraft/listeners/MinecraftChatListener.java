package com.pablobh.discordcraft.listeners;

import java.net.URL;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.pablobh.discordcraft.Discord;
import com.pablobh.discordcraft.LinkedChannel;
import com.pablobh.discordcraft.avatar.AvatarProvider;
import com.pablobh.discordcraft.avatar.AvatarStyle;

import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;

public class MinecraftChatListener implements Listener {

    private AvatarProvider avatarProvider;

    public MinecraftChatListener() {
        this.avatarProvider = new AvatarProvider();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        for (LinkedChannel linkedChannel : Discord.getLinkedChannels()) {
            if (linkedChannel.canSendMinecraftChatMessages()) {
                sendMinecraftMessage(linkedChannel, event.getPlayer(), event.getMessage());
            }
        }
    }

    public void sendMinecraftMessage(LinkedChannel channel, Player player, String message) {
        URL avatarUrl = avatarProvider.getAvatarUrl(player, AvatarStyle.BUST, 128);

        WebhookMessage messageObj = new WebhookMessageBuilder()
            .setUsername(player.getName())
            .setAvatarUrl(avatarUrl.toString())
            .setContent(message)
            .build();

        channel.send(messageObj);
    }

}
