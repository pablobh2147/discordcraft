package com.pablobh.discordcraft.spigot.platform;

import java.util.Collection;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.pablobh.discordcraft.platform.MinecraftPlayer;
import com.pablobh.discordcraft.platform.MinecraftPlayerProfile;
import com.pablobh.discordcraft.platform.MinecraftServer;
import com.pablobh.discordcraft.platform.component.MinecraftComponent;
import com.pablobh.discordcraft.platform.component.MinecraftComponentBuilder;
import com.pablobh.discordcraft.spigot.platform.component.SpigotComponent;
import com.pablobh.discordcraft.spigot.platform.component.SpigotComponentBuilder;

public class SpigotServer implements MinecraftServer {

    private final JavaPlugin plugin;

    public SpigotServer(@Nonnull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // --------------------- Players ---------------------

    @Nullable
    @Override
    public MinecraftPlayer getPlayer(@Nonnull UUID uuid) {
        var player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return new SpigotPlayer(player);
        }
        return null;
    }

    @Nullable
    @Override
    public MinecraftPlayer getPlayer(@Nonnull String name) {
        var player = Bukkit.getPlayer(name);
        if (player != null) {
            return new SpigotPlayer(player);
        }
        return null;
    }

    @Nullable
    @Override
    public MinecraftPlayerProfile getPlayerProfile(@Nonnull UUID uuid) {
        var offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.getUniqueId() != null) {
            return new SpigotPlayerProfile(offlinePlayer);
        }
        return null;
    }

    @Nullable
    @Override
    public MinecraftPlayerProfile getPlayerProfile(@Nonnull String name) {
        @SuppressWarnings("deprecation")
        var offlinePlayer = Bukkit.getOfflinePlayer(name);
        if (offlinePlayer.getUniqueId() != null) {
            return new SpigotPlayerProfile(offlinePlayer);
        }
        return null;
    }

    @Nonnull
    @Override
    public Collection<? extends MinecraftPlayer> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream()
            .map(SpigotPlayer::new)
            .toList();
    }

    @Nonnull
    @Override
    public Collection<UUID> getBannedPlayers() {
        return Bukkit.getBannedPlayers().stream()
            .map(player -> player.getUniqueId())
            .toList();
    }

    @Nonnull
    @Override
    public Collection<UUID> getOperators() {
        return Bukkit.getOperators().stream()
            .map(op -> op.getUniqueId())
            .toList();
    }

    // --------------------- Player Names ---------------------

    @Nonnull
    @Override
    public Collection<String> getWhitelistedPlayerNames() {
        return Bukkit.getWhitelistedPlayers().stream()
            .map(player -> player.getName())
            .toList();
    }

    @Nonnull
    @Override
    public Collection<String> getBannedPlayerNames() {
        return Bukkit.getBannedPlayers().stream()
            .map(player -> player.getName())
            .toList();
    }

    @Nonnull
    @Override
    public Collection<String> getOperatorPlayerNames() {
        return Bukkit.getOperators().stream()
            .map(op -> op.getName())
            .toList();
    }

    @Nonnull
    @Override
    public Collection<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
            .map(player -> player.getName())
            .toList();
    }

    // --------------------- Misc ---------------------

    @Override
    public void executeCommand(@Nonnull String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    @Override
    public void shutdown() {
        Bukkit.shutdown();
    }

    // --------------------- Async Tasks ---------------------

    @Override
    public void runTask(@Nonnull Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

    @Override
    public void runTaskLater(@Nonnull Runnable task, long delayTicks) {
        Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    @Override
    public void runTaskAsync(@Nonnull Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    // --------------------- Broadcast ---------------------

    @Override
    public void broadcastMessage(@Nonnull String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        Bukkit.broadcastMessage(message);
    }

    @Override
    public void broadcastComponent(@Nonnull MinecraftComponent component) {
        if (component instanceof SpigotComponent spigotComponent) {
            for (var player : Bukkit.getOnlinePlayers()) {
                player.spigot().sendMessage(spigotComponent.getComponents());
            }
            Bukkit.getConsoleSender().spigot().sendMessage(spigotComponent.getComponents());
        }
    }

    @Nonnull
    @Override
    public MinecraftComponentBuilder createComponentBuilder() {
        return new SpigotComponentBuilder();
    }

    @Override
    public void broadcastTitle(@Nonnull String title, @Nonnull String subtitle) {
        final int FADE_IN_TICKS = 10;
        final int DISPLAY_TICKS = 60;
        final int FADE_OUT_TICKS = 10;

        title = ChatColor.translateAlternateColorCodes('&', title);
        subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
        for (var player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, subtitle, FADE_IN_TICKS, DISPLAY_TICKS, FADE_OUT_TICKS);
        }
    }

    // --------------------- Whitelist ---------------------

    @Nonnull
    @Override
    public Collection<UUID> getWhitelistedPlayers() {
        return Bukkit.getWhitelistedPlayers().stream()
            .map(player -> player.getUniqueId())
            .toList();
    }

    @Override
    public boolean isWhitelistEnabled() {
        return Bukkit.hasWhitelist();
    }

    @Override
    public void setWhitelistEnabled(boolean enabled) {
        Bukkit.setWhitelist(enabled);
    }

}
