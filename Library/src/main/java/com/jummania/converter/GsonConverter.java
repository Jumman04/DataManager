
package com.jummania.converter;

import com.google.gson.Gson;
import com.jummania.DataManager;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * GsonConverter is an implementation of the Converter interface
 * that utilizes the Gson library for converting Java objects
 * to JSON format and vice versa.
 *
 * <p>This class provides methods to serialize Java objects to JSON strings
 * and to deserialize JSON strings back into Java objects using the Gson library.</p>
 * <p>
 * Created by Jummania on 08, December, 2024.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
public class GsonConverter implements DataManager.Converter {
    private final Gson gson;


    /**
     * Default constructor that initializes the Gson instance.
     */
    public GsonConverter() {
        gson = new Gson();
    }


    /**
     * Constructor that accepts a custom Gson instance.
     *
     * @param gson the Gson instance to be used for conversions
     */
    public GsonConverter(Gson gson) {
        this.gson = gson;
    }


    /**
     * Converts a Java object to its JSON representation.
     *
     * @param data the Java object to be converted to JSON
     * @param <T>  the type of the object
     * @return a JSON string representing the object
     */
    @Override
    public <T> String toJson(T data) {
        return gson.toJson(data);
    }


    /**
     * Converts a JSON string into a Java object of the specified type.
     *
     * @param json   the JSON string to be converted
     * @param tClass the type of the object to be returned
     * @param <T>    the type of the object
     * @return the Java object represented by the JSON string
     */
    @Override
    public <T> T fromJson(String json, Class<T> tClass) {
        return gson.fromJson(json, tClass);
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
        return gson.fromJson(json, typeOfT);
    }
}
