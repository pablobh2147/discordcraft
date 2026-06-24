package com.pablobh.discordcraft.neoforge.platform;

import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import com.pablobh.discordcraft.platform.MinecraftPlayerProfile;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.server.players.UserWhiteListEntry;

public class NeoForgePlayerProfile implements MinecraftPlayerProfile {

    private final GameProfile profile;
    private final NameAndId nameAndId;
    private final MinecraftServer server;

    public NeoForgePlayerProfile(GameProfile profile, MinecraftServer server) {
        this.profile = profile;
        this.nameAndId = new NameAndId(profile);
        this.server = server;
    }

    public NeoForgePlayerProfile(NameAndId nameAndId, MinecraftServer server) {
        this.profile = null;
        this.nameAndId = nameAndId;
        this.server = server;
    }

    @Override
    public UUID getUniqueId() {
        return nameAndId.id();
    }

    @Override
    public String getName() {
        return nameAndId.name();
    }

    @Override
    public boolean isWhitelisted() {
        return server.getPlayerList().getWhiteList().isWhiteListed(nameAndId);
    }

    @Override
    public void setWhitelisted(boolean whitelisted) {
        if (whitelisted) {
            server.getPlayerList().getWhiteList().add(new UserWhiteListEntry(nameAndId));
        } else {
            server.getPlayerList().getWhiteList().remove(nameAndId);
        }
    }

    @Override
    public boolean isBanned() {
        return server.getPlayerList().getBans().isBanned(nameAndId);
    }

    @Override
    public void setBanned(boolean banned, @Nullable String reason) {
        if (banned) {
            UserBanListEntry entry = new UserBanListEntry(
                nameAndId,
                null,
                "DiscordCraft",
                null,
                reason != null ? reason : "Banned by DiscordCraft"
            );
            server.getPlayerList().getBans().add(entry);
        } else {
            server.getPlayerList().getBans().remove(nameAndId);
        }
    }

}
