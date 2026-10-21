package com.slopggobblercobbler.litematica_converter.model;

import com.slopggobblercobbler.litematica_converter.nbt.CompoundTag;

public final class EntityData {
    private final String id;
    private final double x;
    private final double y;
    private final double z;
    private final CompoundTag nbt;

    public EntityData(String id, double x, double y, double z, CompoundTag nbt) {
        this.id = id != null ? id : "";
        this.x = x;
        this.y = y;
        this.z = z;
        this.nbt = nbt != null ? nbt.copy() : new CompoundTag();
    }

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public CompoundTag getNbt() {
        return nbt;
    }
}
