package com.jummania;


import com.jummania.interfaces.Reader;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class StreamByteReader implements Reader {

    private final DataInputStream in;

    private byte[] buffer = new byte[256];

    public StreamByteReader(DataInputStream in) {
        this.in = in;
    }

    @Override
    public byte readByte() throws IOException {
        return in.readByte();
    }

    @Override
    public boolean readBoolean() throws IOException {
        return in.readBoolean();
    }

    @Override
    public short readShort() throws IOException {
        return in.readShort();
    }

    @Override
    public char readChar() throws IOException {
        return in.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return in.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return in.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return in.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return in.readDouble();
    }

    @Override
    public String readString(int length) throws IOException {
        if (length == 0) return "";

        byte[] bytes = getBuffer(length);

        in.readFully(bytes, 0, length);

        return new String(bytes, 0, length, StandardCharsets.UTF_8);
    }

    private byte[] getBuffer(int size) {

        if (buffer.length < size) {
            buffer = new byte[size];
        }

        return buffer;
    }
}
