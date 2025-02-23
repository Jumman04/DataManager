package com.jummania;


import java.io.File;

/**
 * Created by Jummania on 23/2/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
public class Test {
    public static void main(String[] args) {

        DataManager dataManager = DataManagerFactory.create(new File("/home/jumman/Desktop/TEST DATAMANAGER/"));
        // dataManager.clear();
        /*
        List<String> list = new ArrayList<>();
        list.add("Hello");
        list.add("World");
        dataManager.saveList("key", list);

        dataManager.appendToList("key", 0, 0);

         */

        dataManager.clear();
        dataManager.appendToList("key", new char[0]);
        dataManager.appendToList("key", "null");
    }
}
