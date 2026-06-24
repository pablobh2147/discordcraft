package com.pablobh.discordcraft.neoforge.listeners;

import com.pablobh.discordcraft.listener.ChatEventHandler;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;

public class NeoForgeChatAdapter {

    private final ChatEventHandler handler;

    public NeoForgeChatAdapter(ChatEventHandler handler) {
        this.handler = handler;
    }

    @SubscribeEvent
    public void onPlayerChat(ServerChatEvent event) {
        handler.onPlayerChat(
            event.getPlayer().getName().getString(),
            event.getPlayer().getUUID(),
            event.getMessage().getString()
        );
    }

}
