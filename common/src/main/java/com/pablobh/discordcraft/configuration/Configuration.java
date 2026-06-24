package com.pablobh.discordcraft.configuration;

import javax.annotation.Nonnull;

public interface Configuration extends ConfigurationSection {

    boolean save();

    boolean reload();

    @Nonnull
    default <T extends Enum<T>> T getEnum(@Nonnull String path, @Nonnull Class<T> enumClass, @Nonnull T defaultValue) {
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
    
}
