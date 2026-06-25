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
