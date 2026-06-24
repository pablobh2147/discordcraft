package com.pablobh.discordcraft.platform;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.pablobh.discordcraft.message.Placeholdable;

public interface MinecraftPlayerProfile extends Placeholdable {

    @Nonnull
    UUID getUniqueId();

    @Nonnull
    String getName();

    boolean isWhitelisted();

    void setWhitelisted(boolean whitelisted);

    @Nonnull
    @Override
    default Map<String, String> toPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", getName());
        placeholders.put("uuid", getUniqueId().toString());    
        return placeholders;
    }
   
}
