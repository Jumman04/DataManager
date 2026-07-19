package com.jummania;

import com.jummania.interfaces.Reader;

public final class ByteReader implements Reader {

    private final byte[] data;
    private int position;

    public ByteReader(byte[] data) {
        this.data = data;
    }

    @Override
    public byte readByte() {
        return data[position++];
    }

    @Override
    public boolean readBoolean() {
        return readByte() != 0;
    }

    @Override
    public short readShort() {
        return (short) (((data[position++] & 0xFF) << 8) | (data[position++] & 0xFF));
    }

    @Override
    public char readChar() {
        return (char) readShort();
    }

    @Override
    public int readInt() {
        return ((data[position++] & 0xFF) << 24) | ((data[position++] & 0xFF) << 16) | ((data[position++] & 0xFF) << 8) | (data[position++] & 0xFF);
    }

    @Override
    public long readLong() {
        return ((long) (data[position++] & 0xFF) << 56) | ((long) (data[position++] & 0xFF) << 48) | ((long) (data[position++] & 0xFF) << 40) | ((long) (data[position++] & 0xFF) << 32) | ((long) (data[position++] & 0xFF) << 24) | ((long) (data[position++] & 0xFF) << 16) | ((long) (data[position++] & 0xFF) << 8) | ((long) (data[position++] & 0xFF));
    }

    @Override
    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public String readString(int length) {
        if (length == 0) return "";

        String value = new String(data, position, length, java.nio.charset.StandardCharsets.UTF_8);
        position += length;
        return value;
    }
}
