package com.pablobh.discordcraft.spigot.platform;

import org.bukkit.advancement.AdvancementDisplay;

import com.pablobh.discordcraft.platform.MinecraftAdvancement;

public class SpigotAdvancement implements MinecraftAdvancement {

    private final AdvancementDisplay display;

    public SpigotAdvancement(AdvancementDisplay display) {
        this.display = display;
    }

    @Override
    public String getTitle() {
        return display.getTitle();
    }
    
    @Override
    public String getDescription() {
        return display.getDescription();
    }

    @Override
    public String getIconMaterial() {
        return display.getIcon().getType().name();
    }

}
