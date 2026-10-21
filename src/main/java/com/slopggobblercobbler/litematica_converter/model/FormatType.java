package com.slopggobblercobbler.litematica_converter.model;

public enum FormatType {
    LITEMATIC("litematic", ".litematic", "Litematica Schematic (v5/v6)"),
    SPONGE_SCHEM("sponge", ".schem", "Sponge Schematic (WorldEdit v1/v2/v3)"),
    LEGACY_SCHEMATIC("legacy", ".schematic", "Legacy MCEdit Schematic (1.12-)"),
    STRUCTURE_NBT("structure", ".nbt", "Minecraft Structure NBT");

    private final String id;
    private final String extension;
    private final String displayName;

    FormatType(String id, String extension, String displayName) {
        this.id = id;
        this.extension = extension;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getExtension() {
        return extension;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static FormatType fromExtension(String filename) {
        if (filename == null) return null;
        String lower = filename.toLowerCase();
        if (lower.endsWith(".litematic")) return LITEMATIC;
        if (lower.endsWith(".schem")) return SPONGE_SCHEM;
        if (lower.endsWith(".schematic")) return LEGACY_SCHEMATIC;
        if (lower.endsWith(".nbt")) return STRUCTURE_NBT;
        return null;
    }

    public static FormatType fromId(String id) {
        if (id == null) return null;
        for (FormatType t : values()) {
            if (t.id.equalsIgnoreCase(id) || t.name().equalsIgnoreCase(id)) {
                return t;
            }
        }
        return null;
    }
}
