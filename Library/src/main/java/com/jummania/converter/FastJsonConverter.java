package com.jummania.converter;

import com.alibaba.fastjson.JSON;
import com.jummania.DataManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * FastJsonConverter is an implementation of the Converter interface
 * that utilizes the FastJSON library for converting Java objects
 * to JSON format and vice versa.
 *
 * <p>This class provides methods to serialize Java objects to JSON strings
 * and to deserialize JSON strings back into Java objects.</p>
 * <p>
 * Created by Jummania on 08, December, 2024.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
public class FastJsonConverter implements DataManager.Converter {


    /**
     * Converts a Java object to its JSON representation.
     *
     * @param data the Java object to be converted to JSON
     * @param <T>  the type of the object
     * @return a JSON string representing the object
     */
    @Override
    public <T> String toJson(T data) {
        return JSON.toJSONString(data);
    }


    /**
     * Converts a JSON string into a Java object of the specified type.
     *
     * @param json    the JSON string to be converted
     * @param typeOfT the type of the object to be returned
     * @param <T>     the type of the object
     * @return the Java object represented by the JSON string
     */
    @Override
    public <T> T fromJson(String json, Type typeOfT) {
        return JSON.parseObject(json, typeOfT);
    }


    /**
     * Converts a JSON stream from a Reader into a Java object of the specified type.
     *
     * @param json    the Reader containing the JSON data to be converted
     * @param typeOfT the type of the object to be returned
     * @param <T>     the type of the object
     * @return the Java object represented by the JSON data from the Reader
     */
    @Override
    public <T> T fromReader(Reader json, Type typeOfT) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(json)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            // Log the exception or handle it as needed
            System.err.println("Error reading JSON from Reader: " + e.getMessage());
        }

        // Convert the accumulated JSON string to the specified type
        return fromJson(sb.toString(), typeOfT);
    }
}
