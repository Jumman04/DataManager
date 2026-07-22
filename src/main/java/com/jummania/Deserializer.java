package com.jummania;

import com.jummania.interfaces.Reader;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;

import static com.jummania.FastCache.UNSAFE;

@SuppressWarnings("unchecked")
public class Deserializer {
    @SuppressWarnings("unchecked")
    public <T> T deserialize(Type type, Reader reader) {

        try {

            Object primitive = readPrimitive(type, reader);

            if (primitive != null) return (T) primitive;

            // Array
            if (type instanceof Class<?> clazz && clazz.isArray()) {

                int length = reader.readInt();

                Class<?> componentType = clazz.getComponentType();

                Object array = Array.newInstance(componentType, length);

                for (int i = 0; i < length; i++) {
                    Array.set(array, i, deserialize(componentType, reader));
                }

                return (T) array;
            }

            // Collection<T>
            if (type instanceof ParameterizedType p) {

                Type rawType = p.getRawType();

                if (rawType instanceof Class<?> rawClass && Collection.class.isAssignableFrom(rawClass)) {

                    int size = reader.readInt();

                    Collection<Object> collection = new java.util.ArrayList<>(size);

                    Type itemType = p.getActualTypeArguments()[0];

                    for (int i = 0; i < size; i++) {
                        collection.add(deserialize(itemType, reader));
                    }

                    return (T) collection;
                }
            }

            if (!(type instanceof Class<?> clazz)) {
                throw new IllegalArgumentException("Unsupported type: " + type);
            }

            T object = (T) UNSAFE.allocateInstance(clazz);

            HashMap<String, FastCache.CachedField> map = FastCache.get(clazz).map();

            while (true) {

                int nameLength = reader.readInt();

                if (nameLength == -1) {
                    break;
                }

                FastCache.CachedField field = map.get(reader.readString(nameLength));

                if (field == null) continue;

                switch (field.kind()) {

                    case FastCache.INT -> field.field().setInt(object, reader.readInt());

                    case FastCache.INTEGER -> field.field().set(object, reader.readInt());

                    case FastCache.LONG -> field.field().setLong(object, reader.readLong());

                    case FastCache.LONG_OBJ -> field.field().set(object, reader.readLong());

                    case FastCache.SHORT -> field.field().setShort(object, reader.readShort());

                    case FastCache.SHORT_OBJ -> field.field().set(object, reader.readShort());

                    case FastCache.BYTE -> field.field().setByte(object, reader.readByte());

                    case FastCache.BYTE_OBJ -> field.field().set(object, reader.readByte());

                    case FastCache.CHAR -> field.field().setChar(object, reader.readChar());

                    case FastCache.CHARACTER -> field.field().set(object, reader.readChar());

                    case FastCache.BOOLEAN -> field.field().setBoolean(object, reader.readBoolean());

                    case FastCache.BOOLEAN_OBJ -> field.field().set(object, reader.readBoolean());

                    case FastCache.FLOAT -> field.field().setFloat(object, reader.readFloat());

                    case FastCache.FLOAT_OBJ -> field.field().set(object, reader.readFloat());

                    case FastCache.DOUBLE -> field.field().setDouble(object, reader.readDouble());

                    case FastCache.DOUBLE_OBJ -> field.field().set(object, reader.readDouble());

                    case FastCache.STRING -> field.field().set(object, reader.readString());

                    default -> field.field().set(object, deserialize(field.genericType(), reader));
                }
            }

            return object;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object readPrimitive(Type type, Reader reader) throws IOException {

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
}
