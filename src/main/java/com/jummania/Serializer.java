package com.jummania;

import com.jummania.interfaces.Writer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;

import static com.jummania.Parser.getFieldMap;

public class Serializer {

    void serialize(Object obj, Writer writer) {

        if (obj == null) {
            return;
        }

        Class<?> clazz = obj.getClass();

        try {

            // Primitive / String
            if (appendPrimitive(clazz, obj, writer)) {
                return;
            }

            // Array
            if (clazz.isArray()) {

                int length = Array.getLength(obj);

                writer.writeInt(length);

                for (int i = 0; i < length; i++) {
                    serialize(Array.get(obj, i), writer);
                }

                return;
            }

            // Collection
            if (obj instanceof Collection<?> collection) {

                writer.writeInt(collection.size());

                for (Object item : collection) {
                    serialize(item, writer);
                }

                return;
            }

            // Object
            for (Field field : getFieldMap(clazz).fields()) {

                Object value = field.get(obj);

                writer.writeString(field.getName());

                serialize(value, writer);
            }

            // End Of Object
            writer.writeInt(-1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean appendPrimitive(Class<?> clazz, Object obj, Writer writer) throws IOException {

        if (clazz == Integer.class) {
            writer.writeInt((Integer) obj);
            return true;
        }

        if (clazz == Long.class) {
            writer.writeLong((Long) obj);
            return true;
        }

        if (clazz == Short.class) {
            writer.writeShort((Short) obj);
            return true;
        }

        if (clazz == Byte.class) {
            writer.writeByte((Byte) obj);
            return true;
        }

        if (clazz == Character.class) {
            writer.writeChar((Character) obj);
            return true;
        }

        if (clazz == Boolean.class) {
            writer.writeBoolean((Boolean) obj);
            return true;
        }

        if (clazz == Float.class) {
            writer.writeFloat((Float) obj);
            return true;
        }

        if (clazz == Double.class) {
            writer.writeDouble((Double) obj);
            return true;
        }


        if (clazz == String.class) {
            writer.writeString((String) obj);
            return true;
        }

        return false;
    }

}
