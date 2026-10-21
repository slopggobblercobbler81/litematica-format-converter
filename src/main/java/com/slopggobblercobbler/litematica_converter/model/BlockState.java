package com.slopggobblercobbler.litematica_converter.model;

import com.slopggobblercobbler.litematica_converter.nbt.CompoundTag;
import com.slopggobblercobbler.litematica_converter.nbt.StringTag;
import com.slopggobblercobbler.litematica_converter.nbt.Tag;

import java.util.*;

/**
 * Immutable representation of a Minecraft 1.13+ BlockState with namespaced ID and property map.
 */
public final class BlockState implements Comparable<BlockState> {
    public static final BlockState AIR = new BlockState("minecraft:air");

    private final String name;
    private final Map<String, String> properties;
    private final String cachedString;

    public BlockState(String name) {
        this(name, Collections.emptyMap());
    }

    public BlockState(String name, Map<String, String> properties) {
        if (name == null || name.isEmpty()) {
            this.name = "minecraft:air";
        } else if (!name.contains(":")) {
            this.name = "minecraft:" + name;
        } else {
            this.name = name;
        }

        if (properties == null || properties.isEmpty()) {
            this.properties = Collections.emptyMap();
        } else {
            // Sort keys deterministically
            this.properties = Collections.unmodifiableMap(new TreeMap<>(properties));
        }

        this.cachedString = buildStringRepresentation();
    }

    private String buildStringRepresentation() {
        if (properties.isEmpty()) {
            return name;
        }
        StringBuilder sb = new StringBuilder(name).append('[');
        boolean first = true;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (!first) sb.append(',');
            sb.append(entry.getKey()).append('=').append(entry.getValue());
            first = false;
        }
        sb.append(']');
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public boolean isAir() {
        return name.equals("minecraft:air") || name.equals("minecraft:cave_air") || name.equals("minecraft:void_air");
    }

    public static BlockState fromString(String stateStr) {
        if (stateStr == null || stateStr.trim().isEmpty()) {
            return AIR;
        }
        stateStr = stateStr.trim();
        int bracketIndex = stateStr.indexOf('[');
        if (bracketIndex < 0) {
            return new BlockState(stateStr);
        }

        String blockId = stateStr.substring(0, bracketIndex);
        int closeBracket = stateStr.lastIndexOf(']');
        if (closeBracket <= bracketIndex) {
            return new BlockState(blockId);
        }

        String propsStr = stateStr.substring(bracketIndex + 1, closeBracket);
        Map<String, String> props = new HashMap<>();
        String[] pairs = propsStr.split(",");
        for (String pair : pairs) {
            int eq = pair.indexOf('=');
            if (eq > 0) {
                String k = pair.substring(0, eq).trim();
                String v = pair.substring(eq + 1).trim();
                props.put(k, v);
            }
        }
        return new BlockState(blockId, props);
    }

    public static BlockState fromCompoundTag(CompoundTag tag) {
        if (tag == null) return AIR;
        String name = tag.getString("Name");
        if (name.isEmpty()) {
            name = tag.getString("name");
        }
        if (name.isEmpty()) return AIR;

        Map<String, String> props = new HashMap<>();
        CompoundTag propsTag = tag.getCompound("Properties");
        if (propsTag.isEmpty()) {
            propsTag = tag.getCompound("properties");
        }
        for (Map.Entry<String, Tag> entry : propsTag.entrySet()) {
            if (entry.getValue() instanceof StringTag) {
                props.put(entry.getKey(), ((StringTag) entry.getValue()).getValue());
            } else {
                props.put(entry.getKey(), String.valueOf(entry.getValue().getValue()));
            }
        }
        return new BlockState(name, props);
    }

    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Name", name);
        if (!properties.isEmpty()) {
            CompoundTag propsTag = new CompoundTag();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                propsTag.putString(entry.getKey(), entry.getValue());
            }
            tag.put("Properties", propsTag);
        }
        return tag;
    }

    @Override
    public String toString() {
        return cachedString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockState)) return false;
        BlockState that = (BlockState) o;
        return cachedString.equals(that.cachedString);
    }

    @Override
    public int hashCode() {
        return cachedString.hashCode();
    }

    @Override
    public int compareTo(BlockState o) {
        return this.cachedString.compareTo(o.cachedString);
    }
}
