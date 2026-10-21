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

    @Test
    public void testCorruptedOrTruncatedNbtInput() {
        assertThrows(IOException.class, () -> {
            NBTUtil.readCompound(new ByteArrayInputStream(new byte[0]));
        });

        assertThrows(IOException.class, () -> {
            NBTUtil.readCompound(new ByteArrayInputStream(new byte[] { 0x1f, (byte) 0x8b, 0x08, 0x00 }));
        });

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
            gzip.write(new byte[] { 0x0a, 0x00, 0x04, 'T', 'e', 's', 't' });
        } catch (IOException ignored) {}
        
        byte[] truncatedGzip = baos.toByteArray();
        if (truncatedGzip.length > 5) {
            byte[] corrupted = new byte[truncatedGzip.length - 4];
            System.arraycopy(truncatedGzip, 0, corrupted, 0, corrupted.length);
            assertThrows(IOException.class, () -> {
                NBTUtil.readCompound(new ByteArrayInputStream(corrupted));
            });
        }
    }

    @Test
    public void testNestedCompoundTagDepthExhaustion() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos);
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(gzip))) {
            
            dos.writeByte(TagType.TAG_COMPOUND.getId());
            dos.writeUTF("Root");

            for (int i = 0; i < 600; i++) {
                dos.writeByte(TagType.TAG_COMPOUND.getId());
                dos.writeUTF("nested_" + i);
            }

            for (int i = 0; i < 601; i++) {
                dos.writeByte(TagType.TAG_END.getId());
            }
        } catch (IOException ignored) {}

        byte[] data = baos.toByteArray();
        IOException ex = assertThrows(IOException.class, () -> {
            NBTUtil.readCompound(new ByteArrayInputStream(data));
        });
        assertTrue(ex.getMessage().contains("depth exceeded maximum depth limit"));
    }

    @Test
    public void testArrayTagBufferExhaustion() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos);
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(gzip))) {
            
            dos.writeByte(TagType.TAG_COMPOUND.getId());
            dos.writeUTF("Root");

            dos.writeByte(TagType.TAG_BYTE_ARRAY.getId());
            dos.writeUTF("HugeBytes");
            dos.writeInt(999999);

            dos.writeByte(TagType.TAG_END.getId());
        } catch (IOException ignored) {}

        byte[] data = baos.toByteArray();
        assertThrows(IOException.class, () -> {
            NBTUtil.readCompound(new ByteArrayInputStream(data));
        });

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos2);
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(gzip))) {
            
            dos.writeByte(TagType.TAG_COMPOUND.getId());
            dos.writeUTF("Root");

            dos.writeByte(TagType.TAG_INT_ARRAY.getId());
            dos.writeUTF("HugeInts");
            dos.writeInt(999999);

            dos.writeByte(TagType.TAG_END.getId());
        } catch (IOException ignored) {}

        byte[] data2 = baos2.toByteArray();
        assertThrows(IOException.class, () -> {
            NBTUtil.readCompound(new ByteArrayInputStream(data2));
        });
    }

    @Test
    public void testInvalidUtf8StringTagHeaders() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos);
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(gzip))) {
            
            dos.writeByte(TagType.TAG_COMPOUND.getId());
            dos.writeUTF("Root");

            dos.writeByte(TagType.TAG_STRING.getId());
            dos.writeShort(10);
            dos.write(new byte[] { (byte)0xff, (byte)0xff, (byte)0xff });

            dos.writeByte(TagType.TAG_END.getId());
        } catch (IOException ignored) {}

        byte[] data = baos.toByteArray();
        assertThrows(IOException.class, () -> {
            NBTUtil.readCompound(new ByteArrayInputStream(data));
        });
    }

    private byte[] generateStressPayload(int blockCount, int bitsPerEntry) throws IOException {
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
            dos.writeInt(3);

            dos.writeByte(TagType.TAG_SHORT.getId());
            dos.writeUTF("Width");
            dos.writeShort(100);

            dos.writeByte(TagType.TAG_SHORT.getId());
            dos.writeUTF("Height");
            dos.writeShort(100);

            dos.writeByte(TagType.TAG_SHORT.getId());
            dos.writeUTF("Length");
            dos.writeShort(blockCount / 10000);

            // Blocks byte array
            int byteCount = (blockCount * bitsPerEntry + 7) / 8;
            dos.writeByte(TagType.TAG_BYTE_ARRAY.getId());
            dos.writeUTF("Blocks");
            dos.writeInt(byteCount);
            byte[] blockBytes = new byte[byteCount];
            for (int i = 0; i < byteCount; i++) {
                blockBytes[i] = (byte) (i % 256);
            }
            dos.write(blockBytes);

            // Palette compound with distinct entries
            dos.writeByte(TagType.TAG_COMPOUND.getId());
            dos.writeUTF("Palette");
            int paletteSize = 1 << bitsPerEntry;
            for (int p = 0; p < paletteSize; p++) {
                dos.writeByte(TagType.TAG_INT.getId());
                dos.writeUTF("minecraft:block_" + p);
                dos.writeInt(p);
            }
            dos.writeByte(TagType.TAG_END.getId()); // End Palette

            dos.writeByte(TagType.TAG_END.getId()); // End Root
        }
        return baos.toByteArray();
    }

    @Test
    public void testStressBenchmarkThroughputAndMemory() throws Exception {
        int blockCount = 1_000_000;
        int[] bitWidths = new int[] { 4, 8, 12 };

        System.gc();
        long memBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        for (int bits : bitWidths) {
            byte[] payload = generateStressPayload(blockCount, bits);

            long startTime = System.nanoTime();
            CompoundTag root = NBTUtil.readCompound(new ByteArrayInputStream(payload));
            long endTime = System.nanoTime();

            assertNotNull(root);
            assertEquals(3, ((IntTag) root.get("Version")).getValue());

            double durationSeconds = (endTime - startTime) / 1_000_000_000.0;
            double blocksPerSec = blockCount / durationSeconds;
            System.out.println(String.format("Stress Benchmark [%d-bit palette, %d blocks]: parsed in %.3f seconds (%.2f blocks/sec)",
                    bits, blockCount, durationSeconds, blocksPerSec));
            
            assertTrue(blocksPerSec > 10_000, "Throughput should exceed 10,000 blocks/sec");
        }

        System.gc();
        long memAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memGrowth = memAfter - memBefore;
        System.out.println(String.format("Memory check: Growth after benchmark = %.2f MB", memGrowth / (1024.0 * 1024.0)));
        // Verify no severe memory leaks (allow reasonable GC buffer)
        assertTrue(memGrowth < 150 * 1024 * 1024, "Memory growth should remain bounded below 150MB");
    }
}
