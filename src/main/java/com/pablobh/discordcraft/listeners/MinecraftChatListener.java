package com.pablobh.discordcraft.listeners;

import java.net.URL;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.pablobh.discordcraft.avatar.AvatarProvider;
import com.pablobh.discordcraft.config.GlobalConfiguration;
import com.pablobh.discordcraft.discord.DiscordService;
import com.pablobh.discordcraft.discord.LinkedChannel;

public class MinecraftChatListener implements Listener {

    private static final int AVATAR_SIZE = 128;

    private final GlobalConfiguration globalConfig;
    private final AvatarProvider avatarProvider;
    private final DiscordService discordService;

    public MinecraftChatListener(GlobalConfiguration globalConfig, DiscordService discordService) {
        this.globalConfig = globalConfig;
        this.discordService = discordService;
        this.avatarProvider = new AvatarProvider();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        for (LinkedChannel channel : discordService.getLinkedChannels()) {
            if (channel.canSendMinecraftChatMessages()) {
                URL avatarUrl = avatarProvider.getAvatarUrl(event.getPlayer(), globalConfig.getAvatarStyle(), AVATAR_SIZE);
                channel.sendMessage(event.getPlayer().getName(), avatarUrl, event.getMessage());
            }
        }
    }

}
