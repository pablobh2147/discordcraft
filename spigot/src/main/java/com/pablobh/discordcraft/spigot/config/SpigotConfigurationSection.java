package com.pablobh.discordcraft.spigot.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pablobh.discordcraft.configuration.ConfigurationSection;

public class SpigotConfigurationSection implements ConfigurationSection {

    private final org.bukkit.configuration.ConfigurationSection handle;

    public SpigotConfigurationSection(@Nonnull org.bukkit.configuration.ConfigurationSection handle) {
        this.handle = handle;
    }

    @Nonnull
    public org.bukkit.configuration.ConfigurationSection getHandle() {
        return handle;
    }

    // --------------------- Core Methods ---------------------

    @Nullable
    @Override
    public Object get(@Nonnull String path) {
        return handle.get(path);
    }

    @Override
    public boolean contains(@Nonnull String path) {
        return handle.contains(path);
    }

    @Override
    public void set(@Nonnull String path, @Nullable Object value) {
        handle.set(path, value);
    }

    // --------------------- Primitive Getters ---------------------

    @Nullable
    @Override
    public String getString(@Nonnull String path) {
        return handle.getString(path);
    }

    @Nullable
    @Override
    public String getString(@Nonnull String path, @Nullable String defaultValue) {
        return handle.getString(path, defaultValue);
    }

    @Override
    public int getInt(@Nonnull String path) {
        return handle.getInt(path);
    }

    @Override
    public int getInt(@Nonnull String path, int defaultValue) {
        return handle.getInt(path, defaultValue);
    }

    @Override
    public long getLong(@Nonnull String path) {
        return handle.getLong(path);
    }

    @Override
    public long getLong(@Nonnull String path, long defaultValue) {
        return handle.getLong(path, defaultValue);
    }

    @Override
    public boolean getBoolean(@Nonnull String path) {
        return handle.getBoolean(path);
    }

    @Override
    public boolean getBoolean(@Nonnull String path, boolean defaultValue) {
        return handle.getBoolean(path, defaultValue);
    }

    @Override
    public double getDouble(@Nonnull String path) {
        return handle.getDouble(path);
    }

    @Override
    public double getDouble(@Nonnull String path, double defaultValue) {
        return handle.getDouble(path, defaultValue);
    }

    // --------------------- Collection Getters ---------------------

    @Nonnull
    @Override
    public List<String> getStringList(@Nonnull String path) {
        return handle.getStringList(path);
    }

    @Nonnull
    @Override
    public List<Map<?, ?>> getMapList(@Nonnull String path) {
        return handle.getMapList(path);
    }

    @Nonnull
    @Override
    public Set<String> getKeys(boolean deep) {
        return handle.getKeys(deep);
    }

    // --------------------- Section Methods ---------------------

    @Nullable
    @Override
    public ConfigurationSection getSection(@Nonnull String path) {
        org.bukkit.configuration.ConfigurationSection section = handle.getConfigurationSection(path);
        return section != null ? new SpigotConfigurationSection(section) : null;
    }

    @Nonnull
    @Override
    public ConfigurationSection createSection(@Nonnull String path) {
        return new SpigotConfigurationSection(handle.createSection(path));
    }

    // --------------------- Type Checks ---------------------

    @Override
    public boolean isString(@Nonnull String path) {
        return handle.isString(path);
    }

    @Override
    public boolean isSection(@Nonnull String path) {
        return handle.isConfigurationSection(path);
    }

    @Override
    public boolean isList(@Nonnull String path) {
        return handle.isList(path);
    }
}
