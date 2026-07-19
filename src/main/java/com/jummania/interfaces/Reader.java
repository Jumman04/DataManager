package com.jummania.interfaces;

import java.io.IOException;

public interface Reader {

    byte readByte() throws IOException;

    boolean readBoolean() throws IOException;

    short readShort() throws IOException;

    char readChar() throws IOException;

    int readInt() throws IOException;

    long readLong() throws IOException;

    float readFloat() throws IOException;

    double readDouble() throws IOException;

    String readString(int length) throws IOException;

    default String readString() throws IOException {
        return readString(readInt());
    }
}
