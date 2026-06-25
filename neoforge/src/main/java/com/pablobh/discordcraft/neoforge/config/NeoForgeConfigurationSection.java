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

package com.pablobh.discordcraft.neoforge.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pablobh.discordcraft.configuration.ConfigurationSection;

public class NeoForgeConfigurationSection implements ConfigurationSection {

    private final Map<String, Object> data;

    public NeoForgeConfigurationSection(@Nonnull Map<String, Object> data) {
        this.data = data;
    }

    @Nullable
    @Override
    public Object get(@Nonnull String path) {
        return getFromPath(data, path);
    }

    @Override
    public boolean contains(@Nonnull String path) {
        return get(path) != null;
    }

    @Override
    public void set(@Nonnull String path, @Nullable Object value) {
        setInPath(data, path, value);
    }

    @Nullable
    @Override
    public String getString(@Nonnull String path) {
        Object value = get(path);
        return value != null ? value.toString() : null;
    }

    @Nullable
    @Override
    public String getString(@Nonnull String path, @Nullable String defaultValue) {
        String value = getString(path);
        return value != null ? value : defaultValue;
    }

    @Override
    public int getInt(@Nonnull String path) {
        return getInt(path, 0);
    }

    @Override
    public int getInt(@Nonnull String path, int defaultValue) {
        Object value = get(path);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                int parsed = Integer.parseInt((String) value);
                set(path, parsed);
                return parsed;
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    @Override
    public long getLong(@Nonnull String path) {
        return getLong(path, 0L);
    }

    @Override
    public long getLong(@Nonnull String path, long defaultValue) {
        Object value = get(path);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                long parsed = Long.parseLong((String) value);
                set(path, parsed);
                return parsed;
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    @Override
    public boolean getBoolean(@Nonnull String path) {
        return getBoolean(path, false);
    }

    @Override
    public boolean getBoolean(@Nonnull String path, boolean defaultValue) {
        Object value = get(path);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    @Override
    public double getDouble(@Nonnull String path) {
        return getDouble(path, 0.0);
    }

    @Override
    public double getDouble(@Nonnull String path, double defaultValue) {
        Object value = get(path);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                double parsed = Double.parseDouble((String) value);
                set(path, parsed);
                return parsed;
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    @Nonnull
    @Override
    public List<String> getStringList(@Nonnull String path) {
        Object value = get(path);
        if (value instanceof List) {
            List<String> result = new ArrayList<>();
            for (Object item : (List<?>) value) {
                result.add(item.toString());
            }
            return result;
        }
        return new ArrayList<>();
    }

    @Nonnull
    @Override
    public List<Map<?, ?>> getMapList(@Nonnull String path) {
        Object value = get(path);
        if (value instanceof List) {
            List<Map<?, ?>> result = new ArrayList<>();
            for (Object item : (List<?>) value) {
                if (item instanceof Map) {
                    result.add((Map<?, ?>) item);
                }
            }
            return result;
        }
        return new ArrayList<>();
    }

    @Nonnull
    @Override
    public Set<String> getKeys(boolean deep) {
        if (deep) {
            return getAllKeys(data, "");
        }
        return data.keySet();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public ConfigurationSection getSection(@Nonnull String path) {
        Object value = get(path);
        if (value instanceof Map) {
            return new NeoForgeConfigurationSection((Map<String, Object>) value);
        }
        return null;
    }

    @Nonnull
    @Override
    public ConfigurationSection createSection(@Nonnull String path) {
        Map<String, Object> section = new HashMap<>();
        set(path, section);
        return new NeoForgeConfigurationSection(section);
    }

    @Override
    public boolean isString(@Nonnull String path) {
        return get(path) instanceof String;
    }

    @Override
    public boolean isSection(@Nonnull String path) {
        return get(path) instanceof Map;
    }

    @Override
    public boolean isList(@Nonnull String path) {
        return get(path) instanceof List;
    }

    private Object getFromPath(Map<String, Object> map, String path) {
        String[] parts = path.split("\\.");
        Object current = map;
        
        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else {
                return null;
            }
        }
        
        return current;
    }

    @SuppressWarnings("unchecked")
    private void setInPath(Map<String, Object> map, String path, Object value) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = map;
        
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map)) {
                next = new HashMap<String, Object>();
                current.put(parts[i], next);
            }
            current = (Map<String, Object>) next;
        }
        
        current.put(parts[parts.length - 1], value);
    }

    @SuppressWarnings("unchecked")
    private Set<String> getAllKeys(Map<String, Object> map, String prefix) {
        Set<String> keys = new java.util.HashSet<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            keys.add(key);
            if (entry.getValue() instanceof Map) {
                keys.addAll(getAllKeys((Map<String, Object>) entry.getValue(), key));
            }
        }
        return keys;
    }

}
