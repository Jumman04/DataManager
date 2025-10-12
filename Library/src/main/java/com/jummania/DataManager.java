package com.jummania;

import com.jummania.model.PaginatedData;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Predicate;

/**
 * Interface for managing data operations such as saving, retrieving, and deleting key-value data,
 * including support for JSON serialization, pagination, and list management.
 * <p>
 * Implementations of this interface should provide persistent or in-memory storage functionality.
 * <p>
 * Created by Jummania on 20, November, 2024.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */


public interface DataManager {


    /**
     * Retrieves a string value associated with the given key.
     * If no value is found or the value is null, returns the provided default value.
     *
     * @param key      the key to look up the value
     * @param defValue the default value to return if no value is found or if the result is null
     * @return the retrieved string value or the default value if not found
     */
    default String getString(String key, String defValue) {
        String value = getObject(key, String.class);
        return value != null ? value : defValue;
    }


    /**
     * Retrieves a string value associated with the given key.
     * If no value is found or the value is null, returns null.
     * <p>
     * This is a shorthand method that calls {@link #getString(String, String)} with a default value of null.
     *
     * @param key the key to look up the value
     * @return the retrieved string value or null if not found
     */
    default String getString(String key) {
        return getString(key, null);
    }


    /**
     * Retrieves an integer value associated with the given key.
     * If no value is found or the value is null, returns the provided default value.
     *
     * @param key      the key to look up the value
     * @param defValue the default value to return if no value is found or if the result is null
     * @return the retrieved integer value or the default value if not found
     */
    default int getInt(String key, int defValue) {
        Integer value = getObject(key, Integer.class);
        return value != null ? value : defValue;
    }


    /**
     * Retrieves an integer value associated with the given key.
     * If no value is found or the value is null, returns 0.
     * <p>
     * This is a shorthand method that calls {@link #getInt(String, int)} with a default value of 0.
     *
     * @param key the key to look up the value
     * @return the retrieved integer value or 0 if not found
     */
    default int getInt(String key) {
        return getInt(key, 0);
    }


    /**
     * Retrieves a long value associated with the given key.
     * If no value is found or the value is null, returns the provided default value.
     *
     * @param key      the key to look up the value
     * @param defValue the default value to return if no value is found or if the result is null
     * @return the retrieved long value or the default value if not found
     */
    default long getLong(String key, long defValue) {
        Long value = getObject(key, Long.class);
        return value != null ? value : defValue;
    }


    /**
     * Retrieves a long value associated with the given key.
     * If no value is found or the value is null, returns 0.
     * <p>
     * This is a shorthand method that calls {@link #getLong(String, long)} with a default value of 0.
     *
     * @param key the key to look up the value
     * @return the retrieved long value or 0 if not found
     */
    default long getLong(String key) {
        return getLong(key, 0L);
    }


    /**
     * Retrieves a float value associated with the given key.
     * If no value is found or the value is null, returns the provided default value.
     *
     * @param key      the key to look up the value
     * @param defValue the default value to return if no value is found or if the result is null
     * @return the retrieved float value or the default value if not found
     */
    default float getFloat(String key, float defValue) {
        Float value = getObject(key, Float.class);
        return value != null ? value : defValue;
    }


    /**
     * Retrieves a float value associated with the given key.
     * If no value is found or the value is null, returns 0.
     * <p>
     * This is a shorthand method that calls {@link #getFloat(String, float)} with a default value of 0.
     *
     * @param key the key to look up the value
     * @return the retrieved float value or 0 if not found
     */
    default float getFloat(String key) {
        return getFloat(key, 0F);
    }


    /**
     * Retrieves a boolean value associated with the given key.
     * If no value is found or the value is null, returns the provided default value.
     *
     * @param key      the key to look up the value
     * @param defValue the default value to return if no value is found or if the result is null
     * @return the retrieved boolean value or the default value if not found
     */
    default boolean getBoolean(String key, boolean defValue) {
        Boolean value = getObject(key, Boolean.class);
        return value != null ? value : defValue;
    }


