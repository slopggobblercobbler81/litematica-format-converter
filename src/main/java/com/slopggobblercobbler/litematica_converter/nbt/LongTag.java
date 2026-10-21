package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class LongTag implements Tag {
    private final long value;

    public LongTag(long value) {
        this.value = value;
    }

    @Override
    public TagType getType() {
        return TagType.TAG_LONG;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeLong(value);
    }

    public static LongTag read(DataInput in) throws IOException {
        return new LongTag(in.readLong());
    }

    @Override
    public LongTag copy() {
        return new LongTag(value);
    }

    @Override
    public Long getValue() {
        return value;
    }

    public long getLong() {
        return value;
    }

    @Override
    public String toString() {
        return value + "L";
    }
}
