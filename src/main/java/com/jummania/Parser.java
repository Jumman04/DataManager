package com.jummania;

import java.util.ArrayList;
import java.util.List;

public final class Parser {

    private final Serializer serializer = new Serializer();
    private final Deserializer deserializer = new Deserializer();

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
