package com.pablobh.discordcraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.pablobh.discordcraft.Discord;
import com.pablobh.discordcraft.LinkedChannel;
import com.pablobh.discordcraft.Messages;

public class MinecraftChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = Messages.getMessage("chat.discord-format", "player", event.getPlayer(), "message", event.getMessage());
        
        for (LinkedChannel linkedChannel : Discord.getLinkedChannels()) {
            if (linkedChannel.canSendMinecraftChatMessages()) {
                linkedChannel.getChannel().sendMessage(message).queue();
            }
        }
    }

}
