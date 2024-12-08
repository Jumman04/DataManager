package com.jummania.converter;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.jummania.DataManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * JsonIterConverter is an implementation of the Converter interface
 * that utilizes the Jsoniter library for converting Java objects
 * to JSON format and vice versa.
 *
 * <p>This class provides methods to serialize Java objects to JSON strings
 * and to deserialize JSON strings back into Java objects using the Jsoniter library.</p>
 * <p>
 * Created by Jummania on 08, December, 2024.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
public class JsonIterConverter implements DataManager.Converter {


    /**
     * Converts a Java object to its JSON representation.
     *
     * @param data the Java object to be converted to JSON
     * @param <T>  the type of the object
     * @return a JSON string representing the object
     */
    @Override
    public <T> String toJson(T data) {
        return JsonStream.serialize(data);
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
        return JsonIterator.deserialize(json, (Class<T>) typeOfT);
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
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(json)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException i) {
            // Log the error or handle it as needed
            System.err.println("Error reading JSON from Reader: " + i.getMessage());
        }
        return fromJson(stringBuilder.toString(), typeOfT);
    }
}
