package com.jummania.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jummania.DataManager;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * JacksonConverter is an implementation of the Converter interface
 * that utilizes the Jackson library for converting Java objects
 * to JSON format and vice versa.
 *
 * <p>This class provides methods to serialize Java objects to JSON strings
 * and to deserialize JSON strings back into Java objects using the Jackson library.</p>
 * <p>
 * Created by Jummania on 08, December, 2024.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
public class JacksonConverter implements DataManager.Converter {
    private final ObjectMapper objectMapper;


    /**
     * Default constructor that initializes the ObjectMapper instance.
     */
    public JacksonConverter() {
        objectMapper = new ObjectMapper();
    }


    /**
     * Constructor that accepts a custom ObjectMapper instance.
     *
     * @param objectMapper the ObjectMapper instance to be used for conversions
     */
    public JacksonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    /**
     * Converts a Java object to its JSON representation.
     *
     * @param data the Java object to be converted to JSON
     * @param <T>  the type of the object
     * @return a JSON string representing the object, or null if conversion fails
     */
    @Override
    public <T> String toJson(T data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            // Log the error or handle it as needed
            System.err.println("Error converting to JSON: " + e.getMessage());
            return null;
        }
    }


    /**
     * Converts a JSON string into a Java object of the specified type.
     *
     * @param json    the JSON string to be converted
     * @param typeOfT the type of the object to be returned
     * @param <T>     the type of the object
     * @return the Java object represented by the JSON string, or null if conversion fails
     */
    @Override
    public <T> T fromJson(String json, Type typeOfT) {
        try {
            return objectMapper.readValue(json, objectMapper.constructType(typeOfT));
        } catch (JsonProcessingException e) {
            // Log the error or handle it as needed
            System.err.println("Error reading JSON from string: " + e.getMessage());
            return null;
        }
    }


    /**
     * Converts a JSON stream from a Reader into a Java object of the specified type.
     *
     * @param json    the Reader containing the JSON data to be converted
     * @param typeOfT the type of the object to be returned
     * @param <T>     the type of the object
     * @return the Java object represented by the JSON data from the Reader, or null if conversion fails
     */
    @Override
    public <T> T fromReader(Reader json, Type typeOfT) {
        try {
            return objectMapper.readValue(json, objectMapper.constructType(typeOfT));
        } catch (IOException e) {
            // Log the error or handle it as needed
            System.err.println("Error reading JSON from Reader: " + e.getMessage());
            return null;
        }
    }
}
