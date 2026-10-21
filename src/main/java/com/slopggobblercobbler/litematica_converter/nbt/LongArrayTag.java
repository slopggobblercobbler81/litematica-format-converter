package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public final class LongArrayTag implements Tag {
    private final long[] value;

    public LongArrayTag(long[] value) {
        this.value = value != null ? value : new long[0];
    }

    @Override
    public TagType getType() {
        return TagType.TAG_LONG_ARRAY;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(value.length);
        for (long l : value) {
            out.writeLong(l);
        }
    }

    public static LongArrayTag read(DataInput in) throws IOException {
        int length = in.readInt();
        long[] data = new long[length];
        for (int i = 0; i < length; i++) {
            data[i] = in.readLong();
        }
        return new LongArrayTag(data);
    }

    @Override
    public LongArrayTag copy() {
        return new LongArrayTag(Arrays.copyOf(value, value.length));
    }

    @Override
    public long[] getValue() {
        return value;
    }

    public int length() {
        return value.length;
    }

    @Override
    public String toString() {
        return "[L; " + value.length + " longs]";
    }
}
