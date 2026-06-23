package com.pablobh.discordcraft.spigot;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.pablobh.discordcraft.DiscordCraft;
import com.pablobh.discordcraft.spigot.config.GlobalConfiguration;
import com.pablobh.discordcraft.spigot.config.SpigotConfiguration;
import com.pablobh.discordcraft.spigot.listeners.MinecraftChatListener;
import com.pablobh.discordcraft.spigot.listeners.PlayerEventsListener;
import com.pablobh.discordcraft.spigot.logging.SpigotLogger;

public class DiscordCraftPlugin extends JavaPlugin {

    private DiscordCraft discordCraft = null;

    @Override
    public void onEnable() {
        SpigotLogger logger = new SpigotLogger(getLogger());

        GlobalConfiguration globalConfiguration = new GlobalConfiguration(this, "config.yml");
        SpigotConfiguration messagesConfig = new SpigotConfiguration(this, "messages.yml");
        SpigotConfiguration botConfig = new SpigotConfiguration(this, "bot.yml");
        SpigotConfiguration commandsConfig = new SpigotConfiguration(this, "commands.yml");

        try {
            discordCraft = new DiscordCraft(logger, globalConfiguration, messagesConfig, botConfig, commandsConfig);
        } catch (LoginException e) {
            logger.severe(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        registerEventListeners(globalConfiguration);

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
        discordCraft.getLogger().info(getDescription().getName() + " v" + getDescription().getVersion() + " has been disabled!");

        discordCraft.shutdown();
    }

    private void registerEventListeners(@Nonnull GlobalConfiguration globalConfiguration) {
        Bukkit.getPluginManager().registerEvents(new MinecraftChatListener(globalConfiguration, discordCraft.getDiscordService()), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEventsListener(discordCraft.getDiscordService(), discordCraft.getMessageService()), this);
    }

}