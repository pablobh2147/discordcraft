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

package com.pablobh.discordcraft.neoforge.logging;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pablobh.discordcraft.logging.PluginLogger;

public class NeoForgeLogger implements PluginLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger("DiscordCraft");

    @Override
    public void info(@Nonnull String message) {
        LOGGER.info(message);
    }

    @Override
    public void warning(@Nonnull String message) {
        LOGGER.warn(message);
    }

    @Override
    public void severe(@Nonnull String message) {
        LOGGER.error(message);
    }

    @Override
    public void exception(@Nonnull Exception e, @Nullable String message) {
        if (message != null && !message.isBlank()) {
            LOGGER.error(message, e);
        } else {
            LOGGER.error("An exception occurred", e);
        }
    }
    
}
