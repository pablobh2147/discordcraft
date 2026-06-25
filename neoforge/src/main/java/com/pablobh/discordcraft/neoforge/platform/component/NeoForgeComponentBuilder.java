/*
 * This file is part of DiscordCraft.
 *
 * Copyright (c) 2025 Pablo Bermejo Hernández
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
