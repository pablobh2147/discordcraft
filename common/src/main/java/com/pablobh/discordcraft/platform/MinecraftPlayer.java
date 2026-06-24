package com.pablobh.discordcraft.platform;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface MinecraftPlayer extends MinecraftPlayerProfile {

    @Nonnull
    UUID getUniqueId();

    @Nonnull
    String getName();

    @Nonnull
    String getDisplayName();

    @Nullable
    InetAddress getAddress();

    void sendMessage(@Nonnull String message);

    default String getIpAddress() {
        InetAddress address = getAddress();
        return address != null ? address.getHostAddress() : null;
    }

    @Nonnull
    @Override
    default Map<String, String> toPlaceholders() {
        Map<String, String> placeholders = MinecraftPlayerProfile.super.toPlaceholders();
        
        placeholders.put("displayname", getDisplayName());
        
        String ip = getIpAddress();
        if (ip != null) {
            placeholders.put("ip", ip);
        }
        
        return placeholders;
    }

}
