package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ListTag implements Tag, Iterable<Tag> {
    private TagType elementType;
    private final List<Tag> list;

    public ListTag() {
        this(TagType.TAG_END, new ArrayList<>());
    }

    public ListTag(TagType elementType) {
        this(elementType, new ArrayList<>());
    }

    public ListTag(TagType elementType, List<Tag> list) {
        this.elementType = elementType;
        this.list = list != null ? list : new ArrayList<>();
    }

    @Override
    public TagType getType() {
        return TagType.TAG_LIST;
    }

    public TagType getElementType() {
        return elementType;
    }

    public void add(Tag tag) {
        if (tag == null) return;
        if (list.isEmpty() && elementType == TagType.TAG_END) {
            elementType = tag.getType();
        } else if (tag.getType() != elementType) {
            throw new IllegalArgumentException("Cannot add tag of type " + tag.getType() + " to ListTag of type " + elementType);
        }
        list.add(tag);
    }

    public Tag get(int index) {
        return list.get(index);
    }

    public CompoundTag getCompound(int index) {
        Tag tag = list.get(index);
        return tag instanceof CompoundTag ? (CompoundTag) tag : new CompoundTag();
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Iterator<Tag> iterator() {
        return list.iterator();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeByte(elementType.getId());
        out.writeInt(list.size());
        for (Tag tag : list) {
            tag.write(out);
        }
    }

    public static ListTag read(DataInput in, int depth) throws IOException {
        int elementTypeId = in.readByte();
        int size = in.readInt();
        TagType elemType = TagType.fromId(elementTypeId);
        List<Tag> elements = new ArrayList<>(Math.max(0, size));
        if (size > 0 && elemType != TagType.TAG_END) {
            for (int i = 0; i < size; i++) {
                elements.add(NBTUtil.readTagPayload(elemType, in, depth + 1));
            }
        }
        return new ListTag(elemType, elements);
    }

    @Override
    public ListTag copy() {
        List<Tag> copied = new ArrayList<>(list.size());
        for (Tag tag : list) {
            copied.add(tag.copy());
        }
        return new ListTag(elementType, copied);
    }

    @Override
    public List<Tag> getValue() {
        return list;
    }

    @Override
    public String toString() {
        return "[" + list.size() + " entries of " + elementType.getName() + "]";
    }
}
