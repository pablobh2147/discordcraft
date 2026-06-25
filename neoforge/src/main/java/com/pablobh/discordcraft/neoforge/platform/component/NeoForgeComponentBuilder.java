package com.pablobh.discordcraft.neoforge.platform.component;

import javax.annotation.Nonnull;

import com.pablobh.discordcraft.platform.component.ClickAction;
import com.pablobh.discordcraft.platform.component.MinecraftComponent;
import com.pablobh.discordcraft.platform.component.MinecraftComponentBuilder;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class NeoForgeComponentBuilder implements MinecraftComponentBuilder {

    private MutableComponent component;

    public NeoForgeComponentBuilder() {
        this.component = Component.empty();
    }

    @Nonnull
    @Override
    public MinecraftComponentBuilder append(@Nonnull String text) {
        Component parsedComponent = NeoForgeComponentParser.parse(MiniMessage.miniMessage().deserialize(text));
        component = component.append(parsedComponent);
        return this;
    }

    @Nonnull
    @Override
    public MinecraftComponentBuilder append(@Nonnull MinecraftComponent component) {
        if (component instanceof NeoForgeComponent neoForgeComponent) {
            this.component = this.component.append(neoForgeComponent.getComponent());
        }
        return this;
    }

    @Nonnull
    @Override
    public MinecraftComponentBuilder setClickEvent(@Nonnull ClickAction action, @Nonnull String value) {
        ClickEvent clickEvent = switch (action) {
            case OPEN_URL -> new ClickEvent.OpenUrl(java.net.URI.create(value));
            case RUN_COMMAND -> new ClickEvent.RunCommand(value);
            case SUGGEST_COMMAND -> new ClickEvent.SuggestCommand(value);
        };
        component = component.withStyle(style -> style.withClickEvent(clickEvent));
        return this;
    }

    @Nonnull
    @Override
    public MinecraftComponent build() {
        return new NeoForgeComponent(component);
    }

}
