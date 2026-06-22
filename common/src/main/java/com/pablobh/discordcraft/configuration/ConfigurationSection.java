package com.pablobh.discordcraft.configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ConfigurationSection {

    @Nullable
    Object get(@Nonnull String path);

    boolean contains(@Nonnull String path);

    void set(@Nonnull String path, @Nullable Object value);

    // --------------------- Primitive Getters ---------------------

    @Nullable
    String getString(@Nonnull String path);

    @Nullable
    String getString(@Nonnull String path, @Nullable String defaultValue);

    int getInt(@Nonnull String path);

    int getInt(@Nonnull String path, int defaultValue);

    long getLong(@Nonnull String path);

    long getLong(@Nonnull String path, long defaultValue);

    boolean getBoolean(@Nonnull String path);

    boolean getBoolean(@Nonnull String path, boolean defaultValue);

    double getDouble(@Nonnull String path);

    double getDouble(@Nonnull String path, double defaultValue);

    // --------------------- Collection Getters ---------------------

    @Nonnull
    List<String> getStringList(@Nonnull String path);

    @Nonnull
    List<Map<?, ?>> getMapList(@Nonnull String path);

    @Nonnull
    Set<String> getKeys(boolean deep);

    // --------------------- Section Methods ---------------------

    @Nullable
    ConfigurationSection getSection(@Nonnull String path);

    @Nonnull
    ConfigurationSection createSection(@Nonnull String path);

    // --------------------- Type Checks ---------------------

    boolean isString(@Nonnull String path);

    boolean isSection(@Nonnull String path);

    boolean isList(@Nonnull String path);
}
