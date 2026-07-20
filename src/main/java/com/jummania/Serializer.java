package com.jummania;

import com.jummania.interfaces.Writer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;

public class Serializer {

    void serialize(Object obj, Writer writer) {
        serialize(obj, obj.getClass(), writer);
    }

    void serialize(Object obj, Class<?> clazz, Writer writer) {

        if (obj == null) {
            return;
        }

        try {

            // ---------- Primitive ----------
            if (clazz == Integer.class) {
                writer.writeInt((Integer) obj);
                return;
            }

            if (clazz == Long.class) {
                writer.writeLong((Long) obj);
                return;
            }

            if (clazz == Short.class) {
                writer.writeShort((Short) obj);
                return;
            }

            if (clazz == Byte.class) {
                writer.writeByte((Byte) obj);
                return;
            }

            if (clazz == Character.class) {
                writer.writeChar((Character) obj);
                return;
            }

            if (clazz == Boolean.class) {
                writer.writeBoolean((Boolean) obj);
                return;
            }

            if (clazz == Float.class) {
                writer.writeFloat((Float) obj);
                return;
            }

            if (clazz == Double.class) {
                writer.writeDouble((Double) obj);
                return;
            }

            if (clazz == String.class) {
                writer.writeString((String) obj);
                return;
            }

            // ---------- Array ----------
            if (clazz.isArray()) {

                int length = Array.getLength(obj);

                writer.writeInt(length);

                Class<?> componentType = clazz.getComponentType();

                for (int i = 0; i < length; i++) {

                    serialize(Array.get(obj, i), componentType, writer);
                }

                return;
            }

            // ---------- Collection ----------
            if (obj instanceof Collection<?> collection) {

                writer.writeInt(collection.size());

                for (Object item : collection) {

                    serialize(item, item.getClass(), writer);
                }

                return;
            }

            // ---------- Object ----------
            FastCache.FieldMap cache = FastCache.get(clazz);

            for (FastCache.CachedField cached : cache.fields()) {

                writer.writeInt(cached.nameBytes().length);
                writer.writeBytes(cached.nameBytes());

                Field field = cached.field();

                switch (cached.kind()) {

                    case FastCache.INT -> writer.writeInt(field.getInt(obj));

                    case FastCache.LONG -> writer.writeLong(field.getLong(obj));

                    case FastCache.SHORT -> writer.writeShort(field.getShort(obj));

                    case FastCache.BYTE -> writer.writeByte(field.getByte(obj));

                    case FastCache.CHAR -> writer.writeChar(field.getChar(obj));

                    case FastCache.BOOLEAN -> writer.writeBoolean(field.getBoolean(obj));

                    case FastCache.FLOAT -> writer.writeFloat(field.getFloat(obj));

                    case FastCache.DOUBLE -> writer.writeDouble(field.getDouble(obj));

                    case FastCache.STRING -> writer.writeString((String) field.get(obj));

                    default -> {
                        Object value = field.get(obj);

                        if (value != null) {
                            serialize(value, cached.rawType(), writer);
                        }
                    }
                }
            }

            writer.writeInt(-1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
