package com.slopggobblercobbler.litematica_converter.model;

import com.slopggobblercobbler.litematica_converter.nbt.CompoundTag;

public final class BlockEntityData {
    private final String id;
    private final int x;
    private final int y;
    private final int z;
    private final CompoundTag nbt;

    public BlockEntityData(String id, int x, int y, int z, CompoundTag nbt) {
        this.id = id != null ? id : "";
        this.x = x;
        this.y = y;
        this.z = z;
        this.nbt = nbt != null ? nbt.copy() : new CompoundTag();
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public CompoundTag getNbt() {
        return nbt;
    }

    public BlockEntityData withOffset(int offsetX, int offsetY, int offsetZ) {
        CompoundTag newNbt = nbt.copy();
        int newX = x + offsetX;
        int newY = y + offsetY;
        int newZ = z + offsetZ;
        newNbt.putInt("x", newX);
        newNbt.putInt("y", newY);
        newNbt.putInt("z", newZ);
        return new BlockEntityData(id, newX, newY, newZ, newNbt);
    }
}
