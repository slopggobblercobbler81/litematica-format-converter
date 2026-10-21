package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class DoubleTag implements Tag {
    private final double value;

    public DoubleTag(double value) {
        this.value = value;
    }

    @Override
    public TagType getType() {
        return TagType.TAG_DOUBLE;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(value);
    }

    public static DoubleTag read(DataInput in) throws IOException {
        return new DoubleTag(in.readDouble());
    }

    @Override
    public DoubleTag copy() {
        return new DoubleTag(value);
    }

    @Override
    public Double getValue() {
        return value;
    }

    public double getDouble() {
        return value;
    }

    @Override
    public String toString() {
        return value + "d";
    }
}
