package com.pablobh.discordcraft.neoforge.platform.component;

import com.pablobh.discordcraft.platform.component.MinecraftComponent;

import net.minecraft.network.chat.Component;

public class NeoForgeComponent implements MinecraftComponent {

    private final Component component;

    public NeoForgeComponent(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

}