    /**
     * Retrieves a boolean value associated with the given key.
     * If no value is found or the value is null, returns false.
     * <p>
     * This is a shorthand method that calls {@link #getBoolean(String, boolean)} with a default value of false.
     *
     * @param key the key to look up the value
     * @return the retrieved boolean value or false if not found
     */
    default boolean getBoolean(String key) {
        return getBoolean(key, false);
    }


    /**
     * Retrieves the raw String value associated with the given key.
     * The value is returned as-is, without any defaulting behavior.
     *
     * @param key the key to look up the value
     * @return the raw String value associated with the key, or null if not found
     */
    String getRawString(String key);


    /**
     * Retrieves an object of the specified type associated with the given key.
     * The object is deserialized from the underlying data source to match the specified type.
     *
     * @param key  the key to look up the object
     * @param type the type of the object to be returned
     * @param <T>  the type of the object to be returned
     * @return the object of the specified type associated with the key, or null if not found or if the type doesn't match
     */
    <T> T getObject(String key, Type type);


    /**
     * Retrieves a list of objects of the specified type associated with the given key.
     * The list is deserialized from the underlying data source to match the specified type.
     *
     * @param key    the key to look up the list of objects
     * @param eClass the type of the objects in the list
     * @param <E>    the type of the objects in the list
     * @return the list of objects of the specified type associated with the key, or an empty list if not found
     */
    <E> List<E> getFullList(String key, Class<E> eClass);


    /**
     * Retrieves a paginated list of objects of the specified type associated with the given key.
     * The list is deserialized from the underlying data source to match the specified type, with pagination support.
     *
     * @param key    the key to look up the paginated list of objects
     * @param eClass the type of the objects in the list
     * @param page   the page number to retrieve
     * @param <E>    the type of the objects in the list
     * @return a {@link PaginatedData} object containing the paginated list of objects of the specified type,
     * or an empty paginated data if no objects are found
     */
    <E> PaginatedData<E> getPagedList(String key, Class<E> eClass, int page, boolean reverse);

    default <E> PaginatedData<E> getPagedList(String key, Class<E> eClass, int page) {
        return getPagedList(key, eClass, page, false);
    }


    /**
     * Saves a string value associated with the given key.
     * If the key already exists, the value will be updated.
     *
     * @param key   the key to associate with the string value
     * @param value the string value to save
     */
    void saveString(String key, String value);


    /**
     * Saves an integer value associated with the given key by converting the integer to a string.
     * If the key already exists, the value will be updated.
     * <p>
     * This is a shorthand method that calls {@link #saveString(String, String)} by converting the integer value to a string.
     *
     * @param key   the key to associate with the integer value
     * @param value the integer value to save
     */
    default void saveInt(String key, int value) {
        saveString(key, Integer.toString(value));
    }


    /**
     * Saves a long value associated with the given key by converting the long to a string.
     * If the key already exists, the value will be updated.
     * <p>
     * This is a shorthand method that calls {@link #saveString(String, String)} by converting the long value to a string.
     *
     * @param key   the key to associate with the long value
     * @param value the long value to save
     */
    default void saveLong(String key, long value) {
        saveString(key, Long.toString(value));
    }


    /**
     * Saves a float value associated with the given key by converting the float to a string.
     * If the key already exists, the value will be updated.
     * <p>
     * This is a shorthand method that calls {@link #saveString(String, String)} by converting the float value to a string.
     *
     * @param key   the key to associate with the float value
     * @param value the float value to save
     */
    default void saveFloat(String key, float value) {
        saveString(key, Float.toString(value));
    }


    /**
     * Saves a boolean value associated with the given key by converting the boolean to a string.
     * If the key already exists, the value will be updated.
     * <p>
     * This is a shorthand method that calls {@link #saveString(String, String)} by converting the boolean value to a string.
     *
     * @param key   the key to associate with the boolean value
     * @param value the boolean value to save
     */
    default void saveBoolean(String key, boolean value) {
        saveString(key, Boolean.toString(value));
    }


    /**
     * Saves an object associated with the given key by converting the object to a JSON string.
     * If the key already exists, the value will be updated.
     * <p>
     * This is a shorthand method that calls {@link #saveString(String, String)} by converting the object to a JSON string using {@link #toJson(Object)}.
     *
     * @param key   the key to associate with the object
     * @param value the object to save
     */
    default void saveObject(String key, Object value) {
        saveString(key, toJson(value));
    }


