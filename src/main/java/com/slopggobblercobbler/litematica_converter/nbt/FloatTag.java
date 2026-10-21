package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class FloatTag implements Tag {
    private final float value;

    public FloatTag(float value) {
        this.value = value;
    }

    @Override
    public TagType getType() {
        return TagType.TAG_FLOAT;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeFloat(value);
    }

    public static FloatTag read(DataInput in) throws IOException {
        return new FloatTag(in.readFloat());
    }

    @Override
    public FloatTag copy() {
        return new FloatTag(value);
    }

    @Override
    public Float getValue() {
        return value;
    }

    public float getFloat() {
        return value;
    }

    @Override
    public String toString() {
        return value + "f";
    }
}
