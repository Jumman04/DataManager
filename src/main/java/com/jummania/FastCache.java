package com.jummania;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class FastCache {

    public static final byte INT = 1;
    public static final byte LONG = 2;
    public static final byte SHORT = 3;
    public static final byte BYTE = 4;
    public static final byte CHAR = 5;
    public static final byte BOOLEAN = 6;
    public static final byte FLOAT = 7;
    public static final byte DOUBLE = 8;
    public static final byte STRING = 9;
    public static final byte ARRAY = 10;
    public static final byte COLLECTION = 11;
    public static final byte OBJECT = 12;

    private static final ConcurrentHashMap<Class<?>, FieldMap> CACHE =
            new ConcurrentHashMap<>();

    private FastCache() {
    }

    public static FieldMap get(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, FastCache::build);
    }

    private static FieldMap build(Class<?> clazz) {

        Field[] declared = clazz.getDeclaredFields();

        CachedField[] fields = new CachedField[declared.length];

        HashMap<String, CachedField> map =
                new HashMap<>((int) (declared.length / 0.75f) + 1);

        int count = 0;

        for (Field field : declared) {

            int modifiers = field.getModifiers();

            if (Modifier.isStatic(modifiers)
                    || Modifier.isTransient(modifiers)
                    || field.isSynthetic()) {
                continue;
            }

            field.setAccessible(true);

            String name = field.getName();

            Class<?> rawType = field.getType();

            byte kind = resolveKind(rawType);

            Type genericType =
                    kind == COLLECTION
                            ? field.getGenericType()
                            : rawType;

            CachedField cached =
                    new CachedField(
                            field,
                            rawType,
                            genericType,
                            name.getBytes(StandardCharsets.UTF_8),
                            kind
                    );

            fields[count++] = cached;

            map.put(name, cached);
        }

        if (count != fields.length) {
            fields = Arrays.copyOf(fields, count);
        }

        return new FieldMap(fields, map);
    }

    private static byte resolveKind(Class<?> type) {

        if (type == int.class || type == Integer.class)
            return INT;

        if (type == long.class || type == Long.class)
            return LONG;

        if (type == short.class || type == Short.class)
            return SHORT;

        if (type == byte.class || type == Byte.class)
            return BYTE;

        if (type == char.class || type == Character.class)
            return CHAR;

        if (type == boolean.class || type == Boolean.class)
            return BOOLEAN;

        if (type == float.class || type == Float.class)
            return FLOAT;

        if (type == double.class || type == Double.class)
            return DOUBLE;

        if (type == String.class)
            return STRING;

        if (type.isArray())
            return ARRAY;

        if (java.util.Collection.class.isAssignableFrom(type))
            return COLLECTION;

        return OBJECT;
    }

    public record CachedField(
            Field field,
            Class<?> rawType,
            Type genericType,
            byte[] nameBytes,
            byte kind
    ) {
    }

    public record FieldMap(
            CachedField[] fields,
            HashMap<String, CachedField> map
    ) {
    }
}