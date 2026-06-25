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

package com.pablobh.discordcraft.spigot.platform.component;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;

import com.pablobh.discordcraft.platform.component.ClickAction;
import com.pablobh.discordcraft.platform.component.MinecraftComponent;
import com.pablobh.discordcraft.platform.component.MinecraftComponentBuilder;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

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
