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

import com.pablobh.discordcraft.configuration.Configuration;
import com.pablobh.discordcraft.configuration.ConfigurationSection;

public class MessageService {
   
    private final Configuration config;

    public MessageService(Configuration config) {
        this.config = config;
    }

    @Nullable
    public Message getMessage(@Nonnull String key) {
        String messageContent = config.getString(key);
        
        if (messageContent == null || messageContent.isEmpty()) {
            return null;
        }
        
        return new StringMessage(messageContent);
    }

    @Nonnull
    public Message getMessageOrEmpty(@Nonnull String key) {
        return getMessageOrDefault(key, "");
    }

    @Nonnull
    public Message getMessageOrDefault(@Nonnull String key, @Nonnull String defaultValue) {
        Message message = getMessage(key);
        return message != null ? message : new StringMessage(defaultValue);
    }

    @Nullable
    public Message getDiscordMessage(@Nonnull String key) {
        if (config.isString(key)) {
            String messageContent = config.getString(key);
            if (messageContent == null || messageContent.isEmpty()) {
                return null;
            }
            return new StringMessage(messageContent);
        }
        
        if (config.isSection(key)) {
            ConfigurationSection section = config.getSection(key);
            if (section == null) {
                return null;
            }
            return new DiscordMessage(section);
        }
        
        return null;
    }

    @Nonnull
    public Message getDiscordMessageOrDefault(@Nonnull String key, @Nonnull String defaultValue) {
        Message message = getDiscordMessage(key);    
        return message != null ? message : new StringMessage(defaultValue);
    }

    @Nullable
    public String getPlainMessage(@Nonnull String key) {
        return config.getString(key, null);
    }

    @Nonnull
    public String getPlainMessageOrDefault(@Nonnull String key, @Nonnull String defaultValue) {
        return config.getString(key, defaultValue);
    }
}
