package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class EndTag implements Tag {
    public static final EndTag INSTANCE = new EndTag();

    private EndTag() {}

    @Override
    public TagType getType() {
        return TagType.TAG_END;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        // EndTag has no payload
    }

    public static EndTag read(DataInput in) {
        return INSTANCE;
    }

    @Override
    public EndTag copy() {
        return INSTANCE;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String toString() {
        return "TAG_End";
    }
}
