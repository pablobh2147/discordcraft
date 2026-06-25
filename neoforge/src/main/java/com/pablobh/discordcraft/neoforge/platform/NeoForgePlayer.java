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

import java.net.InetAddress;
import java.util.UUID;

import javax.annotation.Nullable;

import com.pablobh.discordcraft.neoforge.platform.component.NeoForgeComponentParser;
import com.pablobh.discordcraft.platform.MinecraftPlayer;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.server.players.UserWhiteListEntry;

public class NeoForgePlayer implements MinecraftPlayer {

    private final ServerPlayer player;

    public NeoForgePlayer(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public UUID getUniqueId() {
        return player.getUUID();
    }

    @Override
    public String getName() {
        return player.getName().getString();
    }

    @Override
    public String getDisplayName() {
        return player.getDisplayName().getString();
    }

    @Nullable
    @Override
    public InetAddress getAddress() {
        if (player.connection != null && player.connection.getRemoteAddress() != null) {
            return ((java.net.InetSocketAddress) player.connection.getRemoteAddress()).getAddress();
        }
        return null;
    }

    @Override
    public void sendMessage(String message) {
        Component component = NeoForgeComponentParser.parse(MiniMessage.miniMessage().deserialize(message));
        player.sendSystemMessage(component);
    }

    private NameAndId getNameAndId() {
        return new NameAndId(player.getGameProfile());
    }

    @Override
    public boolean isWhitelisted() {
        return player.level().getServer().getPlayerList().getWhiteList().isWhiteListed(getNameAndId());
    }

    @Override
    public void setWhitelisted(boolean whitelisted) {
        NameAndId nameAndId = getNameAndId();
        if (whitelisted) {
            player.level().getServer().getPlayerList().getWhiteList().add(new UserWhiteListEntry(nameAndId));
        } else {
            player.level().getServer().getPlayerList().getWhiteList().remove(nameAndId);
        }
    }

    @Override
    public boolean isBanned() {
        return player.level().getServer().getPlayerList().getBans().isBanned(getNameAndId());
    }

    @Override
    public void setBanned(boolean banned, @Nullable String reason) {
        NameAndId nameAndId = getNameAndId();
        if (banned) {
            UserBanListEntry entry = new UserBanListEntry(
                nameAndId,
                null,
                "DiscordCraft",
                null,
                reason != null ? reason : "Banned by DiscordCraft"
            );
            player.level().getServer().getPlayerList().getBans().add(entry);
        } else {
            player.level().getServer().getPlayerList().getBans().remove(nameAndId);
        }
    }

}
