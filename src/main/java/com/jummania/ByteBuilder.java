package com.jummania;

import java.nio.charset.StandardCharsets;

public final class ByteBuilder {

    private byte[] data = new byte[256];
    private int size;

    public int size() {
        return size;
    }

    public byte[] toByteArray() {
        return java.util.Arrays.copyOf(data, size);
    }

    private void ensureCapacity(int extra) {
        int needed = size + extra;

        if (needed > data.length) {
            int newCapacity = Math.max(needed, data.length << 1);

            byte[] newData = new byte[newCapacity];

            System.arraycopy(data, 0, newData, 0, size);

            data = newData;
        }
    }

    public void writeByte(byte value) {
        ensureCapacity(1);
        data[size++] = value;
    }

    public void writeBoolean(boolean value) {
        ensureCapacity(1);
        data[size++] = (byte) (value ? 1 : 0);
    }

    public void writeShort(short value) {
        ensureCapacity(2);

        data[size++] = (byte) (value >>> 8);
        data[size++] = (byte) value;
    }

    public void writeChar(char value) {
        ensureCapacity(2);

        data[size++] = (byte) (value >>> 8);
        data[size++] = (byte) value;
    }

    public void writeInt(int value) {
        ensureCapacity(4);

        data[size++] = (byte) (value >>> 24);
        data[size++] = (byte) (value >>> 16);
        data[size++] = (byte) (value >>> 8);
        data[size++] = (byte) value;
    }

    public void writeLong(long value) {
        ensureCapacity(8);

        data[size++] = (byte) (value >>> 56);
        data[size++] = (byte) (value >>> 48);
        data[size++] = (byte) (value >>> 40);
        data[size++] = (byte) (value >>> 32);
        data[size++] = (byte) (value >>> 24);
        data[size++] = (byte) (value >>> 16);
        data[size++] = (byte) (value >>> 8);
        data[size++] = (byte) value;
    }

    public void writeFloat(float value) {
        writeInt(Float.floatToIntBits(value));
    }

    public void writeDouble(double value) {
        writeLong(Double.doubleToLongBits(value));
    }

    public void writeBytes(byte[] bytes) {

        int length = bytes.length;

        ensureCapacity(length);

        System.arraycopy(bytes, 0, data, size, length);

        size += length;
    }

    public void writeString(String value) {

        if (value == null) {
            writeInt(-1);
            return;
        }

        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

        writeInt(bytes.length);
        writeBytes(bytes);
    }

    @Override
    public String toString() {
        return new String(toByteArray(), StandardCharsets.UTF_8);
    }

    public String toHexString() {
        byte[] bytes = toByteArray();

        StringBuilder sb = new StringBuilder(bytes.length * 3);

        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }

        return sb.toString();
    }
}
