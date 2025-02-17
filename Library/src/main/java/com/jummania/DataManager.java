package com.jummania;

import com.google.gson.JsonSyntaxException;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Interface for managing data storage and retrieval, including primitives, objects, and collections.
 * Provides methods to store, retrieve, and remove data, as well as register listeners for data changes.
 * <p>
 * Created by Jummania on 20, November, 2024.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
public interface DataManager {


    /**
     * Retrieves a stored String value associated with the specified key.
     * If no value is found, returns the provided default value.
     *
     * @param key      The key for the stored value.
     * @param defValue The default value to return if no value is found.
     * @return The stored String value or the default value.
     */
    String getString(String key, String defValue);


    /**
     * Retrieves the raw JSON string associated with the specified key from storage.
     *
     * <p>
     * This method reads the data from the storage file corresponding to the given key
     * and returns it as a plain JSON string. If no data is found for the specified key,
     * it will return null.
     * </p>
     *
     * @param key The unique key associated with the JSON data in storage. This key is used
     *            to locate and retrieve the corresponding data.
     * @return The raw JSON string associated with the specified key, or null if no data
     * exists for that key.
     * @throws IllegalArgumentException if the key is null or empty.
     * @throws RuntimeException         if an error occurs while reading from storage.
     * @see #saveString(String, String) (String, String) for saving a JSON string to storage.
     */
    String getRawString(String key);


    /**
     * Retrieves a stored int value associated with the specified key.
     * If no value is found, returns the provided default value.
     *
     * @param key      The key for the stored value.
     * @param defValue The default value to return if no value is found.
     * @return The stored int value or the default value.
     */
    int getInt(String key, int defValue);


    /**
     * Retrieves a stored long value associated with the specified key.
     * If no value is found, returns the provided default value.
     *
     * @param key      The key for the stored value.
     * @param defValue The default value to return if no value is found.
     * @return The stored long value or the default value.
     */
    long getLong(String key, long defValue);


    /**
     * Retrieves a stored float value associated with the specified key.
     * If no value is found, returns the provided default value.
     *
     * @param key      The key for the stored value.
     * @param defValue The default value to return if no value is found.
     * @return The stored float value or the default value.
     */
    float getFloat(String key, float defValue);


    /**
     * Retrieves a stored boolean value associated with the specified key.
     * If no value is found, returns the provided default value.
     *
     * @param key      The key for the stored value.
     * @param defValue The default value to return if no value is found.
     * @return The stored boolean value or the default value.
     */
    boolean getBoolean(String key, boolean defValue);


    /**
     * Retrieves a stored object associated with the specified key.
     *
     * @param key  The key for the stored object.
     * @param type The Type of the object to retrieve.
     * @param <T>  The type of the object.
     * @return The stored object of type T.
     */
    <T> T getObject(String key, Type type);


    /**
     * Retrieves a parameterized object associated with the specified key.
     *
     * @param key           The key for the stored object.
     * @param rawType       The raw type of the object.
     * @param typeArguments The type arguments for the parameterized object.
     * @param <T>           The type of the object.
     * @return The stored parameterized object of type T.
     */
    <T> T getParameterized(String key, Type rawType, Type... typeArguments);


    /**
     * Retrieves a list of objects associated with the specified key.
     *
     * @param key  The key for the stored list.
     * @param type The Type of the objects in the list.
     * @param <T>  The type of objects in the list.
     * @return A list of objects of type T.
     */
    <T> List<T> getList(String key, Type type);


    /**
     * Retrieves a String value associated with the specified key, with a default value of null if not found.
     *
     * @param key The key for the stored value.
     * @return The stored String value or null if no value is found.
     */
    default String getString(String key) {
        return getString(key, null);
    }


    /**
     * Retrieves an int value associated with the specified key, with a default value of 0 if not found.
     *
     * @param key The key for the stored value.
     * @return The stored int value or 0 if no value is found.
     */
    default int getInt(String key) {
        return getInt(key, 0);
    }


    /**
     * Retrieves a long value associated with the specified key, with a default value of 0L if not found.
     *
     * @param key The key for the stored value.
     * @return The stored long value or 0L if no value is found.
     */
    default long getLong(String key) {
        return getLong(key, 0L);
    }


    /**
     * Retrieves a float value associated with the specified key, with a default value of 0F if not found.
     *
     * @param key The key for the stored value.
     * @return The stored float value or 0F if no value is found.
     */
    default float getFloat(String key) {
        return getFloat(key, 0F);
    }


    /**
     * Retrieves a boolean value associated with the specified key, with a default value of false if not found.
     *
     * @param key The key for the stored value.
     * @return The stored boolean value or false if no value is found.
     */
    default boolean getBoolean(String key) {
        return getBoolean(key, false);
    }


    // Data modification methods

    /**
     * Stores a String value with the specified key.
     *
     * @param key   The key for the stored value.
     * @param value The String value to store.
     */
    void saveString(String key, String value);


    /**
     * Stores an int value with the specified key.
     *
     * @param key   The key for the stored value.
     * @param value The int value to store.
     */
    void saveInt(String key, int value);


    /**
     * Stores a long value with the specified key.
     *
     * @param key   The key for the stored value.
     * @param value The long value to store.
     */
    void saveLong(String key, long value);


    /**
     * Stores a float value with the specified key.
     *
     * @param key   The key for the stored value.
     * @param value The float value to store.
     */
    void saveFloat(String key, float value);


    /**
     * Stores a boolean value with the specified key.
     *
     * @param key   The key for the stored value.
     * @param value The boolean value to store.
     */
    void saveBoolean(String key, boolean value);


    /**
     * Stores an object with the specified key.
     *
     * @param key   The key for the stored object.
     * @param value The object to store.
     */
    void saveObject(String key, Object value);


