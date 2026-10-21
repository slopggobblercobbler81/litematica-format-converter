package com.slopggobblercobbler.litematica_converter.nbt;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

public final class NBTUtil {
    public static final int MAX_DEPTH = 512;

    private NBTUtil() {}

    public static Tag readTagPayload(TagType type, DataInput in, int depth) throws IOException {
        if (depth > MAX_DEPTH) {
            throw new IOException("NBT depth exceeded maximum depth limit (" + MAX_DEPTH + ")");
        }
        switch (type) {
            case TAG_END: return EndTag.read(in);
            case TAG_BYTE: return ByteTag.read(in);
            case TAG_SHORT: return ShortTag.read(in);
            case TAG_INT: return IntTag.read(in);
            case TAG_LONG: return LongTag.read(in);
            case TAG_FLOAT: return FloatTag.read(in);
            case TAG_DOUBLE: return DoubleTag.read(in);
            case TAG_BYTE_ARRAY: return ByteArrayTag.read(in);
            case TAG_STRING: return StringTag.read(in);
            case TAG_LIST: return ListTag.read(in, depth);
            case TAG_COMPOUND: return CompoundTag.read(in, depth);
            case TAG_INT_ARRAY: return IntArrayTag.read(in);
            case TAG_LONG_ARRAY: return LongArrayTag.read(in);
            default: throw new IOException("Unknown tag type: " + type);
        }
    }

    public static NamedTag readNamed(DataInputStream in) throws IOException {
        byte typeId = in.readByte();
        if (typeId == TagType.TAG_END.getId()) {
            return new NamedTag("", EndTag.INSTANCE);
        }
        TagType type = TagType.fromId(typeId);
        String name = in.readUTF();
        Tag tag = readTagPayload(type, in, 0);
        return new NamedTag(name, tag);
    }

    public static void writeNamed(String name, Tag tag, DataOutputStream out) throws IOException {
        if (tag == null || tag.getType() == TagType.TAG_END) {
            out.writeByte(TagType.TAG_END.getId());
            return;
        }
        out.writeByte(tag.getType().getId());
        out.writeUTF(name != null ? name : "");
        tag.write(out);
    }

    public static CompoundTag readCompound(InputStream rawIn) throws IOException {
        // Detect compression via magic bytes
        PushbackInputStream pushback = new PushbackInputStream(rawIn, 2);
        byte[] magic = new byte[2];
        int read = pushback.read(magic);
        if (read > 0) {
            pushback.unread(magic, 0, read);
        }

        InputStream in = pushback;
        if (read >= 2) {
            int b0 = magic[0] & 0xFF;
            int b1 = magic[1] & 0xFF;
            if (b0 == 0x1F && b1 == 0x8B) {
                // GZIP
                in = new GZIPInputStream(pushback);
            } else if (b0 == 0x78 && (b1 == 0x01 || b1 == 0x9C || b1 == 0xDA)) {
                // ZLIB
                in = new InflaterInputStream(pushback);
            }
        }

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(in))) {
            NamedTag named = readNamed(dis);
            if (named.tag instanceof CompoundTag) {
                return (CompoundTag) named.tag;
            }
            throw new IOException("Root tag is not a TAG_Compound: " + (named.tag != null ? named.tag.getType() : "null"));
        }
    }

    public static CompoundTag readCompound(Path path) throws IOException {
        try (InputStream in = Files.newInputStream(path)) {
            return readCompound(in);
        }
    }

    public static void writeGzip(CompoundTag tag, String rootName, Path path, int compressionLevel) throws IOException {
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        try (OutputStream fileOut = Files.newOutputStream(path);
             BufferedOutputStream bufOut = new BufferedOutputStream(fileOut)) {
            writeGzip(tag, rootName, bufOut, compressionLevel);
        }
    }

    public static void writeGzip(CompoundTag tag, String rootName, OutputStream out, int compressionLevel) throws IOException {
        // GZIP with custom compression level using Deflater
        GZIPOutputStream gzipOut = new GZIPOutputStream(out) {
            {
                def.setLevel(Math.max(0, Math.min(9, compressionLevel)));
            }
        };
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(gzipOut))) {
            writeNamed(rootName != null ? rootName : "", tag, dos);
            dos.flush();
        }
    }

    public static void writeUncompressed(CompoundTag tag, String rootName, OutputStream out) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(out))) {
            writeNamed(rootName != null ? rootName : "", tag, dos);
            dos.flush();
        }
    }

    public static final class NamedTag {
        public final String name;
        public final Tag tag;

        public NamedTag(String name, Tag tag) {
            this.name = name;
            this.tag = tag;
        }
    }
}
