package com.slopggobblercobbler.litematica_converter.nbt;

/**
 * Minecraft NBT Tag Types (specification 19999).
 */
public enum TagType {
    TAG_END(0, "TAG_End"),
    TAG_BYTE(1, "TAG_Byte"),
    TAG_SHORT(2, "TAG_Short"),
    TAG_INT(3, "TAG_Int"),
    TAG_LONG(4, "TAG_Long"),
    TAG_FLOAT(5, "TAG_Float"),
    TAG_DOUBLE(6, "TAG_Double"),
    TAG_BYTE_ARRAY(7, "TAG_Byte_Array"),
    TAG_STRING(8, "TAG_String"),
    TAG_LIST(9, "TAG_List"),
    TAG_COMPOUND(10, "TAG_Compound"),
    TAG_INT_ARRAY(11, "TAG_Int_Array"),
    TAG_LONG_ARRAY(12, "TAG_Long_Array");

    private final int id;
    private final String name;

    TagType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static TagType fromId(int id) {
        TagType[] values = values();
        if (id >= 0 && id < values.length) {
            return values[id];
        }
        throw new IllegalArgumentException("Unknown NBT tag type ID: " + id);
    }
}
