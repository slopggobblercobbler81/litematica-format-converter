package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class IntTag implements Tag {
    private final int value;

    public IntTag(int value) {
        this.value = value;
    }

    @Override
    public TagType getType() {
        return TagType.TAG_INT;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(value);
    }

    public static IntTag read(DataInput in) throws IOException {
        return new IntTag(in.readInt());
    }

    @Override
    public IntTag copy() {
        return new IntTag(value);
    }

    @Override
    public Integer getValue() {
        return value;
    }

    public int getInt() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
