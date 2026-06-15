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
        for (LinkedChannel channel : Discord.getLinkedChannels()) {
            if (channel.canSendMinecraftChatMessages()) {
                URL avatarUrl = avatarProvider.getAvatarUrl(event.getPlayer(), AvatarStyle.BUST, 128);
                channel.sendMessage(event.getPlayer().getName(), avatarUrl, event.getMessage());
            }
        }
    }

}
