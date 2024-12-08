package com.jummania.converter;

import com.jummania.DataManager;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * MoshiConverter is an implementation of the Converter interface
 * that utilizes the Moshi library for converting Java objects
 * to JSON format and vice versa.
 *
 * <p>This class provides methods to serialize Java objects to JSON strings
 * and to deserialize JSON strings back into Java objects using the Moshi library.</p>
 * <p>
 * Created by Jummania on 08, December, 2024.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
public class MoshiConverter implements DataManager.Converter {

    private final Moshi moshi;


    /**
     * Constructs a MoshiConverter with a given Moshi instance.
     *
     * @param moshi the Moshi instance to use for JSON conversion
     */
    public MoshiConverter(Moshi moshi) {
        this.moshi = moshi;
    }


    /**
     * Constructs a MoshiConverter with a default Moshi instance.
     */
    public MoshiConverter() {
        this.moshi = new Moshi.Builder().build();
    }


    /**
     * Converts a Java object to its JSON representation.
     *
     * @param data the Java object to be converted to JSON
     * @param <T>  the type of the object
     * @return a JSON string representing the object, or null if an error occurs
     */
    @Override
    public <T> String toJson(T data) {
        JsonAdapter<T> adapter = moshi.adapter((Type) data.getClass());
        return adapter.toJson(data);
    }


    /**
     * Converts a JSON string into a Java object of the specified type.
     *
     * @param json    the JSON string to be converted
     * @param typeOfT the type of the object to be returned
     * @param <T>     the type of the object
     * @return the Java object represented by the JSON string, or null if an error occurs
     */
    @Override
    public <T> T fromJson(String json, Type typeOfT) {
        JsonAdapter<T> adapter = moshi.adapter(Types.newParameterizedType(typeOfT));
        try {
            return adapter.fromJson(json);
        } catch (IOException e) {
            // Log the error or handle it as needed
            System.err.println("Error parsing JSON to object: " + e.getMessage());
            return null;
        }
    }


    /**
     * Converts a JSON stream from a Reader into a Java object of the specified type.
     *
     * @param json    the Reader containing the JSON data to be converted
     * @param typeOfT the type of the object to be returned
     * @param <T>     the type of the object
     * @return the Java object represented by the JSON data from the Reader, or null if an error occurs
     */
    @Override
    public <T> T fromReader(Reader json, Type typeOfT) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(json)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            // Log the error or handle it as needed
            System.err.println("Error reading JSON from Reader: " + e.getMessage());
        }
        return fromJson(stringBuilder.toString(), typeOfT);
    }
}
