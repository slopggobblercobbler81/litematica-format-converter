package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Base interface for all NBT tags.
 */
public interface Tag {
    TagType getType();

    void write(DataOutput out) throws IOException;

    Tag copy();

    Object getValue();
}
