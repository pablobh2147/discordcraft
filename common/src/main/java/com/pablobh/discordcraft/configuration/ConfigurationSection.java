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

package com.pablobh.discordcraft.configuration;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ConfigurationSection {

    @Nullable
    Object get(@Nonnull String path);

    boolean contains(@Nonnull String path);

    void set(@Nonnull String path, @Nullable Object value);

    // --------------------- Primitive Getters ---------------------

    @Nullable
    String getString(@Nonnull String path);

    @Nullable
    String getString(@Nonnull String path, @Nullable String defaultValue);

    int getInt(@Nonnull String path);

    int getInt(@Nonnull String path, int defaultValue);

    long getLong(@Nonnull String path);

    long getLong(@Nonnull String path, long defaultValue);

    boolean getBoolean(@Nonnull String path);

    boolean getBoolean(@Nonnull String path, boolean defaultValue);

    double getDouble(@Nonnull String path);

    double getDouble(@Nonnull String path, double defaultValue);

    // --------------------- Collection Getters ---------------------

    @Nonnull
    List<String> getStringList(@Nonnull String path);

    @Nonnull
    List<Map<?, ?>> getMapList(@Nonnull String path);

    @Nonnull
    Set<String> getKeys(boolean deep);

    // --------------------- Section Methods ---------------------

    @Nullable
    ConfigurationSection getSection(@Nonnull String path);

    @Nonnull
    ConfigurationSection createSection(@Nonnull String path);

    // --------------------- Type Checks ---------------------

    boolean isString(@Nonnull String path);

    boolean isSection(@Nonnull String path);

    boolean isList(@Nonnull String path);
    
}
