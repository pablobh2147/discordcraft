package com.pablobh.discordcraft.platform;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pablobh.discordcraft.message.Placeholdable;

public interface MinecraftPlayer extends Placeholdable {

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
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", getName());
        placeholders.put("displayname", getDisplayName());
        placeholders.put("uuid", getUniqueId().toString());
        
        String ip = getIpAddress();
        if (ip != null) {
            placeholders.put("ip", ip);
        }
        
        return placeholders;
    }
}
