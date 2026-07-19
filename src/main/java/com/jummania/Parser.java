package com.jummania;

import com.jummania.interfaces.Reader;
import com.jummania.interfaces.Writer;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class Parser {

    private static final Unsafe UNSAFE;
    private static final ConcurrentHashMap<Class<?>, FieldMap> FIELD_CACHE = new ConcurrentHashMap<>();

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void main() throws Exception {
        //  Writer writer = new Writer(14, "hello world");
        //  byte[] ser = serialize(writer);
        //  System.out.println(deserialize(ser, Writer.class));
    }

    private static FieldMap getFieldMap(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, c -> {

            Field[] fields = c.getDeclaredFields();

            HashMap<String, Field> map = new HashMap<>(fields.length * 2);

            for (Field field : fields) {

                field.setAccessible(true);

                map.put(field.getName(), field);
            }

            return new FieldMap(fields, map);
        });
    }

    public static byte[] serialize(Object obj) {
        ByteBuilder sb = new ByteBuilder();
        serialize(obj, sb);
        return sb.toByteArray();
    }

    private static void serialize(Object obj, Writer writer) {

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

    private static boolean appendPrimitive(Class<?> clazz, Object obj, Writer writer) throws IOException {

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


    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes, Class<T> clazz, Reader reader) throws Exception {

        try {

            Object primitive = readPrimitive(clazz, reader);

            if (primitive != null) return (T) primitive;

            T object = (T) UNSAFE.allocateInstance(clazz);


            int length;
            while ((length = reader.readInt()) != -1) {
                String fieldName = reader.readString(length);

                Field field = getFieldMap(clazz).map().get(fieldName);

                if (field == null) return null;

                readAndSet(field, object, reader);
            }


            return object;

        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private static Object readPrimitive(Class<?> type, Reader reader) throws IOException {

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

    private static void readAndSet(Field field, Object object, Reader reader) throws Exception {

        Class<?> type = field.getType();

        if (type == int.class) {
            field.setInt(object, reader.readInt());
        } else if (type == Integer.class) {
            field.set(object, reader.readInt());
        } else if (type == long.class) {
            field.setLong(object, reader.readLong());
        } else if (type == Long.class) {
            field.set(object, reader.readLong());
        } else if (type == short.class) {
            field.setShort(object, reader.readShort());
        } else if (type == Short.class) {
            field.set(object, reader.readShort());
        } else if (type == byte.class) {
            field.setByte(object, reader.readByte());
        } else if (type == Byte.class) {
            field.set(object, reader.readByte());
        } else if (type == char.class) {
            field.setChar(object, reader.readChar());
        } else if (type == Character.class) {
            field.set(object, reader.readChar());
        } else if (type == boolean.class) {
            field.setBoolean(object, reader.readBoolean());
        } else if (type == Boolean.class) {
            field.set(object, reader.readBoolean());
        } else if (type == float.class) {
            field.setFloat(object, reader.readFloat());
        } else if (type == Float.class) {
            field.set(object, reader.readFloat());
        } else if (type == double.class) {
            field.setDouble(object, reader.readDouble());
        } else if (type == Double.class) {
            field.set(object, reader.readDouble());
        } else if (type == String.class) {
            field.set(object, reader.readString());
        }
    }
}
