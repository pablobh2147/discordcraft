package com.pablobh.discordcraft.spigot.platform;

import java.net.InetAddress;
import java.time.Instant;
import java.util.UUID;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerProfile;

import com.pablobh.discordcraft.platform.MinecraftPlayer;

public class SpigotPlayer implements MinecraftPlayer {

    private final org.bukkit.entity.Player player;

    public SpigotPlayer(org.bukkit.entity.Player player) {
        this.player = player;
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public String getDisplayName() {
        return player.getDisplayName();
    }

    @Override
    public InetAddress getAddress() {
        return player.getAddress().getAddress();
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public boolean isWhitelisted() {
        return player.isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean whitelisted) {
        player.setWhitelisted(whitelisted);
    }

    @Override
    public boolean isBanned() {
        return player.isBanned();
    }

    @Override
    public void setBanned(boolean banned, String reason) {
        BanList<PlayerProfile> profileBanList = Bukkit.getBanList(BanList.Type.PROFILE);
        if (banned) {
            profileBanList.addBan(player.getPlayerProfile(), reason, (Instant) null, reason);
        } else {
            profileBanList.pardon(player.getPlayerProfile());
        }
    }
   
}
