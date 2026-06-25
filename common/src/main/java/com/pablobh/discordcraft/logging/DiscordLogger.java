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

package com.pablobh.discordcraft.logging;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pablobh.discordcraft.message.Message;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DiscordLogger {

    @Nullable
    private TextChannel channel;

    public DiscordLogger(@Nullable TextChannel channel) {
        this.channel = channel;
    }

    public void setChannel(@Nullable TextChannel channel) {
        this.channel = channel;
    }

    @Nullable
    public TextChannel getChannel() {
        return channel;
    }

    private void sendMessage(@Nonnull String message) {
        if (channel != null) {
            channel.sendMessage(message).queue();
        }
    }

    public void sendMessage(@Nonnull Message message) {
        if (channel != null) {
            channel.sendMessage(message.toDiscordMessage()).queue();
        }
    }

    private void log(@Nonnull String prefix, @Nonnull String message) {
        sendMessage("[" + prefix + "]: " + message);
    }
   
    public void info(@Nonnull String message) {
        log("INFO", message);
    }

    public void warning(@Nonnull String message) {
        log("WARNING", message);
    }

    public void severe(@Nonnull String message) {
        log("ERROR", message);
    }

    public void exception(@Nonnull Exception e, @Nullable String message) {
        if (message == null || message.isBlank()) {
            message = "An internal error occurred!";
        }
        log("ERROR", message + " (Check Console for more Details).");
    }

}
