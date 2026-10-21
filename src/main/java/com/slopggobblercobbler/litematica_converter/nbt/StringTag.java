package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class StringTag implements Tag {
    private final String value;

    public StringTag(String value) {
        this.value = value != null ? value : "";
    }

    @Override
    public TagType getType() {
        return TagType.TAG_STRING;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(value);
    }

    public static StringTag read(DataInput in) throws IOException {
        return new StringTag(in.readUTF());
    }

    @Override
    public StringTag copy() {
        return new StringTag(value);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "\"" + value.replace("\"", "\\\"") + "\"";
    }
}
