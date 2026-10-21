package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public final class IntArrayTag implements Tag {
    private final int[] value;

    public IntArrayTag(int[] value) {
        this.value = value != null ? value : new int[0];
    }

    @Override
    public TagType getType() {
        return TagType.TAG_INT_ARRAY;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(value.length);
        for (int i : value) {
            out.writeInt(i);
        }
    }

    public static IntArrayTag read(DataInput in) throws IOException {
        int length = in.readInt();
        int[] data = new int[length];
        for (int i = 0; i < length; i++) {
            data[i] = in.readInt();
        }
        return new IntArrayTag(data);
    }

    @Override
    public IntArrayTag copy() {
        return new IntArrayTag(Arrays.copyOf(value, value.length));
    }

    @Override
    public int[] getValue() {
        return value;
    }

    public int length() {
        return value.length;
    }

    @Override
    public String toString() {
        return "[I; " + value.length + " ints]";
    }
}
