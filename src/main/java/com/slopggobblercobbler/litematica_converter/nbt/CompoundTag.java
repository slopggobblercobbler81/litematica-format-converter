package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public final class CompoundTag implements Tag, Iterable<Map.Entry<String, Tag>> {
    private final Map<String, Tag> tags;

    public CompoundTag() {
        this.tags = new LinkedHashMap<>();
    }

    public CompoundTag(Map<String, Tag> tags) {
        this.tags = tags != null ? new LinkedHashMap<>(tags) : new LinkedHashMap<>();
    }

    @Override
    public TagType getType() {
        return TagType.TAG_COMPOUND;
    }

    public void put(String key, Tag tag) {
        if (key != null && tag != null) {
            tags.put(key, tag);
        }
    }

    public void putByte(String key, byte value) {
        put(key, new ByteTag(value));
    }

    public void putBoolean(String key, boolean value) {
        put(key, new ByteTag(value));
    }

    public void putShort(String key, short value) {
        put(key, new ShortTag(value));
    }

    public void putInt(String key, int value) {
        put(key, new IntTag(value));
    }

    public void putLong(String key, long value) {
        put(key, new LongTag(value));
    }

    public void putFloat(String key, float value) {
        put(key, new FloatTag(value));
    }

    public void putDouble(String key, double value) {
        put(key, new DoubleTag(value));
    }

    public void putByteArray(String key, byte[] value) {
        put(key, new ByteArrayTag(value));
    }

    public void putString(String key, String value) {
        put(key, new StringTag(value));
    }

    public void putIntArray(String key, int[] value) {
        put(key, new IntArrayTag(value));
    }

    public void putLongArray(String key, long[] value) {
        put(key, new LongArrayTag(value));
    }

    public Tag get(String key) {
        return tags.get(key);
    }

    public boolean contains(String key) {
        return tags.containsKey(key);
    }

    public boolean contains(String key, TagType type) {
        Tag tag = tags.get(key);
        return tag != null && tag.getType() == type;
    }

    public byte getByte(String key) {
        Tag tag = tags.get(key);
        return tag instanceof ByteTag ? ((ByteTag) tag).getByte() : 0;
    }

    public boolean getBoolean(String key) {
        return getByte(key) != 0;
    }

    public short getShort(String key) {
        Tag tag = tags.get(key);
        return tag instanceof ShortTag ? ((ShortTag) tag).getShort() : 0;
    }

    public int getInt(String key) {
        Tag tag = tags.get(key);
        if (tag instanceof IntTag) {
            return ((IntTag) tag).getInt();
        } else if (tag instanceof ShortTag) {
            return ((ShortTag) tag).getShort();
        } else if (tag instanceof ByteTag) {
            return ((ByteTag) tag).getByte();
        }
        return 0;
    }

    public long getLong(String key) {
        Tag tag = tags.get(key);
        if (tag instanceof LongTag) {
            return ((LongTag) tag).getLong();
        } else if (tag instanceof IntTag) {
            return ((IntTag) tag).getInt();
        }
        return 0L;
    }

    public float getFloat(String key) {
        Tag tag = tags.get(key);
        return tag instanceof FloatTag ? ((FloatTag) tag).getFloat() : 0.0f;
    }

    public double getDouble(String key) {
        Tag tag = tags.get(key);
        return tag instanceof DoubleTag ? ((DoubleTag) tag).getDouble() : 0.0;
    }

    public String getString(String key) {
        Tag tag = tags.get(key);
        return tag instanceof StringTag ? ((StringTag) tag).getValue() : "";
    }

    public byte[] getByteArray(String key) {
        Tag tag = tags.get(key);
        return tag instanceof ByteArrayTag ? ((ByteArrayTag) tag).getValue() : new byte[0];
    }

    public int[] getIntArray(String key) {
        Tag tag = tags.get(key);
        return tag instanceof IntArrayTag ? ((IntArrayTag) tag).getValue() : new int[0];
    }

    public long[] getLongArray(String key) {
        Tag tag = tags.get(key);
        return tag instanceof LongArrayTag ? ((LongArrayTag) tag).getValue() : new long[0];
    }

    public CompoundTag getCompound(String key) {
        Tag tag = tags.get(key);
        return tag instanceof CompoundTag ? (CompoundTag) tag : new CompoundTag();
    }

    public ListTag getList(String key) {
        Tag tag = tags.get(key);
        return tag instanceof ListTag ? (ListTag) tag : new ListTag();
    }

    public Tag remove(String key) {
        return tags.remove(key);
    }

    public Set<String> keySet() {
        return tags.keySet();
    }

    public Set<Map.Entry<String, Tag>> entrySet() {
        return tags.entrySet();
    }

    public int size() {
        return tags.size();
    }

    public boolean isEmpty() {
        return tags.isEmpty();
    }

    @Override
    public Iterator<Map.Entry<String, Tag>> iterator() {
        return tags.entrySet().iterator();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        for (Map.Entry<String, Tag> entry : tags.entrySet()) {
            Tag tag = entry.getValue();
            if (tag == null || tag.getType() == TagType.TAG_END) continue;
            out.writeByte(tag.getType().getId());
            out.writeUTF(entry.getKey());
            tag.write(out);
        }
        out.writeByte(TagType.TAG_END.getId());
    }

    public static CompoundTag read(DataInput in, int depth) throws IOException {
        CompoundTag compound = new CompoundTag();
        while (true) {
            byte typeId = in.readByte();
            if (typeId == TagType.TAG_END.getId()) {
                break;
            }
            TagType type = TagType.fromId(typeId);
            String name = in.readUTF();
            Tag tag = NBTUtil.readTagPayload(type, in, depth + 1);
            compound.put(name, tag);
        }
        return compound;
    }

    @Override
    public CompoundTag copy() {
        CompoundTag copy = new CompoundTag();
        for (Map.Entry<String, Tag> entry : tags.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().copy());
        }
        return copy;
    }

    @Override
    public Map<String, Tag> getValue() {
        return tags;
    }

    @Override
    public String toString() {
        return "TAG_Compound{" + tags.size() + " entries}";
    }
}
