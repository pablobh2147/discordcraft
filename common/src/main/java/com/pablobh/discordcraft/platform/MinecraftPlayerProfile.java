package com.pablobh.discordcraft.platform;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pablobh.discordcraft.message.Placeholdable;

public interface MinecraftPlayerProfile extends Placeholdable {

    @Nonnull
    UUID getUniqueId();

    @Nonnull
    String getName();

    boolean isWhitelisted();

    void setWhitelisted(boolean whitelisted);

    boolean isBanned();

    void setBanned(boolean banned, @Nullable String reason);

    @Nonnull
    @Override
    default Map<String, String> toPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", getName());
        placeholders.put("uuid", getUniqueId().toString());    
        return placeholders;
    }
   
}
