package com.pablobh.discordcraft.neoforge.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.pablobh.discordcraft.configuration.Configuration;
import com.pablobh.discordcraft.configuration.ConfigurationSection;

import net.neoforged.fml.loading.FMLPaths;

public class NeoForgeConfiguration implements Configuration {

    private Map<String, Object> data;
    private File configFile;
    private final String filename;

    public NeoForgeConfiguration(@Nonnull String filename) {
        this.filename = filename;
        File configDir = FMLPaths.CONFIGDIR.get().toFile();
        File discordCraftDir = new File(configDir, "discordcraft");

        if (!discordCraftDir.exists()) {
            discordCraftDir.mkdirs();
        }

        configFile = new File(discordCraftDir, filename);

        if (!configFile.exists()) {
            copyDefaultConfig();
        }

        load();
    }

    private void copyDefaultConfig() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (in != null) {
                java.nio.file.Files.copy(in, configFile.toPath());
            }
        } catch (IOException e) {
            LoggerFactory.getLogger("DiscordCraft").error("Failed to copy default config: " + filename, e);
        }
    }

    private void load() {
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            Object loaded = yaml.load(fis);
            if (loaded instanceof Map) {
                data = (Map<String, Object>) loaded;
            } else {
                data = new HashMap<>();
            }
        } catch (IOException e) {
            LoggerFactory.getLogger("DiscordCraft").error("Failed to load config: " + filename, e);
            data = new HashMap<>();
        }
    }

    @Override
    public boolean save() {
        Yaml yaml = new Yaml();
        try (FileWriter writer = new FileWriter(configFile)) {
            yaml.dump(data, writer);
            return true;
        } catch (IOException e) {
            LoggerFactory.getLogger("DiscordCraft").error("Failed to save config: " + filename, e);
            return false;
        }
    }

    @Override
    public boolean reload() {
        load();
        return true;
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
