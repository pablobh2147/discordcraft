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
