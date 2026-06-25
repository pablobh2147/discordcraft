package com.pablobh.discordcraft.spigot;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.avatar.AvatarStyle;
import com.pablobh.discordcraft.listener.ChatEventHandler;
import com.pablobh.discordcraft.listener.PlayerEventHandler;
import com.pablobh.discordcraft.spigot.config.SpigotConfiguration;
import com.pablobh.discordcraft.spigot.listeners.SpigotChatAdapter;
import com.pablobh.discordcraft.spigot.listeners.SpigotPlayerEventsAdapter;
import com.pablobh.discordcraft.spigot.logging.SpigotLogger;
import com.pablobh.discordcraft.spigot.platform.SpigotServer;

public class DiscordCraftPlugin extends JavaPlugin {

    private DiscordCraft discordCraft = null;

    @Override
    public void onEnable() {
        SpigotLogger logger = new SpigotLogger(getLogger());

        SpigotConfiguration globalConfig = new SpigotConfiguration(this, "config.yml");
        SpigotConfiguration messagesConfig = new SpigotConfiguration(this, "messages.yml");
        SpigotConfiguration botConfig = new SpigotConfiguration(this, "bot.yml");
        SpigotConfiguration commandsConfig = new SpigotConfiguration(this, "commands.yml");

        SpigotServer spigotServer = new SpigotServer(this);

        try {
            discordCraft = new DiscordCraft(logger, spigotServer, globalConfig, messagesConfig, botConfig, commandsConfig);
        } catch (LoginException e) {
            logger.severe(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        AvatarStyle avatarStyle = globalConfig.getEnum("avatar-style", AvatarStyle.class, AvatarStyle.BUST);
        registerEventListeners(avatarStyle);

        discordCraft.notifyServerStart();
        discordCraft.getLogger().info(getDescription().getName() + " v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        if (discordCraft == null) {
            return;
        }

        discordCraft.notifyServerStop();
        discordCraft.saveConfigurations();
        discordCraft.shutdown();
        discordCraft.getLogger().info(getDescription().getName() + " v" + getDescription().getVersion() + " has been disabled!");
    }

    private void registerEventListeners(@Nonnull AvatarStyle avatarStyle) {
        ChatEventHandler chatHandler = new ChatEventHandler(discordCraft.getDiscordService(), avatarStyle);
        PlayerEventHandler playerHandler = new PlayerEventHandler(discordCraft.getDiscordService(), discordCraft.getMessageService());
        
        Bukkit.getPluginManager().registerEvents(new SpigotChatAdapter(chatHandler), this);
        Bukkit.getPluginManager().registerEvents(new SpigotPlayerEventsAdapter(playerHandler), this);
    }

}