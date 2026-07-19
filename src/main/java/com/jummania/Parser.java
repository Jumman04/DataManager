package com.jummania;

import sun.misc.Unsafe;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class Parser {

    private static final Unsafe UNSAFE;
    private static final ConcurrentHashMap<Class<?>, Field[]> FIELD_CACHE = new ConcurrentHashMap<>();

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T newInstance(Class<T> clazz) {
        try {
            return (T) UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    static void main() throws Exception {
        Writer writer = new Writer(14, "hello world");
        byte[] ser = serialize(writer);
        System.out.println(deserialize(ser, String.class));
    }

    private static Field[] getFields(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, c -> {
            Field[] fields = c.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
            }

            return fields;
        });
    }

    public static byte[] serialize(Object obj) {
        ByteBuilder sb = new ByteBuilder();
        serialize(obj, sb);
        return sb.toByteArray();
    }

    private static void serialize(Object obj, ByteBuilder sb) {

        if (obj == null) return;

        Class<?> clazz = obj.getClass();

        if (appendPrimitive(clazz, obj, sb)) return;


        // Array
        if (clazz.isArray()) {

            int length = Array.getLength(obj);

            sb.writeInt(length);

            for (int i = 0; i < length; i++) {
                serialize(Array.get(obj, i), sb);
            }

            return;
        }

        // Collection
        if (obj instanceof Collection<?> collection) {

            sb.writeInt(collection.size());

            for (Object item : collection) {
                serialize(item, sb);
            }

            return;
        }

        try {
            // Object fields
            for (Field field : getFields(clazz)) {

                String name = field.getName();
                Object value = null;

                value = field.get(obj);

                sb.writeString(name);
                serialize(value, sb);
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }

    private static boolean appendPrimitive(Class<?> clazz, Object obj, ByteBuilder sb) {

        if (clazz == Integer.class) {
            sb.writeInt((Integer) obj);
            return true;
        }

        if (clazz == Long.class) {
            sb.writeLong((Long) obj);
            return true;
        }

        if (clazz == Short.class) {
            sb.writeShort((Short) obj);
            return true;
        }

        if (clazz == Byte.class) {
            sb.writeByte((Byte) obj);
            return true;
        }

        if (clazz == Character.class) {
            sb.writeChar((Character) obj);
            return true;
        }

        if (clazz == Boolean.class) {
            sb.writeBoolean((Boolean) obj);
            return true;
        }

        if (clazz == Float.class) {
            sb.writeFloat((Float) obj);
            return true;
        }

        if (clazz == Double.class) {
            sb.writeDouble((Double) obj);
            return true;
        }


        if (clazz == String.class) {
            sb.writeString((String) obj);
            return true;
        }

        return false;
    }


    public static <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {

        try {

            ByteReader reader = new ByteReader(bytes);

            Object primitive = readPrimitive(clazz, reader);

            if (primitive != null) return (T) primitive;

            T object = newInstance(clazz);

            while (reader.hasRemaining()) {

                String fieldName = reader.readString();

                Field field;

                try {
                    field = clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    return null; // mismatch
                }

                field.setAccessible(true);

                Object value = readPrimitive(field.getType(), reader);

                field.set(object, value);
            }

            return object;

        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private static Object readPrimitive(Class<?> type, ByteReader reader) {

        if (type == int.class || type == Integer.class) return reader.readInt();

        if (type == long.class || type == Long.class) return reader.readLong();

        if (type == short.class || type == Short.class) return reader.readShort();

        if (type == byte.class || type == Byte.class) return reader.readByte();

        if (type == char.class || type == Character.class) return reader.readChar();

        if (type == boolean.class || type == Boolean.class) return reader.readBoolean();

        if (type == float.class || type == Float.class) return reader.readFloat();

        if (type == double.class || type == Double.class) return reader.readDouble();

        if (type == String.class) return reader.readString();

        return null;
    }


    public static class Writers {
        final List<Writer> list = new ArrayList<>();

        public Writers() {
            list.add(new Writer(13, "jumman"));
            list.add(new Writer(11111, "111111"));
        }
    }

    public static class Writer {
        private final int id;
        private final String title;

        public Writer(int id, String title) {
            this.id = id;
            this.title = title;
        }

        @Override
        public String toString() {
            return id + " : " + title;
        }
    }
}
