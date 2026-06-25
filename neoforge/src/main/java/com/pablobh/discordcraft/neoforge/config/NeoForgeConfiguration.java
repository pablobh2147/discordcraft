package com.pablobh.discordcraft.neoforge.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.pablobh.discordcraft.configuration.Configuration;
import com.pablobh.discordcraft.configuration.ConfigurationSection;

import net.neoforged.fml.loading.FMLPaths;

public class NeoForgeConfiguration implements Configuration {

    private Map<String, Object> data;
    private Node rootNode;
    private File configFile;
    private final String filename;
    private final Yaml yaml;

    public NeoForgeConfiguration(@Nonnull String filename) {
        this.filename = filename;

        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setProcessComments(true);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setProcessComments(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setPrettyFlow(true);

        this.yaml = new Yaml(new Constructor(loaderOptions), new Representer(dumperOptions), dumperOptions, loaderOptions);

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
        try (FileInputStream fis = new FileInputStream(configFile);
             InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            rootNode = yaml.compose(reader);
            data = nodeToMap(rootNode);
        } catch (IOException e) {
            LoggerFactory.getLogger("DiscordCraft").error("Failed to load config: " + filename, e);
            rootNode = null;
            data = new LinkedHashMap<>();
        }
    }

    @Override
    public boolean save() {
        if (rootNode != null) {
            syncMapToNode(data, rootNode);
        }
        try (FileWriter writer = new FileWriter(configFile, StandardCharsets.UTF_8)) {
            if (rootNode != null) {
                yaml.serialize(rootNode, writer);
            } else {
                yaml.dump(data, writer);
            }
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
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) value;
            return new NeoForgeConfigurationSection(map);
        }
        return null;
    }

    @Nonnull
    @Override
    public ConfigurationSection createSection(@Nonnull String path) {
        Map<String, Object> section = new LinkedHashMap<>();
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

    // --------------------- Node <-> Map helpers ---------------------

    @SuppressWarnings("unchecked")
    private Map<String, Object> nodeToMap(Node node) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!(node instanceof MappingNode mappingNode)) {
            return result;
        }
        for (NodeTuple tuple : mappingNode.getValue()) {
            if (!(tuple.getKeyNode() instanceof ScalarNode keyNode)) continue;
            String key = keyNode.getValue();
            result.put(key, nodeToObject(tuple.getValueNode()));
        }
        return result;
    }

    private Object nodeToObject(Node node) {
        if (node instanceof MappingNode) {
            return nodeToMap(node);
        } else if (node instanceof SequenceNode sequenceNode) {
            List<Object> list = new ArrayList<>();
            for (Node item : sequenceNode.getValue()) {
                list.add(nodeToObject(item));
            }
            return list;
        } else if (node instanceof ScalarNode scalarNode) {
            return resolveScalar(scalarNode);
        }
        return null;
    }

    private Object resolveScalar(ScalarNode node) {
        String value = node.getValue();
        Tag tag = node.getTag();
        if (Tag.INT.equals(tag)) {
            try { return Integer.parseInt(value); } catch (NumberFormatException e) {
                try { return Long.parseLong(value); } catch (NumberFormatException e2) { return value; }
            }
        }
        if (Tag.FLOAT.equals(tag)) {
            try { return Double.parseDouble(value); } catch (NumberFormatException e) { return value; }
        }
        if (Tag.BOOL.equals(tag)) {
            return "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value);
        }
        if (Tag.NULL.equals(tag)) {
            return null;
        }
        return value;
    }

    private void syncMapToNode(Map<String, Object> map, Node node) {
        if (!(node instanceof MappingNode mappingNode)) return;
        List<NodeTuple> tuples = mappingNode.getValue();
        for (int i = 0; i < tuples.size(); i++) {
            NodeTuple tuple = tuples.get(i);
            if (!(tuple.getKeyNode() instanceof ScalarNode keyNode)) continue;
            String key = keyNode.getValue();
            if (!map.containsKey(key)) continue;
            Object value = map.get(key);
            Node valueNode = tuple.getValueNode();
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> subMap = (Map<String, Object>) value;
                if (valueNode instanceof MappingNode) {
                    syncMapToNode(subMap, valueNode);
                } else {
                    // node was null/scalar but data now has a map — build a new MappingNode
                    MappingNode newMapping = objectToMappingNode(subMap);
                    newMapping.setBlockComments(valueNode.getBlockComments());
                    newMapping.setInLineComments(valueNode.getInLineComments());
                    newMapping.setEndComments(valueNode.getEndComments());
                    tuples.set(i, new NodeTuple(keyNode, newMapping));
                }
            } else if (valueNode instanceof ScalarNode scalarNode) {
                ScalarNode updated = new ScalarNode(
                    scalarNode.getTag(),
                    objectToScalar(value),
                    scalarNode.getStartMark(),
                    scalarNode.getEndMark(),
                    scalarNode.getScalarStyle()
                );
                updated.setBlockComments(scalarNode.getBlockComments());
                updated.setInLineComments(scalarNode.getInLineComments());
                updated.setEndComments(scalarNode.getEndComments());
                tuples.set(i, new NodeTuple(keyNode, updated));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private MappingNode objectToMappingNode(Map<String, Object> map) {
        List<NodeTuple> tuples = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            ScalarNode keyNode = new ScalarNode(Tag.STR, entry.getKey(), null, null, DumperOptions.ScalarStyle.PLAIN);
            Node valueNode;
            Object val = entry.getValue();
            if (val instanceof Map) {
                valueNode = objectToMappingNode((Map<String, Object>) val);
            } else if (val instanceof List) {
                valueNode = objectToSequenceNode((List<Object>) val);
            } else {
                String strVal = val == null ? "" : val.toString();
                valueNode = new ScalarNode(Tag.STR, strVal, null, null, DumperOptions.ScalarStyle.PLAIN);
            }
            tuples.add(new NodeTuple(keyNode, valueNode));
        }
        return new MappingNode(Tag.MAP, tuples, DumperOptions.FlowStyle.BLOCK);
    }

    @SuppressWarnings("unchecked")
    private SequenceNode objectToSequenceNode(List<Object> list) {
        List<Node> nodes = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map) {
                nodes.add(objectToMappingNode((Map<String, Object>) item));
            } else {
                String strVal = item == null ? "" : item.toString();
                nodes.add(new ScalarNode(Tag.STR, strVal, null, null, DumperOptions.ScalarStyle.PLAIN));
            }
        }
        return new SequenceNode(Tag.SEQ, nodes, DumperOptions.FlowStyle.BLOCK);
    }

    private String objectToScalar(Object value) {
        if (value == null) return "null";
        return value.toString();
    }

    // --------------------- Path helpers ---------------------

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
                next = new LinkedHashMap<String, Object>();
                current.put(parts[i], next);
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> nextMap = (Map<String, Object>) next;
            current = nextMap;
        }
        current.put(parts[parts.length - 1], value);
    }

    private Set<String> getAllKeys(Map<String, Object> map, String prefix) {
        Set<String> keys = new HashSet<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            keys.add(key);
            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> subMap = (Map<String, Object>) entry.getValue();
                keys.addAll(getAllKeys(subMap, key));
            }
        }
        return keys;
    }

}
