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

package com.pablobh.discordcraft.neoforge;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.avatar.AvatarStyle;
import com.pablobh.discordcraft.listener.ChatEventHandler;
import com.pablobh.discordcraft.listener.PlayerEventHandler;
import com.pablobh.discordcraft.neoforge.config.NeoForgeConfiguration;
import com.pablobh.discordcraft.neoforge.listeners.NeoForgeChatAdapter;
import com.pablobh.discordcraft.neoforge.listeners.NeoForgePlayerEventsAdapter;
import com.pablobh.discordcraft.neoforge.logging.NeoForgeLogger;
import com.pablobh.discordcraft.neoforge.platform.NeoForgeServer;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod("discordcraft")
public class DiscordCraftMod {

    private DiscordCraft discordCraft = null;
    private NeoForgeLogger logger;

    private NeoForgeChatAdapter chatAdapter;
    private NeoForgePlayerEventsAdapter playerAdapter;

    public DiscordCraftMod(IEventBus modEventBus) {
        modEventBus.addListener(this::onServerStarted);
        NeoForge.EVENT_BUS.addListener(this::onServerStopping);
    }

    private void onServerStarted(@Nonnull FMLCommonSetupEvent event) {
        logger = new NeoForgeLogger();

        NeoForgeConfiguration globalConfig = new NeoForgeConfiguration("config.yml");
        NeoForgeConfiguration messagesConfig = new NeoForgeConfiguration("messages.yml");
        NeoForgeConfiguration botConfig = new NeoForgeConfiguration("bot.yml");
        NeoForgeConfiguration commandsConfig = new NeoForgeConfiguration("commands.yml");

        NeoForgeServer neoForgeServer = new NeoForgeServer();

        try {
            discordCraft = new DiscordCraft(logger, neoForgeServer, globalConfig, messagesConfig, botConfig, commandsConfig);
        } catch (LoginException e) {
            logger.severe(e.getMessage());
            return;
        }

        AvatarStyle avatarStyle = globalConfig.getEnum("avatar-style", AvatarStyle.class, AvatarStyle.BUST);
        registerEventListeners(avatarStyle);

        discordCraft.notifyServerStart();
        logger.info("DiscordCraft v2.0.0 has been enabled!");
    }

    private void registerEventListeners(@Nonnull AvatarStyle avatarStyle) {
        ChatEventHandler chatHandler = new ChatEventHandler(discordCraft.getDiscordService(), avatarStyle);
        PlayerEventHandler playerHandler = new PlayerEventHandler(discordCraft.getDiscordService(), discordCraft.getMessageService());
        
        chatAdapter = new NeoForgeChatAdapter(chatHandler);
        playerAdapter = new NeoForgePlayerEventsAdapter(playerHandler);

        NeoForge.EVENT_BUS.register(chatAdapter);
        NeoForge.EVENT_BUS.register(playerAdapter);
    }

    private void onServerStopping(ServerStoppingEvent event) {
        if (discordCraft == null) {
            return;
        }

        discordCraft.notifyServerStop();

        if (chatAdapter != null) NeoForge.EVENT_BUS.unregister(chatAdapter);
        if (playerAdapter != null) NeoForge.EVENT_BUS.unregister(playerAdapter);

        discordCraft.saveConfigurations();
        discordCraft.shutdown();
        logger.info("DiscordCraft v2.0.0 has been disabled!");
    }

}
