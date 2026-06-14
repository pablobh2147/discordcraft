package com.electrodiux.discordcraft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {

    private FileConfiguration config;
    private File configFile;

    public ConfigManager(String file, boolean defaults) {
        File dataFolder = DiscordCraft.instance().getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        configFile = new File(dataFolder, file);

        if (!configFile.exists()) {
            DiscordCraft.instance().saveResource(file, false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        if (defaults) {
            InputStream defaultConfigStream = DiscordCraft.instance().getResource(file);
            if (defaultConfigStream != null) {
                YamlConfiguration defaultConfiguration = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
                config.setDefaults(defaultConfiguration);
                config.options().copyDefaults(true); // This copies the default values to the config file if they are not present
                this.saveConfig();
            }
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public ConfigurationSection getSection(String path) {
        return config.getConfigurationSection(path);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            DiscordCraft.logException(e, Messages.getMessage("errors.config-save-error"));
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

}
