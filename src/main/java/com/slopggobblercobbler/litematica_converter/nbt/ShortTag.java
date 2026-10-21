package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class ShortTag implements Tag {
    private final short value;

    public ShortTag(short value) {
        this.value = value;
    }

    @Override
    public TagType getType() {
        return TagType.TAG_SHORT;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeShort(value);
    }

    public static ShortTag read(DataInput in) throws IOException {
        return new ShortTag(in.readShort());
    }

    @Override
    public ShortTag copy() {
        return new ShortTag(value);
    }

    @Override
    public Short getValue() {
        return value;
    }

    public short getShort() {
        return value;
    }

    @Override
    public String toString() {
        return value + "s";
    }
}
