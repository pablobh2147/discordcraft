package com.pablobh.discordcraft.neoforge.platform;

import com.pablobh.discordcraft.platform.MinecraftAdvancement;

import net.minecraft.advancements.DisplayInfo;

public class NeoForgeAdvancement implements MinecraftAdvancement {

    private final DisplayInfo display;

    public NeoForgeAdvancement(DisplayInfo display) {
        this.display = display;
    }

    @Override
    public String getTitle() {
        return display.getTitle().getString();
    }

    @Override
    public String getDescription() {
        return display.getDescription().getString();
    }

    @Override
    public String getIconMaterial() {
        return display.getIcon().item().getRegisteredName();
    }

}
