package com.jummania;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class Parser {

    static final Unsafe UNSAFE;
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

    private final Serializer serializer = new Serializer();
    private final Deserializer deserializer = new Deserializer();

    static FieldMap getFieldMap(Class<?> clazz) {
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

    void main() {


        User user = new User(14, "hello world");

        List<User> list = new ArrayList<>();
        list.add(user);
        list.add(user);

        Users users = new Users(list);

        byte[] ser = serialize(users);
        Users l = deserializer.deserialize(Users.class, new ByteReader(ser));
        System.out.println(l);
    }

    public byte[] serialize(Object obj) {
        ByteBuilder sb = new ByteBuilder();
        serializer.serialize(obj, sb);
        return sb.toByteArray();
    }


    private class Users {
        List<User> users;

        public Users(List<User> users) {
            this.users = users;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (User user : users) {
                stringBuilder.append(user).append("\n");
            }
            return stringBuilder.toString();
        }
    }

    private class User {
        int id;
        String name;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return id + " : " + name;
        }
    }
}
