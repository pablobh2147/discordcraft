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
