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
    @Override
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
