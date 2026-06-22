package com.pablobh.discordcraft.spigot.message;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.OfflinePlayer;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.entity.Player;

public final class SpigotPlaceholder {
   
    private SpigotPlaceholder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    @Nonnull
    public static Map<String, String> player(@Nonnull OfflinePlayer player) {
        Map<String, String> replacers = new HashMap<>();
        replacers.put("name", player.getName());
        replacers.put("uuid", player.getUniqueId().toString());
        return replacers;
    }

    @Nonnull
    public static Map<String, String> player(@Nonnull Player player) {
        Map<String, String> replacers = new HashMap<>();
        replacers.put("name", player.getName());
        replacers.put("displayname", player.getDisplayName());
        replacers.put("uuid", player.getUniqueId().toString());
        replacers.put("ip", player.getAddress().getAddress().getHostAddress());
        return replacers;
    }

    @Nonnull
    public static Map<String, String> advancement(@Nonnull AdvancementDisplay display) {
        Map<String, String> replacers = new HashMap<>();
        replacers.put("title", display.getTitle());
        replacers.put("description", display.getDescription());
        replacers.put("icon", display.getIcon().getType().name());
        return replacers;
    }

}
