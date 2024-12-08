package com.jummania.converter;

import static kotlinx.serialization.SerializersKt.serializer;

import com.jummania.DataManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import kotlinx.serialization.KSerializer;
import kotlinx.serialization.json.Json;

/**
 * A converter that uses Kotlinx Serialization for serializing and deserializing
 * objects to and from JSON format.
 *
 * <p>Created by Jummania on 08, December, 2024.</p>
 * <p>Email: sharifuddinjumman@gmail.com</p>
 * <p>Location: Dhaka, Bangladesh.</p>
 */
public class KotlinxSerializationConverter implements DataManager.Converter {

    private final Json json;

    /**
     * Constructs a KotlinxSerializationConverter using the default JSON configuration.
     */
    public KotlinxSerializationConverter() {
        this.json = Json.Default;
    }

    /**
     * Constructs a KotlinxSerializationConverter using a custom Json instance.
     *
     * @param json the Json instance to use for serialization/deserialization
     */
    public KotlinxSerializationConverter(Json json) {
        this.json = json;
    }

    /**
     * Converts the given data object to its JSON representation as a String.
     *
     * @param data the data object to serialize, must be annotated with @Serializable
     * @param <T>  the type of the data object
     * @return the JSON representation of the data object
     * @throws IllegalArgumentException if the data is not annotated with @Serializable
     */
    @Override
    public <T> String toJson(T data) {
        if (!(data instanceof kotlinx.serialization.Serializable)) {
            throw new IllegalArgumentException("Data must be @Serializable");
        }
        // Use Kotlinx Serialization's Json serialization
        return json.encodeToString(serializer(data.getClass()), data);
    }

    /**
     * Deserializes the given JSON content into an object of the specified type.
     *
     * @param jsonContent the JSON string to deserialize
     * @param typeOfT     the type of the resulting object
     * @param <T>         the type of the resulting object
     * @return the deserialized object of type T
     */
    @Override
    public <T> T fromJson(String jsonContent, Type typeOfT) {
        return json.decodeFromString((KSerializer<T>) serializer(typeOfT), jsonContent);
    }

    /**
     * Reads JSON content from the given Reader and deserializes it into an object of the specified type.
     *
     * @param json    the Reader providing the JSON content
     * @param typeOfT the type of the resulting object
     * @param <T>     the type of the resulting object
     * @return the deserialized object of type T
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