    /**
     * Saves a list of objects associated with the given key by converting the list to a JSON string.
     * If the key already exists, the value will be updated.
     * The size of the list is capped to the specified maximum array size.
     *
     * @param key          the key to associate with the list of objects
     * @param list         the list of objects to save
     * @param maxBatchSize the maximum number of elements in the list to be saved
     * @param <E>          the type of elements in the list
     */
    <E> void saveList(String key, List<E> list, int listSizeLimit, int maxBatchSize);


    /**
     * Saves a list of objects associated with the given key by converting the list to a JSON string.
     * If the key already exists, the list will be updated. The size of the list is capped to 25 elements.
     * <p>
     * This is a shorthand method that calls {@link #saveList(String, List, int, int)} with a default maximum array size of 25.
     *
     * @param key  the key to associate with the list of objects
     * @param list the list of objects to save
     * @param <E>  the type of elements in the list
     */
    default <E> void saveList(String key, List<E> list) {
        saveList(key, list, list.size(), 25);
    }


    /**
     * Appends an element to a list associated with the given key at the specified index.
     * If the list does not exist, a new list will be created.
     * Optionally, duplicates can be removed before appending the element.
     *
     * @param key     the key to identify the list
     * @param element the element to append to the list
     */
    <E> void appendToList(String key, E element, Class<E> eClass, int listSizeLimit, int maxBatchSize, Predicate<? super E> itemToRemove);

    default <E> void appendToList(String key, E element, Class<E> eClass) {
        appendToList(key, element, eClass, Integer.MAX_VALUE, 25, null);
    }


    /**
     * Converts a JSON string into an object of the specified type.
     * This method deserializes the given JSON string into an instance of the provided type using the Gson library (or similar).
     *
     * @param value  the JSON string to deserialize
     * @param tClass the Type of the object to convert the JSON string into
     * @param <T>    the type of the object to return
     * @return the deserialized object of the specified type
     */
    <T> T fromJson(String value, Class<T> tClass);


    /**
     * Converts a JSON input stream (Reader) into an object of the specified type.
     * This method deserializes the provided JSON from the given reader into an instance of the provided type.
     *
     * @param json    the Reader that contains the JSON data to deserialize
     * @param typeOfT the Type of the object to convert the JSON data into
     * @param <T>     the type of the object to return
     * @return the deserialized object of the specified type
     */
    <T> T fromReader(Reader json, Type typeOfT);


    /**
     * Converts an object into its JSON string representation.
     * This method serializes the given object into a JSON string using a serialization library such as Gson.
     *
     * @param object the object to serialize into JSON
     * @return the JSON string representation of the object
     */
    String toJson(Object object);


    /**
     * Creates a parameterized {@link Type} using the specified raw type and type arguments.
     * This method is useful when dealing with generic types and allows the creation of a {@link Type}
     * that can represent a generic type with its actual type parameters.
     *
     * @param rawType       the raw type of the generic type (e.g., {@link java.util.List List})
     * @param typeArguments the type arguments that the generic type should use (e.g., {@link java.lang.String String})
     * @return the parameterized type representing the raw type with the provided type arguments
     */
    Type getParameterized(Type rawType, Type... typeArguments);


    /**
     * Removes the entry associated with the given key.
     * If the key does not exist, no changes will be made.
     *
     * @param key the key identifying the entry to remove
     */
    void remove(String key);


    /**
     * Clears all entries from the data structure.
     * After calling this method, the data structure will be empty.
     */
    void clear();


    /**
     * Checks if the data structure contains an entry associated with the given key.
     *
     * @param key the key to check for existence in the data structure
     * @return {@code true} if the key exists in the data structure, {@code false} otherwise
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
         * @param json   the JSON string to be converted
         * @param tClass the type of the object to be returned
         * @param <T>    the type of the object
         * @return the Java object represented by the JSON string
         */
        <T> T fromJson(String json, Class<T> tClass);


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