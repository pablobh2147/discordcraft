package com.pablobh.discordcraft.spigot.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.pablobh.discordcraft.configuration.Configuration;
import com.pablobh.discordcraft.configuration.ConfigurationSection;

public class SpigotConfiguration implements Configuration {

    private FileConfiguration configObj;
    private File configFile;

    public SpigotConfiguration(@Nonnull JavaPlugin plugin, @Nonnull String filename) {
        File dataFolder = plugin.getDataFolder();

        if (!dataFolder.exists()) {
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

    @Nonnull
    public FileConfiguration getHandle() {
        return configObj;
    }

    // --------------------- Configuration Interface ---------------------

    @Override
    public boolean save() {
        try {
            configObj.save(configFile);
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save configuration file: " + configFile.getAbsolutePath());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void reload() {
        configObj = YamlConfiguration.loadConfiguration(configFile);
    }

    // --------------------- Core Methods ---------------------

    @Nullable
    @Override
    public Object get(@Nonnull String path) {
        return configObj.get(path);
    }

    @Override
    public boolean contains(@Nonnull String path) {
        return configObj.contains(path);
    }

    @Override
    public void set(@Nonnull String path, @Nullable Object value) {
        configObj.set(path, value);
    }

    // --------------------- Primitive Getters ---------------------

    @Nullable
    @Override
    public String getString(@Nonnull String path) {
        return configObj.getString(path);
    }

    @Nullable
    @Override
    public String getString(@Nonnull String path, @Nullable String defaultValue) {
        return configObj.getString(path, defaultValue);
    }

    @Override
    public int getInt(@Nonnull String path) {
        return configObj.getInt(path);
    }

    @Override
    public int getInt(@Nonnull String path, int defaultValue) {
        return configObj.getInt(path, defaultValue);
    }

    @Override
    public long getLong(@Nonnull String path) {
        return configObj.getLong(path);
    }

    @Override
    public long getLong(@Nonnull String path, long defaultValue) {
        return configObj.getLong(path, defaultValue);
    }

    @Override
    public boolean getBoolean(@Nonnull String path) {
        return configObj.getBoolean(path);
    }

    @Override
    public boolean getBoolean(@Nonnull String path, boolean defaultValue) {
        return configObj.getBoolean(path, defaultValue);
    }

    @Override
    public double getDouble(@Nonnull String path) {
        return configObj.getDouble(path);
    }

    @Override
    public double getDouble(@Nonnull String path, double defaultValue) {
        return configObj.getDouble(path, defaultValue);
    }

    // --------------------- Collection Getters ---------------------

    @Nonnull
    @Override
    public List<String> getStringList(@Nonnull String path) {
        return configObj.getStringList(path);
    }

    @Nonnull
    @Override
    public List<Map<?, ?>> getMapList(@Nonnull String path) {
        return configObj.getMapList(path);
    }

    @Nonnull
    @Override
    public Set<String> getKeys(boolean deep) {
        return configObj.getKeys(deep);
    }

    // --------------------- Section Methods ---------------------

    @Nullable
    @Override
    public ConfigurationSection getSection(@Nonnull String path) {
        org.bukkit.configuration.ConfigurationSection section = configObj.getConfigurationSection(path);
        return section != null ? new SpigotConfigurationSection(section) : null;
    }

    @Nonnull
    @Override
    public ConfigurationSection createSection(@Nonnull String path) {
        return new SpigotConfigurationSection(configObj.createSection(path));
    }

    // --------------------- Type Checks ---------------------

    @Override
    public boolean isString(@Nonnull String path) {
        return configObj.isString(path);
    }

    @Override
    public boolean isSection(@Nonnull String path) {
        return configObj.isConfigurationSection(path);
    }

    @Override
    public boolean isList(@Nonnull String path) {
        return configObj.isList(path);
    }
}
