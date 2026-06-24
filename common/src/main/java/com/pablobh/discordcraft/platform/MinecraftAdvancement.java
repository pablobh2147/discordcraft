package com.pablobh.discordcraft.platform;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.pablobh.discordcraft.message.Placeholdable;

public interface MinecraftAdvancement extends Placeholdable {

    @Nonnull
    String getTitle();

    @Nonnull
    String getDescription();

    @Nonnull
    String getIconMaterial();

    @Nonnull
    @Override
    default Map<String, String> toPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("title", getTitle());
        placeholders.put("icon", getIconMaterial());
        placeholders.put("description", getDescription());
        return placeholders;
    }
   
}
