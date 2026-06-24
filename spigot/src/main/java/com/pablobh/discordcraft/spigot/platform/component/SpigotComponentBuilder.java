package com.pablobh.discordcraft.spigot.platform.component;

import javax.annotation.Nonnull;

import com.pablobh.discordcraft.platform.component.ClickAction;
import com.pablobh.discordcraft.platform.component.MinecraftComponent;
import com.pablobh.discordcraft.platform.component.MinecraftComponentBuilder;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;

public class SpigotComponentBuilder implements MinecraftComponentBuilder {

    private final ComponentBuilder builder;

    public SpigotComponentBuilder() {
        this.builder = new ComponentBuilder("");
    }

    @Nonnull
    @Override
    public MinecraftComponentBuilder append(@Nonnull String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        builder.append(text, ComponentBuilder.FormatRetention.NONE);
        return this;
    }

    @Nonnull
    @Override
    public MinecraftComponentBuilder append(@Nonnull MinecraftComponent component) {
        if (component instanceof SpigotComponent spigotComponent) {
            builder.append(spigotComponent.getComponents());
        }
        return this;
    }

    @Nonnull
    @Override
    public MinecraftComponentBuilder setClickEvent(@Nonnull ClickAction action, @Nonnull String value) {
        ClickEvent.Action spigotAction = switch (action) {
            case OPEN_URL -> ClickEvent.Action.OPEN_URL;
            case RUN_COMMAND -> ClickEvent.Action.RUN_COMMAND;
            case SUGGEST_COMMAND -> ClickEvent.Action.SUGGEST_COMMAND;
        };
        builder.event(new ClickEvent(spigotAction, value));
        return this;
    }

    @Nonnull
    @Override
    public MinecraftComponent build() {
        return new SpigotComponent(builder.create());
    }

}
