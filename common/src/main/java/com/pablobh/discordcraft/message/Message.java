package com.pablobh.discordcraft.message;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public abstract class Message {

    public static final char INITIAL_PLACEHOLDER_CHAR = '%';
    public static final char FINAL_PLACEHOLDER_CHAR = '%';

    @Nonnull
    @Override
    public abstract String toString();

    @Nonnull
    public abstract MessageCreateData toDiscordMessage();

    @Nonnull
    public abstract Message replace(@Nonnull String key, @Nullable String value);

    @Nonnull
    public Message replace(@Nonnull String prefix, @Nonnull Map<String, String> fields) {
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            replace(prefix + "_" + entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Nonnull
    public Message replace(@Nonnull String prefix, @Nullable Placeholdable entity) {
        if (entity == null) {
            return this;
        }
        return replace(prefix, entity.toPlaceholders());
    }

    @Nonnull
    public Message replace(@Nonnull String key, @Nullable Member member) {
        if (member == null) {
            return this;
        }

        replace(key + "_id", member.getId());
        replace(key + "_name", member.getEffectiveName());

        String nickname = member.getNickname();
        if (nickname == null) nickname = member.getEffectiveName();
        replace(key + "_nickname", nickname);

        replace(key + "_mention", member.getAsMention());

        return this;
    }
}
