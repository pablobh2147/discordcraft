/*
 * This file is part of DiscordCraft.
 *
 * Copyright (c) 2025 Pablo Bermejo Hernández
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.pablobh.discordcraft.neoforge.platform;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pablobh.discordcraft.neoforge.platform.component.NeoForgeComponent;
import com.pablobh.discordcraft.neoforge.platform.component.NeoForgeComponentBuilder;
import com.pablobh.discordcraft.neoforge.platform.component.NeoForgeComponentParser;
import com.pablobh.discordcraft.platform.MinecraftPlayer;
import com.pablobh.discordcraft.platform.MinecraftPlayerProfile;
import com.pablobh.discordcraft.platform.component.MinecraftComponent;
import com.pablobh.discordcraft.platform.component.MinecraftComponentBuilder;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserWhiteList;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class NeoForgeServer implements com.pablobh.discordcraft.platform.MinecraftServer {

    @Nullable
    @Override
    public MinecraftPlayer getPlayer(@Nonnull UUID uuid) {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                return new NeoForgePlayer(player);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public MinecraftPlayer getPlayer(@Nonnull String name) {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            ServerPlayer player = server.getPlayerList().getPlayerByName(name);
            if (player != null) {
                return new NeoForgePlayer(player);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public MinecraftPlayerProfile getPlayerProfile(@Nonnull UUID uuid) {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            NameAndId nameAndId = server.services().nameToIdCache().get(uuid).orElse(null);
            if (nameAndId != null) {
                return new NeoForgePlayerProfile(nameAndId, server);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public MinecraftPlayerProfile getPlayerProfile(@Nonnull String name) {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            NameAndId nameAndId = server.services().nameToIdCache().get(name).orElse(null);
            if (nameAndId != null) {
                return new NeoForgePlayerProfile(nameAndId, server);
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public Collection<? extends MinecraftPlayer> getOnlinePlayers() {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return server.getPlayerList().getPlayers().stream()
                .map(NeoForgePlayer::new)
                .collect(Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    @Nonnull
    @Override
    public Collection<UUID> getBannedPlayers() {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            UserBanList banList = server.getPlayerList().getBans();
            return banList.getEntries().stream()
                .map(entry -> entry.getUser().id())
                .collect(Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    @Nonnull
    @Override
    public Collection<UUID> getOperators() {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return server.getPlayerList().getOps().getEntries().stream()
                .map(entry -> entry.getUser().id())
                .collect(Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    @Nonnull
    @Override
    public Collection<String> getWhitelistedPlayerNames() {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            UserWhiteList whitelist = server.getPlayerList().getWhiteList();
            return whitelist.getEntries().stream()
                .map(entry -> entry.getUser().name())
                .collect(Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    @Nonnull
    @Override
    public Collection<String> getBannedPlayerNames() {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            UserBanList banList = server.getPlayerList().getBans();
            return banList.getEntries().stream()
                .map(entry -> entry.getUser().name())
                .collect(Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    @Nonnull
    @Override
    public Collection<String> getOperatorPlayerNames() {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return server.getPlayerList().getOps().getEntries().stream()
                .map(entry -> entry.getUser().name())
                .collect(Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    @Nonnull
    @Override
    public Collection<String> getOnlinePlayerNames() {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return server.getPlayerList().getPlayers().stream()
                .map(player -> player.getName().getString())
                .collect(Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    @Override
    public void executeCommand(@Nonnull String command) {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            CommandSourceStack source = server.createCommandSourceStack();
            server.getCommands().performPrefixedCommand(source, command);
        }
    }

    @Override
    public void shutdown() {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.halt(false);
        }
    }

    @Override
    public void runTask(@Nonnull Runnable task) {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.execute(task);
        }
    }

    @Override
    public void runTaskLater(@Nonnull Runnable task, long delayTicks) {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.execute(() -> {
                try {
                    Thread.sleep(delayTicks * 50);
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    @Override
    public void runTaskAsync(@Nonnull Runnable task) {
        new Thread(task).start();
    }

    @Override
    public void broadcastMessage(@Nonnull String message) {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            Component component = NeoForgeComponentParser.parse(MiniMessage.miniMessage().deserialize(message));
            server.getPlayerList().broadcastSystemMessage(component, false);
        }
    }

    @Override
    public void broadcastComponent(@Nonnull MinecraftComponent component) {
        if (component instanceof NeoForgeComponent neoForgeComponent) {
            net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                server.getPlayerList().broadcastSystemMessage(neoForgeComponent.getComponent(), false);
            }
        }
    }

    @Override
    public void broadcastTitle(@Nonnull String title, @Nonnull String subtitle) {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            Component titleComponent = NeoForgeComponentParser.parse(MiniMessage.miniMessage().deserialize(title));
            Component subtitleComponent = NeoForgeComponentParser.parse(MiniMessage.miniMessage().deserialize(subtitle));
            
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                player.sendSystemMessage(titleComponent);
                player.sendSystemMessage(subtitleComponent);
            }
        }
    }

    @Nonnull
    @Override
    public MinecraftComponentBuilder createComponentBuilder() {
        return new NeoForgeComponentBuilder();
    }

    @Nonnull
    @Override
    public Collection<UUID> getWhitelistedPlayers() {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            UserWhiteList whitelist = server.getPlayerList().getWhiteList();
            return whitelist.getEntries().stream()
                .map(entry -> entry.getUser().id())
                .collect(Collectors.toList());
        }
        return java.util.Collections.emptyList();
    }

    @Override
    public boolean isWhitelistEnabled() {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server != null && server.getPlayerList().isUsingWhitelist();
    }

    @Override
    public void setWhitelistEnabled(boolean enabled) {
        net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server instanceof DedicatedServer dedicatedServer) {
            dedicatedServer.setUsingWhitelist(enabled);
        }
    }

}
