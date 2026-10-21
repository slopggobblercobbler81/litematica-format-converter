package com.slopggobblercobbler.litematica_converter.nbt;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class SpongeSchematicNbtTest {

    private byte[] createSampleSpongeNbt(int version, boolean negativeOffset, boolean largePalette) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos);
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(gzip))) {
            
            dos.writeByte(TagType.TAG_COMPOUND.getId());
            dos.writeUTF("Schematic");

            dos.writeByte(TagType.TAG_INT.getId());
            dos.writeUTF("DataVersion");
            dos.writeInt(2586);

            dos.writeByte(TagType.TAG_INT.getId());
            dos.writeUTF("Version");
            dos.writeInt(version);

            dos.writeByte(TagType.TAG_COMPOUND.getId());
            dos.writeUTF("Metadata");
            dos.writeByte(TagType.TAG_STRING.getId());
            dos.writeUTF("Name");
            dos.writeUTF("AdvancedTest");
            dos.writeByte(TagType.TAG_END.getId());

            dos.writeByte(TagType.TAG_SHORT.getId());
            dos.writeUTF("Width");
            dos.writeShort(16);

            dos.writeByte(TagType.TAG_SHORT.getId());
            dos.writeUTF("Height");
            dos.writeShort(16);

            dos.writeByte(TagType.TAG_SHORT.getId());
            dos.writeUTF("Length");
            dos.writeShort(16);

            dos.writeByte(TagType.TAG_INT_ARRAY.getId());
            dos.writeUTF("Offset");
            dos.writeInt(3);
            dos.writeInt(negativeOffset ? -10 : 0);
            dos.writeInt(0);
            dos.writeInt(negativeOffset ? -5 : 0);

            dos.writeByte(TagType.TAG_COMPOUND.getId());
            dos.writeUTF("Blocks");
            dos.writeByte(TagType.TAG_BYTE_ARRAY.getId());
            dos.writeUTF("Data");
            byte[] blockData = new byte[] {0, 1, 2, 3};
            dos.writeInt(blockData.length);
            dos.write(blockData);
            dos.writeByte(TagType.TAG_END.getId());

            dos.writeByte(TagType.TAG_COMPOUND.getId());
            dos.writeUTF("Palette");
            dos.writeByte(TagType.TAG_INT.getId());
            dos.writeUTF("minecraft:air");
            dos.writeInt(0);
            dos.writeByte(TagType.TAG_INT.getId());
            dos.writeUTF("minecraft:stone");
            dos.writeInt(1);
            if (largePalette) {
                for (int i = 2; i < 130; i++) {
                    dos.writeByte(TagType.TAG_INT.getId());
                    dos.writeUTF("minecraft:block_" + i);
                    dos.writeInt(i);
                }
            }
            dos.writeByte(TagType.TAG_END.getId());

            if (version >= 2) {
                dos.writeByte(TagType.TAG_COMPOUND.getId());
                dos.writeUTF("Biomes");
                dos.writeByte(TagType.TAG_INT.getId());
                dos.writeUTF("Index");
                dos.writeInt(0);
                dos.writeByte(TagType.TAG_END.getId());
            }

            if (version >= 3) {
                dos.writeByte(TagType.TAG_LIST.getId());
                dos.writeUTF("Entities");
                dos.writeByte((byte) TagType.TAG_COMPOUND.getId());
                dos.writeInt(0);
            }

            dos.writeByte(TagType.TAG_END.getId());
        }
        return baos.toByteArray();
    }

    private byte[] createSampleSpongeNbt(int version) throws IOException {
        return createSampleSpongeNbt(version, false, false);
    }

    @Test
    public void testSpongeSchematicV1Parsing() throws Exception {
        byte[] data = createSampleSpongeNbt(1);
        CompoundTag root = NBTUtil.readCompound(new ByteArrayInputStream(data));
        assertNotNull(root);
        assertEquals(2586, ((IntTag) root.get("DataVersion")).getValue());
        assertEquals(1, ((IntTag) root.get("Version")).getValue());
    }

    @Test
    public void testSpongeSchematicV2Parsing() throws Exception {
        byte[] data = createSampleSpongeNbt(2);
        CompoundTag root = NBTUtil.readCompound(new ByteArrayInputStream(data));
        assertNotNull(root);
        assertEquals(2, ((IntTag) root.get("Version")).getValue());
        assertNotNull(root.get("Biomes"));
    }

    @Test
    public void testSpongeSchematicV3Parsing() throws Exception {
        byte[] data = createSampleSpongeNbt(3);
        CompoundTag root = NBTUtil.readCompound(new ByteArrayInputStream(data));
        assertNotNull(root);
        assertEquals(3, ((IntTag) root.get("Version")).getValue());
        assertNotNull(root.get("Entities"));
    }

    @Test
    public void testSpongeV3PaletteCompressionAndLargePalette() throws Exception {
        byte[] data = createSampleSpongeNbt(3, false, true);
        CompoundTag root = NBTUtil.readCompound(new ByteArrayInputStream(data));
        assertNotNull(root);
        CompoundTag palette = (CompoundTag) root.get("Palette");
        assertNotNull(palette);
        assertEquals(0, ((IntTag) palette.get("minecraft:air")).getValue());
        assertEquals(1, ((IntTag) palette.get("minecraft:stone")).getValue());
        assertEquals(129, ((IntTag) palette.get("minecraft:block_129")).getValue());
    }

    @Test
    public void testSpongeNegativeCoordinateOffsets() throws Exception {
        byte[] data = createSampleSpongeNbt(3, true, false);
        CompoundTag root = NBTUtil.readCompound(new ByteArrayInputStream(data));
        assertNotNull(root);
        IntArrayTag offset = (IntArrayTag) root.get("Offset");
        assertNotNull(offset);
        int[] vals = offset.getValue();
        assertEquals(3, vals.length);
        assertEquals(-10, vals[0]);
        assertEquals(0, vals[1]);
        assertEquals(-5, vals[2]);
    }
}
