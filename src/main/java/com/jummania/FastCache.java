package com.jummania;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

final class FastCache {

    static final byte INT = 1;
    static final byte INTEGER = 2;

    static final byte LONG = 3;
    static final byte LONG_OBJ = 4;

    static final byte SHORT = 5;
    static final byte SHORT_OBJ = 6;

    static final byte BYTE = 7;
    static final byte BYTE_OBJ = 8;

    static final byte CHAR = 9;
    static final byte CHARACTER = 10;

    static final byte BOOLEAN = 11;
    static final byte BOOLEAN_OBJ = 12;

    static final byte FLOAT = 13;
    static final byte FLOAT_OBJ = 14;

    static final byte DOUBLE = 15;
    static final byte DOUBLE_OBJ = 16;

    static final byte STRING = 17;

    static final byte ARRAY = 18;
    static final byte COLLECTION = 19;
    static final byte OBJECT = 20;

    static final Unsafe UNSAFE;
    private static final ConcurrentHashMap<Class<?>, FieldMap> CACHE = new ConcurrentHashMap<>();

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FastCache() {
    }

    static FieldMap get(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, FastCache::build);
    }

    private static FieldMap build(Class<?> clazz) {

        Field[] declared = clazz.getDeclaredFields();

        CachedField[] fields = new CachedField[declared.length];

        HashMap<String, CachedField> map = new HashMap<>((int) (declared.length / 0.75f) + 1);

        int count = 0;

        for (Field field : declared) {

            int modifiers = field.getModifiers();

            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic()) {
                continue;
            }

            field.setAccessible(true);

            String name = field.getName();

            Class<?> rawType = field.getType();

            byte kind = resolveKind(rawType);

            Type genericType = kind == COLLECTION ? field.getGenericType() : rawType;

            CachedField cached = new CachedField(field, rawType, genericType, name.getBytes(StandardCharsets.UTF_8), kind);

            fields[count++] = cached;

            map.put(name, cached);
        }

        if (count != fields.length) {
            fields = Arrays.copyOf(fields, count);
        }

        return new FieldMap(fields, map);
    }

    private static byte resolveKind(Class<?> type) {

        if (type == int.class) return INT;
        if (type == Integer.class) return INTEGER;

        if (type == long.class) return LONG;
        if (type == Long.class) return LONG_OBJ;

        if (type == short.class) return SHORT;
        if (type == Short.class) return SHORT_OBJ;

        if (type == byte.class) return BYTE;
        if (type == Byte.class) return BYTE_OBJ;

        if (type == char.class) return CHAR;
        if (type == Character.class) return CHARACTER;

        if (type == boolean.class) return BOOLEAN;
        if (type == Boolean.class) return BOOLEAN_OBJ;

        if (type == float.class) return FLOAT;
        if (type == Float.class) return FLOAT_OBJ;

        if (type == double.class) return DOUBLE;
        if (type == Double.class) return DOUBLE_OBJ;

        if (type == String.class) return STRING;

        if (type.isArray()) return ARRAY;

        if (java.util.Collection.class.isAssignableFrom(type)) return COLLECTION;

        return OBJECT;
    }

    record CachedField(Field field, Class<?> rawType, Type genericType, byte[] nameBytes, byte kind) {
    }

    record FieldMap(CachedField[] fields, HashMap<String, CachedField> map) {
    }
}