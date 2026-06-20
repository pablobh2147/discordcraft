package com.pablobh.discordcraft.spigot.message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.md_5.bungee.api.ChatColor;

public class StringMessage extends Message {

    private String content;

    public StringMessage(@Nonnull String content) {
        this.content = content;
    }

    @Override
    public Message formatMinecraftColors() {
        this.content = ChatColor.translateAlternateColorCodes(ALTERNATE_COLOR_CHAR, this.content);
        return this;
    }

    @Override
    public Message replace(@Nonnull String key, @Nullable String value) {
        if (value == null) {
            return this;
        }
        
        String placeholder = INITIAL_PLACEHOLDER_CHAR + key + FINAL_PLACEHOLDER_CHAR;
        this.content = this.content.replace(placeholder, value);
        return this;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return content;
    }

    @Nonnull
    @Override
    public MessageCreateData toDiscordMessage() {
        return new MessageCreateBuilder().setContent(content).build();
    }
}
