package com.pablobh.discordcraft.platform;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MinecraftServer {

    @Nonnull
    Collection<? extends MinecraftPlayer> getOnlinePlayers();

    @Nullable
    MinecraftPlayer getPlayer(@Nonnull UUID uuid);

    @Nullable
    MinecraftPlayer getOfflinePlayer(@Nonnull UUID uuid);

    @Nonnull
    Collection<UUID> getWhitelistedPlayers();

    @Nonnull
    Collection<UUID> getBannedPlayers();

    @Nonnull
    Collection<UUID> getOperators();

    void executeCommand(@Nonnull String command);

    void runTask(@Nonnull Runnable task);

    void runTaskLater(@Nonnull Runnable task, long delayTicks);

    void runTaskAsync(@Nonnull Runnable task);

    void broadcastMessage(@Nonnull String message);
}
