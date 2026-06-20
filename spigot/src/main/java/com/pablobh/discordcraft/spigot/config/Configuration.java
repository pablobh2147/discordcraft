package com.pablobh.discordcraft.spigot.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.pablobh.discordcraft.spigot.DiscordCraft;

public class Configuration {

    private FileConfiguration configObj;
    private File configFile;

    public Configuration(@NotNull JavaPlugin plugin, @NotNull String filename) {
        File dataFolder = plugin.getDataFolder();

        if (dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        configFile = new File(dataFolder, filename);

        if (!configFile.exists()) {
            plugin.saveResource(filename, false);
        }
        
        configObj = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultConfigStream = plugin.getResource(filename);
        if (defaultConfigStream != null) {
            YamlConfiguration defaultConfiguration = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
            configObj.setDefaults(defaultConfiguration);
            configObj.options().copyDefaults(true);
            save();
        }
    }

    public ConfigurationSection getConfigObj() {
        return configObj;
    }

    public ConfigurationSection getSection(@NotNull String path) {
        return configObj.getConfigurationSection(path);
    }
    
    public void load() {
        configObj = YamlConfiguration.loadConfiguration(configFile);
    }

    public void save() {
        try {
            configObj.save(configFile);
        } catch (IOException e) {
            DiscordCraft.logException(e, "An error occurred while saving the configuration file.");
        }
    }

    public void set(@NotNull String path, Object value) {
        configObj.set(path, value);
    }

    public boolean contains(@NotNull String path) {
        return configObj.contains(path);
    }

    public Object get(@NotNull String path) {
        return configObj.get(path);
    }

    public String getString(@NotNull String path, @Nullable String defaultValue) {
        return configObj.getString(path, defaultValue);
    }

    public String getString(@NotNull String path) {
        return getString(path, null);
    }

    public int getInt(@NotNull String path, int defaultValue) {
        return configObj.getInt(path, defaultValue);
    }

    public int getInt(@NotNull String path) {
        return getInt(path, 0);
    }

    public long getLong(@NotNull String path, long defaultValue) {
        return configObj.getLong(path, defaultValue);
    }

    public long getLong(@NotNull String path) {
        return getLong(path, 0);
    }

    public boolean getBoolean(@NotNull String path, boolean defaultValue) {
        return configObj.getBoolean(path, defaultValue);
    }
    
    public boolean getBoolean(@NotNull String path) {
        return getBoolean(path, false);
    }

    public <T extends Enum<T>> T getEnum(@NotNull String path, Class<T> enumClass, T defaultValue) {
        String value = getString(path, null);

        if (value == null) {
            return defaultValue;
        }

        try {
            return Enum.valueOf(enumClass, value.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public List<String> getStringList(@NotNull String path) {
        return configObj.getStringList(path);
    }
    
    public ConfigurationSection createSection(@NotNull String path) {
        return configObj.createSection(path);
    }

}
