package com.pablobh.discordcraft.spigot.platform.component;

import com.pablobh.discordcraft.platform.component.MinecraftComponent;

import net.md_5.bungee.api.chat.BaseComponent;

public class SpigotComponent implements MinecraftComponent {

    private final BaseComponent[] components;

    public SpigotComponent(BaseComponent[] components) {
        this.components = components;
    }

    public BaseComponent[] getComponents() {
        return components;
    }

}
