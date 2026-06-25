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
    public abstract String getContent();

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
