package com.pablobh.discordcraft.platform;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MinecraftServer {

    // --------------------- Players ---------------------
    
    @Nullable
    MinecraftPlayer getPlayer(@Nonnull UUID uuid);
    
    @Nullable
    MinecraftPlayer getPlayer(@Nonnull String name);
    
    @Nullable
    MinecraftPlayerProfile getPlayerProfile(@Nonnull UUID uuid);
    
    @Nullable
    MinecraftPlayerProfile getPlayerProfile(@Nonnull String name);
    
    @Nonnull
    Collection<? extends MinecraftPlayer> getOnlinePlayers();

    @Nonnull
    Collection<UUID> getBannedPlayers();

    @Nonnull
    Collection<UUID> getOperators();

    // --------------------- Misc ---------------------

    void executeCommand(@Nonnull String command);
    
    void shutdown();

    // --------------------- Async Tasks ---------------------

    void runTask(@Nonnull Runnable task);

    void runTaskLater(@Nonnull Runnable task, long delayTicks);

    void runTaskAsync(@Nonnull Runnable task);

    // --------------------- Broadcast ---------------------

    void broadcastMessage(@Nonnull String message);

    void broadcastTitle(@Nonnull String title, @Nonnull String subtitle);

    // --------------------- Whitelist ---------------------

    @Nonnull
    Collection<UUID> getWhitelistedPlayers();

    boolean isWhitelistEnabled();

    void setWhitelistEnabled(boolean enabled);

}
