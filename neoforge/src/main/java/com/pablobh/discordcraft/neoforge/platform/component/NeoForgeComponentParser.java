package com.pablobh.discordcraft.neoforge.platform.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public class NeoForgeComponentParser {

    public static Component parse(net.kyori.adventure.text.Component component) {
        String json = net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(component);
        return jsonToComponent(json);
    }
   
    public static Component jsonToComponent(String json) {
        JsonElement jsonElement = JsonParser.parseString(json);
        return ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow();
    }
    
}
