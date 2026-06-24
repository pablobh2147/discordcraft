package com.pablobh.discordcraft.platform;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pablobh.discordcraft.platform.component.MinecraftComponent;
import com.pablobh.discordcraft.platform.component.MinecraftComponentBuilder;

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

    // --------------------- Player Names ---------------------

    @Nonnull
    Collection<String> getWhitelistedPlayerNames();

    @Nonnull
    Collection<String> getBannedPlayerNames();

    @Nonnull
    Collection<String> getOperatorPlayerNames();

    @Nonnull
    Collection<String> getOnlinePlayerNames();

    // --------------------- Misc ---------------------

    void executeCommand(@Nonnull String command);
    
    void shutdown();

    // --------------------- Async Tasks ---------------------

    void runTask(@Nonnull Runnable task);

    void runTaskLater(@Nonnull Runnable task, long delayTicks);

    void runTaskAsync(@Nonnull Runnable task);

    // --------------------- Broadcast ---------------------

    void broadcastMessage(@Nonnull String message);

    void broadcastComponent(@Nonnull MinecraftComponent component);

    void broadcastTitle(@Nonnull String title, @Nonnull String subtitle);

    @Nonnull
    MinecraftComponentBuilder createComponentBuilder();

    // --------------------- Whitelist ---------------------

    @Nonnull
    Collection<UUID> getWhitelistedPlayers();

    boolean isWhitelistEnabled();

    void setWhitelistEnabled(boolean enabled);

}
