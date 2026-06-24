package com.pablobh.discordcraft.message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class StringMessage extends Message {

    private String content;

    public StringMessage(@Nonnull String content) {
        this.content = content;
    }

    @Nonnull
    @Override
    public Message replace(@Nonnull String key, @Nullable String value) {
        if (value == null) {
            return this;
        }
        
        String placeholder = INITIAL_PLACEHOLDER_CHAR + key + FINAL_PLACEHOLDER_CHAR;
        content = content.replace(placeholder, value);
        
        return this;
    }

    @Nonnull
    public String getContent() {
        return content;
    }

    @Nonnull
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
