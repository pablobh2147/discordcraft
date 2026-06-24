package com.pablobh.discordcraft.message;

import java.util.Map;

import javax.annotation.Nonnull;

public interface Placeholdable {

    @Nonnull
    Map<String, String> toPlaceholders();
    
}