    /**
     * Stores a list of objects with the specified key.
     *
     * @param key   The key for the stored list.
     * @param value The list of objects to store.
     * @param <T>   The type of objects in the list.
     */
    <T> void saveList(String key, List<T> value);


    /**
     * Stores a list of objects with the specified key, limiting the size of the list.
     *
     * @param key          The key for the stored list.
     * @param value        The list of objects to store.
     * @param maxArraySize The maximum allowed size of the list.
     * @param <T>          The type of objects in the list.
     */
    <T> void saveList(String key, List<T> value, int maxArraySize);


    /**
     * Adds a new value to the beginning of a JSON array associated with the given key.
     * If an existing JSON array is found, the new value is added as the first element
     * of the array, followed by the existing values.
     *
     * <p>
     * This method uses a StringBuilder for efficient string manipulation to construct the
     * JSON array and reduces memory overhead by avoiding multiple string creations.
     * </p>
     *
     * @param key   The unique key associated with the JSON array in storage. This key is used
     *              to retrieve the existing data and save the updated JSON array.
     * @param value The new value to be added to the JSON array. This value will be converted
     *              to a JSON string format before being saved.
     * @throws IllegalArgumentException if the key is null or empty.
     * @throws RuntimeException         if an error occurs while saving the JSON string to storage.
     * @see #toJson(Object) for converting the object to its JSON representation.
     * @see #getRawString(String) for retrieving the existing JSON string associated with the key.
     * @see #saveString(String, String) for saving the constructed JSON string back to storage.
     */
    void prependToList(String key, Object value);


    /**
     * Adds a new value to the end of a JSON array associated with the given key.
     * If an existing JSON array is found, the new value is added as the last element
     * of the array.
     *
     * <p>
     * This method uses a StringBuilder for efficient string manipulation to construct the
     * JSON array and reduces memory overhead by avoiding multiple string creations.
     * </p>
     *
     * @param key   The unique key associated with the JSON array in storage. This key is used
     *              to retrieve the existing data and save the updated JSON array.
     * @param value The new value to be added to the JSON array. This value will be converted
     *              to a JSON string format before being saved.
     * @throws IllegalArgumentException if the key is null or empty.
     * @throws RuntimeException         if an error occurs while saving the JSON string to storage.
     * @see #toJson(Object) for converting the object to its JSON representation.
     * @see #getRawString(String) for retrieving the existing JSON string associated with the key.
     * @see #saveString(String, String) (String, String) for saving the constructed JSON string back to storage.
     */
    void appendToList(String key, Object value);


    /**
     * Converts the provided JSON string to an object of the specified type.
     * <p>
     * This method uses Gson to deserialize the JSON string into an object of the specified type.
     * The type parameter allows the conversion to a specific object type.
     * </p>
     *
     * @param value   the JSON string to deserialize.
     * @param typeOfT the type of the object to deserialize into.
     * @param <T>     the type of the object.
     * @return the deserialized object of type T.
     * @throws JsonSyntaxException if the JSON string is not a valid representation for the specified type.
     */
    <T> T fromJson(String value, Type typeOfT);


    /**
     * Converts a JSON stream from a Reader into a Java object of the specified type.
     *
     * @param json    the Reader containing the JSON data to be converted
     * @param typeOfT the type of the object to be returned
     * @param <T>     the type of the object
     * @return the Java object represented by the JSON data from the Reader
     */
    <T> T fromReader(Reader json, Type typeOfT);


    /**
     * Converts the given object to a JSON string.
     * <p>
     * This method uses Gson to serialize an object into its JSON representation.
     * It can handle any object type, converting it into a JSON string.
     * </p>
     *
     * @param object the object to serialize into JSON.
     * @return the JSON string representation of the object.
     * @throws JsonSyntaxException if the object cannot be serialized.
     */
    String toJson(Object object);


    /**
     * Removes the stored value associated with the specified key.
     *
     * @param key The key for the value to remove.
     */
    void remove(String key);


    /**
     * Clears all stored data.
     */
    void clear();


    /**
     * Checks if a value associated with the specified key exists in the storage.
     *
     * @param key The key to check.
     * @return true if the key exists, false otherwise.
     */
    boolean contains(String key);


    // Data change listener registration

    /**
     * Registers a listener to be notified when data associated with a key changes.
     *
     * @param listener The listener to be registered.
     */
    void registerOnDataChangeListener(OnDataChangeListener listener);


    /**
     * Unregisters the currently registered data change listener.
     */
    void unregisterOnDataChangeListener();


    /**
     * Interface for listeners to be notified when data changes.
     */
    interface OnDataChangeListener {


        /**
         * Called when data associated with a key changes.
         *
         * @param key The key whose data has changed.
         */
        void onDataChanged(String key);
    }


    /**
     * The Converter interface provides methods for converting
     * data between JSON format and Java objects.
     */
    interface Converter {


        /**
         * Converts a Java object to its JSON representation.
         *
         * @param data the Java object to be converted to JSON
         * @param <T>  the type of the object
         * @return a JSON string representing the object
         */
        <T> String toJson(T data);


        /**
         * Converts a JSON string into a Java object of the specified type.
         *
         * @param json    the JSON string to be converted
         * @param typeOfT the type of the object to be returned
         * @param <T>     the type of the object
         * @return the Java object represented by the JSON string
         */
        <T> T fromJson(String json, Type typeOfT);


        /**
         * Converts a JSON stream from a Reader into a Java object of the specified type.
         *
         * @param json    the Reader containing the JSON data to be converted
         * @param typeOfT the type of the object to be returned
         * @param <T>     the type of the object
         * @return the Java object represented by the JSON data from the Reader
         */
        <T> T fromReader(Reader json, Type typeOfT);
    }


}