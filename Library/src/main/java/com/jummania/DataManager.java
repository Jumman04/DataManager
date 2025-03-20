package com.jummania;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

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
     * Retrieves the raw JSON string associated with the given key.
     *
     * @param key The key used to identify the stored JSON data.
     * @return The raw JSON string if the file exists and is readable; otherwise, {@code null}.
     */
    String getRawString(String key);


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
     * Retrieves a list of objects associated with the specified key.
     *
     * @param key  The key for the stored list.
     * @param type The Type of the objects in the list.
     * @param <T>  The type of objects in the list.
     * @return A list of objects of type T.
     */
    <T> List<T> getList(String key, Type type);


    /**
     * Retrieves a {@link Reader} for reading the stored JSON data associated with the given key.
     *
     * @param key The key used to identify the stored file.
     * @return A {@link Reader} instance if the file exists and is readable; otherwise, {@code null}.
     * @throws IllegalArgumentException If the key is {@code null}.
     */
    Reader getReader(String key);


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
     * @param <E>   The type of objects in the list.
     */
    <E> void saveList(String key, List<E> value);


    /**
     * Stores a list of objects with the specified key, limiting the size of the list.
     *
     * @param key          The key for the stored list.
     * @param value        The list of objects to store.
     * @param maxArraySize The maximum allowed size of the list.
     * @param <E>          The type of objects in the list.
     */
    <E> void saveList(String key, List<E> value, int maxArraySize);


    /**
     * Appends an element at the specified index in a JSON-stored list.
     *
     * @param key             The key associated with the list in storage.
     * @param index           The position where the new element should be inserted.
     *                        Use -1 to append at the end.
     * @param element         The element to be added to the list.
     * @param removeDuplicate If true, removes any existing occurrences of the element before adding.
     * @throws IndexOutOfBoundsException If the index is out of range for the list size.
     * @throws IllegalArgumentException  If the stored list type does not match the element's type.
     */
    void appendToList(String key, int index, Object element, boolean removeDuplicate);


    /**
     * Appends an element at the specified index in a JSON-stored list without removing duplicates.
     *
     * @param key     The key associated with the list in storage.
     * @param index   The position where the new element should be inserted.
     * @param element The element to be added to the list.
     * @throws IndexOutOfBoundsException If the index is out of range for the list size.
     * @throws IllegalArgumentException  If the stored list type does not match the element's type.
     */
    default void appendToList(String key, int index, Object element) {
        appendToList(key, index, element, false);
    }


    /**
     * Appends an element to the end of a JSON-stored list with an option to remove duplicates.
     *
     * @param key             The key associated with the list in storage.
     * @param element         The element to be added to the list.
     * @param removeDuplicate If true, removes any existing occurrences of the element before adding.
     * @throws IllegalArgumentException If the stored list type does not match the element's type.
     */
    default void appendToList(String key, Object element, boolean removeDuplicate) {
        appendToList(key, -1, element, removeDuplicate);
    }


    /**
     * Appends an element to the end of a JSON-stored list without removing duplicates.
     *
     * @param key     The key associated with the list in storage.
     * @param element The element to be added to the list.
     * @throws IllegalArgumentException If the stored list type does not match the element's type.
     */
    default void appendToList(String key, Object element) {
        appendToList(key, -1, element, false);
    }


    /**
     * Removes an element at the specified index from a JSON-stored list.
     *
     * <p>This method retrieves the list associated with the given key, removes the element
     * at the specified index, and updates the storage.</p>
     *
     * @param key   The unique identifier for the stored list. Must not be null.
     * @param index The zero-based position of the element to be removed.
     * @throws IndexOutOfBoundsException If the index is out of bounds for the list size.
     */
    void removeFromList(String key, int index);


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
     * Returns a parameterized {@link Type} representing a generic type with the specified raw type
     * and type arguments.
     * <p>
     * This method is useful when working with Java's generic type system at runtime, particularly
     * when using libraries like Gson that require type information for deserialization.
     * </p>
     *
     * @param rawType       The raw class type that represents the generic type.
     * @param typeArguments The type arguments that should be applied to the raw type.
     *                      These define the generic parameters of the raw type.
     * @return A {@link Type} representing the parameterized type with the specified type arguments.
     * @throws IllegalArgumentException if the number of type arguments is inconsistent
     *                                  with the number of parameters required by the raw type.
     * @see TypeToken#getParameterized(Type, Type...)
     * @see java.lang.reflect.ParameterizedType
     */
    Type getParameterized(Type rawType, Type... typeArguments);


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
     * Registers a {@link DataObserver} to listen for data changes.
     *
     * @param observer The observer to be registered.
     */
    void addDataObserver(DataObserver observer);

    /**
     * Unregisters the currently registered {@link DataObserver}.
     */
    void removeDataObserver();


    /**
     * Observer interface for receiving notifications about data changes and errors.
     */
    interface DataObserver {

        /**
         * Called when the data associated with a specific key has changed.
         *
         * @param key The key whose associated data has changed.
         */
        void onDataChange(String key);

        /**
         * Called when an error occurs while processing data.
         *
         * @param error The exception or error encountered.
         */
        void onError(Throwable error);
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