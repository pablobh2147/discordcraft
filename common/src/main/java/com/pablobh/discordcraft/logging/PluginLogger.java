package com.pablobh.discordcraft.logging;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PluginLogger {

    void info(@Nonnull String message);

    void warning(@Nonnull String message);

    void severe(@Nonnull String message);

    void exception(@Nonnull Exception e, @Nullable String message);

}
