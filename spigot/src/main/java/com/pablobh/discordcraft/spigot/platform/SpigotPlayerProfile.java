package com.pablobh.discordcraft.spigot.platform;

import java.time.Instant;
import java.util.UUID;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerProfile;

import com.pablobh.discordcraft.platform.MinecraftPlayerProfile;

public class SpigotPlayerProfile implements MinecraftPlayerProfile {

    private final org.bukkit.OfflinePlayer offlinePlayer;
   
    public SpigotPlayerProfile(org.bukkit.OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    @Override
    public UUID getUniqueId() {
        return offlinePlayer.getUniqueId();
    }

    @Override
    public String getName() {
        return offlinePlayer.getName();
    }

    @Override
    public boolean isWhitelisted() {
        return offlinePlayer.isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean whitelisted) {
        offlinePlayer.setWhitelisted(whitelisted);
    }

    @Override
    public boolean isBanned() {
        return offlinePlayer.isBanned();
    }

    @Override
    public void setBanned(boolean banned, String reason) {
        BanList<PlayerProfile> profileBanList = Bukkit.getBanList(BanList.Type.PROFILE);
        if (banned) {
            profileBanList.addBan(offlinePlayer.getPlayerProfile(), reason, (Instant) null, reason);
        } else {
            profileBanList.pardon(offlinePlayer.getPlayerProfile());
        }
    }
   
}
