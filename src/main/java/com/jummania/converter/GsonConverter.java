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


    @Override
    public <T> String toJson(T data) {
        return gson.toJson(data);
    }


    @Override
    public void toJson(Object src, Type typeOfSrc, Appendable writer) {
        gson.toJson(src, typeOfSrc, writer);
    }


    @Override
    public <T> T fromJson(String json, Class<T> tClass) {
        return gson.fromJson(json, tClass);
    }


    @Override
    public <T> T fromReader(Reader json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }
}
