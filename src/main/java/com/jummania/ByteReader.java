package com.jummania;

public final class ByteReader {

    private final byte[] data;
    private int position;

    public ByteReader(byte[] data) {
        this.data = data;
    }

    public boolean hasRemaining() {
        return position < data.length;
    }

    public byte readByte() {
        return data[position++];
    }

    public boolean readBoolean() {
        return readByte() != 0;
    }

    public short readShort() {
        return (short) (
                ((data[position++] & 0xFF) << 8) |
                        (data[position++] & 0xFF)
        );
    }

    public char readChar() {
        return (char) readShort();
    }

    public int readInt() {
        return ((data[position++] & 0xFF) << 24)
                | ((data[position++] & 0xFF) << 16)
                | ((data[position++] & 0xFF) << 8)
                | (data[position++] & 0xFF);
    }

    public long readLong() {
        return ((long) (data[position++] & 0xFF) << 56)
                | ((long) (data[position++] & 0xFF) << 48)
                | ((long) (data[position++] & 0xFF) << 40)
                | ((long) (data[position++] & 0xFF) << 32)
                | ((long) (data[position++] & 0xFF) << 24)
                | ((long) (data[position++] & 0xFF) << 16)
                | ((long) (data[position++] & 0xFF) << 8)
                | ((long) (data[position++] & 0xFF));
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public String readString() {

        int length = readInt();

        String value = new String(
                data,
                position,
                length,
                java.nio.charset.StandardCharsets.UTF_8
        );

        position += length;

        return value;
    }
}
