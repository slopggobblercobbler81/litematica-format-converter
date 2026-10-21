package com.slopggobblercobbler.litematica_converter.model;

import java.util.Arrays;

/**
 * Packed bit storage matching Litematica's BitArray implementation.
 * Encodes values sequentially across 64-bit boundaries (unpadded).
 */
public final class BitArray {
    private final int bitsPerEntry;
    private final int size;
    private final long maxEntryValue;
    private final long[] array;

    public BitArray(int bitsPerEntry, int size) {
        if (bitsPerEntry < 1 || bitsPerEntry > 32) {
            throw new IllegalArgumentException("bitsPerEntry must be between 1 and 32, got " + bitsPerEntry);
        }
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative, got " + size);
        }
        this.bitsPerEntry = bitsPerEntry;
        this.size = size;
        this.maxEntryValue = (1L << bitsPerEntry) - 1L;

        long totalBits = (long) size * bitsPerEntry;
        int longCount = (int) ((totalBits + 63L) / 64L);
        this.array = new long[longCount];
    }

    public BitArray(int bitsPerEntry, int size, long[] backingArray) {
        if (bitsPerEntry < 1 || bitsPerEntry > 32) {
            throw new IllegalArgumentException("bitsPerEntry must be between 1 and 32, got " + bitsPerEntry);
        }
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative, got " + size);
        }
        this.bitsPerEntry = bitsPerEntry;
        this.size = size;
        this.maxEntryValue = (1L << bitsPerEntry) - 1L;

        long totalBits = (long) size * bitsPerEntry;
        int expectedLongs = (int) ((totalBits + 63L) / 64L);
        if (backingArray == null || backingArray.length < expectedLongs) {
            this.array = new long[expectedLongs];
            if (backingArray != null) {
                System.arraycopy(backingArray, 0, this.array, 0, backingArray.length);
            }
        } else {
            this.array = Arrays.copyOf(backingArray, expectedLongs);
        }
    }

    public static int calculateBitsNeeded(int paletteSize) {
        int bits = 2;
        while ((1 << bits) < paletteSize) {
            bits++;
        }
        return bits;
    }

    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        long bitIndex = (long) index * bitsPerEntry;
        int startLong = (int) (bitIndex >> 6);
        int startOffset = (int) (bitIndex & 63L);

        if (startOffset + bitsPerEntry <= 64) {
            return (int) ((array[startLong] >>> startOffset) & maxEntryValue);
        } else {
            int bitsInFirst = 64 - startOffset;
            int bitsInSecond = bitsPerEntry - bitsInFirst;
            long mask1 = (1L << bitsInFirst) - 1L;
            long mask2 = (1L << bitsInSecond) - 1L;

            long val1 = (array[startLong] >>> startOffset) & mask1;
            long val2 = (array[startLong + 1] & mask2) << bitsInFirst;
            return (int) (val1 | val2);
        }
    }

    public void set(int index, int value) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        long val = value & maxEntryValue;
        long bitIndex = (long) index * bitsPerEntry;
        int startLong = (int) (bitIndex >> 6);
        int startOffset = (int) (bitIndex & 63L);

        if (startOffset + bitsPerEntry <= 64) {
            array[startLong] &= ~(maxEntryValue << startOffset);
            array[startLong] |= (val << startOffset);
        } else {
            int bitsInFirst = 64 - startOffset;
            int bitsInSecond = bitsPerEntry - bitsInFirst;
            long mask1 = (1L << bitsInFirst) - 1L;
            long mask2 = (1L << bitsInSecond) - 1L;

            array[startLong] &= ~(mask1 << startOffset);
            array[startLong] |= ((val & mask1) << startOffset);

            array[startLong + 1] &= ~mask2;
            array[startLong + 1] |= (((val >>> bitsInFirst) & mask2));
        }
    }

    public int getBitsPerEntry() {
        return bitsPerEntry;
    }

    public int size() {
        return size;
    }

    public long[] getBackingLongArray() {
        return array;
    }
}
