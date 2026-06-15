package com.pablobh.discordcraft.listeners;

import java.net.URL;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.pablobh.discordcraft.Discord;
import com.pablobh.discordcraft.LinkedChannel;
import com.pablobh.discordcraft.avatar.AvatarProvider;
import com.pablobh.discordcraft.config.GlobalConfiguration;

public class MinecraftChatListener implements Listener {

    private static final int AVATAR_SIZE = 128;

    private final GlobalConfiguration globalConfig;
    private final AvatarProvider avatarProvider;

    public MinecraftChatListener(GlobalConfiguration globalConfig) {
        this.globalConfig = globalConfig;
        this.avatarProvider = new AvatarProvider();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        for (LinkedChannel channel : Discord.getLinkedChannels()) {
            if (channel.canSendMinecraftChatMessages()) {
                URL avatarUrl = avatarProvider.getAvatarUrl(event.getPlayer(), globalConfig.getAvatarStyle(), AVATAR_SIZE);
                channel.sendMessage(event.getPlayer().getName(), avatarUrl, event.getMessage());
            }
        }
    }

}
