package com.jummania;

import com.jummania.interfaces.Reader;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

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

            // Normal object
            if (!(type instanceof Class<?> clazz)) {
                throw new IllegalArgumentException("Unsupported type: " + type);
            }

            T object = (T) UNSAFE.allocateInstance(clazz);

            while (true) {

                int length = reader.readInt();

                if (length == -1) {
                    break;
                }

                String fieldName = reader.readString(length);

                FastCache.CachedField field = FastCache.get(clazz).map().get(fieldName);

                if (field == null) return null;

                Type fieldType = field.genericType();

                Object value = deserialize(fieldType, reader);

                field.field().set(object, value);
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
