package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public final class ByteArrayTag implements Tag {
    private final byte[] value;

    public ByteArrayTag(byte[] value) {
        this.value = value != null ? value : new byte[0];
    }

    @Override
    public TagType getType() {
        return TagType.TAG_BYTE_ARRAY;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(value.length);
        out.write(value);
    }

    public static ByteArrayTag read(DataInput in) throws IOException {
        int length = in.readInt();
        byte[] data = new byte[length];
        in.readFully(data);
        return new ByteArrayTag(data);
    }

    @Override
    public ByteArrayTag copy() {
        return new ByteArrayTag(Arrays.copyOf(value, value.length));
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    public int length() {
        return value.length;
    }

    @Override
    public String toString() {
        return "[B; " + value.length + " bytes]";
    }
}
