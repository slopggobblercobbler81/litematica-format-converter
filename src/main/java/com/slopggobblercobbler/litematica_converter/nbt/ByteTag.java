package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class ByteTag implements Tag {
    private final byte value;

    public ByteTag(byte value) {
        this.value = value;
    }

    public ByteTag(boolean bool) {
        this.value = (byte) (bool ? 1 : 0);
    }

    @Override
    public TagType getType() {
        return TagType.TAG_BYTE;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeByte(value);
    }

    public static ByteTag read(DataInput in) throws IOException {
        return new ByteTag(in.readByte());
    }

    @Override
    public ByteTag copy() {
        return new ByteTag(value);
    }

    @Override
    public Byte getValue() {
        return value;
    }

    public byte getByte() {
        return value;
    }

    public boolean getBoolean() {
        return value != 0;
    }

    @Override
    public String toString() {
        return value + "b";
    }
}
