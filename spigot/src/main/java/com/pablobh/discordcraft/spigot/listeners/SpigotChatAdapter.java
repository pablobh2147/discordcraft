package com.pablobh.discordcraft.spigot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.pablobh.discordcraft.listener.ChatEventHandler;

public class SpigotChatAdapter implements Listener {

    private final ChatEventHandler handler;

    public SpigotChatAdapter(ChatEventHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        handler.onPlayerChat(
            event.getPlayer().getName(),
            event.getPlayer().getUniqueId(),
            event.getMessage()
        );
    }

}
