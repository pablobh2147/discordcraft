package com.pablobh.discordcraft.platform.component;

import javax.annotation.Nonnull;

public interface MinecraftComponentBuilder {

    @Nonnull
    MinecraftComponentBuilder append(@Nonnull String text);

    @Nonnull
    MinecraftComponentBuilder append(@Nonnull MinecraftComponent component);

    @Nonnull
    MinecraftComponentBuilder setClickEvent(@Nonnull ClickAction action, @Nonnull String value);

    @Nonnull
    MinecraftComponent build();

}
