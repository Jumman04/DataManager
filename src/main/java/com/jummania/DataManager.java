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
 * Created by Jummania on 20, November 2024.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */


public interface DataManager {


    /**
     * Saves an object to the storage system associated with the given key, using the specified type.
     * <p>
     * This is useful for objects with generic types or when the runtime class
     * does not fully capture the type information. Implementations should
     * serialize the object using {@code typeOfSrc}.
     * </p>
     *
     * @param key       the unique key identifying the storage location; must not be {@code null}
     * @param value     the object to save; may be {@code null} to remove the key
     * @param typeOfSrc the {@link Type} representing the object's type for serialization
     * @see #saveObject(String, Object)
     */
    void saveObject(String key, Object value, Type typeOfSrc);


    /**
     * Saves an object to the storage system associated with the given key.
     * <p>
     * This is a convenience method equivalent to calling
     * {@link #saveObject(String, Object, Type)} using the object's runtime class.
     * </p>
     *
     * @param key   the unique key identifying the storage location; must not be {@code null}
     * @param value the object to save; may be {@code null} to remove the key
     * @see #saveObject(String, Object, Type)
     */
    default void saveObject(String key, Object value) {
        saveObject(key, value, value.getClass());
    }


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
     * Retrieves the raw String value associated with the given key.
     * The value is returned as-is, without any defaulting behavior.
     *
     * @param key the key to look up the value
     * @return the raw String value associated with the key, or null if not found
     */
    String getRawString(String key);

    /**
     * Saves an integer value associated with the given key by converting the integer to a string.
     * If the key already exists, the value will be updated.
     * <p>
     * This is a shorthand method that calls {@link #saveObject(String, Object)} by converting the integer value to a string.
     *
     * @param key   the key to associate with the integer value
     * @param value the integer value to save
     */
    default void saveInt(String key, int value) {
        saveObject(key, Integer.toString(value));
    }


    /**
     * Saves a long value associated with the given key by converting the long to a string.
     * If the key already exists, the value will be updated.
     * <p>
     * This is a shorthand method that calls {@link #saveObject(String, Object)} by converting the long value to a string.
     *
     * @param key   the key to associate with the long value
     * @param value the long value to save
     */
    default void saveLong(String key, long value) {
        saveObject(key, Long.toString(value));
    }


    /**
     * Saves a float value associated with the given key by converting the float to a string.
     * If the key already exists, the value will be updated.
     * <p>
     * This is a shorthand method that calls {@link #saveObject(String, Object)} by converting the float value to a string.
     *
     * @param key   the key to associate with the float value
     * @param value the float value to save
     */
    default void saveFloat(String key, float value) {
        saveObject(key, Float.toString(value));
    }


    /**
     * Saves a boolean value associated with the given key by converting the boolean to a string.
     * If the key already exists, the value will be updated.
     * <p>
     * This is a shorthand method that calls {@link #saveObject(String, Object)} by converting the boolean value to a string.
     *
     * @param key   the key to associate with the boolean value
     * @param value the boolean value to save
     */
    default void saveBoolean(String key, boolean value) {
        saveObject(key, Boolean.toString(value));
    }


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
     * Retrieves the complete list of elements stored under the specified key by
     * combining all paginated data segments into a single {@link List}.
     * <p>
     * The list data is divided into pages (e.g., {@code key.1}, {@code key.2}, etc.),
     * and this method reconstructs the full list by reading and concatenating
     * all pages. If {@code reverse} is {@code true}, pages are read in reverse
     * order (from last to first).
     * </p>
     *
     * <p>
     * If the key has no metadata or no valid pages, an empty list is returned.
     * </p>
     *
     * @param <E>     the type of elements in the list
     * @param key     the base key used to identify the stored paginated list
     * @param eClass  the class type of the list elements (used for deserialization)
     * @param reverse {@code true} to load pages in reverse order (from last to first);
     *                {@code false} to load them in natural order
     * @return a {@link List} containing all elements across all stored pages,
     * or an empty list if no data is found
     */
    <E> List<E> getFullList(String key, Class<E> eClass, boolean reverse);


    /**
     * Retrieves the complete list of elements stored under the specified key
     * in natural (forward) order.
     * <p>
     * This is a convenience overload of {@link #getFullList(String, Class, boolean)}
     * with {@code reverse} set to {@code false}.
     * </p>
     *
     * @param <E>    the type of elements in the list
     * @param key    the base key used to identify the stored paginated list
     * @param eClass the class type of the list elements (used for deserialization)
     * @return a {@link List} containing all elements across all stored pages,
     * or an empty list if no data is found
     * @see #getFullList(String, Class, boolean)
     */
    default <E> List<E> getFullList(String key, Class<E> eClass) {
        return getFullList(key, eClass, false);
    }


    /**
     * Retrieves a paginated list of elements associated with the specified key.
     * <p>
     * This method allows fetching a specific page of data from storage.
     * Pages are 1-based. If {@code reverse} is {@code true}, pages are
     * counted from the last page backward (e.g., 1 → last page, 2 → second-to-last, etc.).
     * </p>
     *
     * @param <E>     the type of elements in the list
     * @param key     the unique key identifying the dataset
     * @param eClass  the class type of the list elements (for deserialization)
     * @param page    the page number to retrieve (1-based)
     * @param reverse if {@code true}, pages are fetched in reverse order
     * @return a {@link PaginatedData} object containing the elements for the requested page
     * @see PaginatedData
     * @see com.jummania.model.Pagination
     */
    <E> PaginatedData<E> getPagedList(String key, Class<E> eClass, int page, boolean reverse);


    /**
     * Retrieves a paginated list of elements associated with the specified key.
     * <p>
     * This is a convenience method equivalent to calling
     * {@link #getPagedList(String, Class, int, boolean)} with {@code reverse = false}.
     * </p>
     *
     * @param <E>    the type of elements in the list
     * @param key    the unique key identifying the dataset
     * @param eClass the class type of the list elements (for deserialization)
     * @param page   the page number to retrieve (1-based)
     * @return a {@link PaginatedData} object containing the elements for the requested page
     * @see #getPagedList(String, Class, int, boolean)
     */
    default <E> PaginatedData<E> getPagedList(String key, Class<E> eClass, int page) {
        return getPagedList(key, eClass, page, false);
    }


    /**
     * Saves a list of elements to storage, splitting it into batches if necessary.
     * <p>
     * The list is divided into smaller sublists (batches) according to {@code maxBatchSize},
     * and each batch is stored separately. Metadata about the total items, total pages,
     * and batch size is stored under a special {@code key.meta} entry.
     * </p>
     * <p>
     * If the list is {@code null} or becomes empty after applying {@code listSizeLimit},
     * the key is removed from storage.
     * </p>
     *
     * @param <E>           the type of elements in the list
     * @param key           the unique key identifying the dataset
     * @param list          the list of elements to save; may be {@code null}
     * @param listSizeLimit the maximum total number of elements to store
     * @param maxBatchSize  the maximum number of elements per batch (must be ≥ 1)
     * @see #saveObject(String, Object, Type)
     * @see #remove(String)
     * @see com.jummania.model.MetaData#toMeta(int, int, int)
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
     * Appends a single element to a paginated list stored under the given key.
     * <p>
     * If the list does not exist, a new one is created. The list is stored in
     * batches according to {@code maxBatchSize}, and the total number of elements
     * is limited by {@code listSizeLimit}. Optionally, an existing element can
     * be removed before adding the new element using the {@code itemToRemove} predicate.
     * </p>
     *
     * @param <E>                the type of elements in the list
     * @param key                the unique key identifying the stored paginated list
     * @param element            the element to append; ignored if {@code null}
     * @param eClass             the class type of the list elements (for deserialization)
     * @param listSizeLimit      the maximum total number of elements allowed in storage
     * @param maxBatchSize       the maximum number of elements per batch
     * @param addFirst           if {@code true}, the new element is added to the beginning of the list
     * @param preventDuplication a predicate to identify and remove an existing element; may be {@code null}
     * @see #saveList(String, List, int, int)
     * @see #saveObject(String, Object, Type)
     */
    <E> void appendToList(String key, E element, Class<E> eClass, int listSizeLimit, int maxBatchSize, boolean addFirst, Predicate<? super E> preventDuplication);


    /**
     * Appends a single element to a paginated list with default limits.
     * <p>
     * This convenience method uses a default {@code listSizeLimit} of {@link Integer#MAX_VALUE},
     * a {@code maxBatchSize} of 25, and does not remove any existing element.
     * </p>
     *
     * @param <E>     the type of elements in the list
     * @param key     the unique key identifying the stored paginated list
     * @param element the element to append; ignored if {@code null}
     * @param eClass  the class type of the list elements (for deserialization)
     * @see #appendToList(String, Object, Class, int, int, boolean, Predicate)
     */
    default <E> void appendToList(String key, E element, Class<E> eClass) {
        appendToList(key, element, eClass, Integer.MAX_VALUE, 25, false, null);
    }


    /**
     * Removes the first element from a paginated list stored under the given key
     * that matches the provided predicate.
     * <p>
     * The method searches through the list pages in reverse order (from last page
     * to first page). If an element matching {@code itemToRemove} is found, it is
     * removed, and the updated page and metadata are saved.
     * </p>
     * <p>
     * If no matching element is found or the predicate is {@code null}, the method
     * returns {@code false}.
     * </p>
     *
     * @param <E>          the type of elements in the list
     * @param key          the unique key identifying the paginated list
     * @param eClass       the class type of the list elements (for deserialization)
     * @param itemToRemove the predicate used to identify the element to remove; must not be {@code null}
     * @return {@code true} if an element was found and removed; {@code false} otherwise
     *
     */

    <E> boolean removeFromList(String key, Class<E> eClass, Predicate<? super E> itemToRemove);


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
     * Serializes the given object into JSON using the specified type and writes it to the provided {@link Appendable}.
     * <p>
     * This is useful when the object type includes generics or is not fully captured by its runtime class.
     * The method allows streaming the JSON output directly to a {@link java.io.Writer},
     * {@link StringBuilder}, or any other {@link Appendable}.
     * </p>
     *
     * @param src       the source object to serialize; must not be {@code null}
     * @param typeOfSrc the specific {@link Type} of the object to serialize
     * @param writer    the destination {@link Appendable} to write the JSON output to
     */
    void toJson(Object src, Type typeOfSrc, Appendable writer);


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
     * Removes all data associated with the specified key, including all paginated files
     * and the metadata file. After removal, notifies the registered data observer (if any)
     * about the change.
     * <p>
     * The method works as follows:
     * <ol>
     *     <li>Iterates through all paginated files with keys in the format {@code key.1, key.2, ...} and deletes them.</li>
     *     <li>Deletes the metadata file stored under {@code key.meta}.</li>
     *     <li>Deletes the base file associated with {@code key}.</li>
     *     <li>If a {@link DataObserver} is registered, calls {@code onDataChange(key)} to notify about the deletion.</li>
     * </ol>
     *
     * @param key the base key of the data to be removed
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


    /**
     * Interface for converting between Java objects and JSON representations.
     * <p>
     * Implementations are responsible for serializing Java objects to JSON strings or streams,
     * and deserializing JSON strings or streams back to Java objects.
     * </p>
     */
    interface Converter {

        /**
         * Converts a Java object to its JSON string representation.
         *
         * @param data the Java object to be converted to JSON
         * @param <T>  the type of the object
         * @return a JSON string representing the object
         */
        <T> String toJson(T data);


        /**
         * Serializes the given object into JSON using the specified type and writes it to the provided {@link Appendable}.
         * <p>
         * This is useful when the object type includes generics or is not fully captured by its runtime class.
         * The method allows streaming the JSON output directly to a {@link java.io.Writer},
         * {@link StringBuilder}, or any other {@link Appendable}.
         * </p>
         *
         * @param src       the source object to serialize; must not be {@code null}
         * @param typeOfSrc the specific {@link Type} of the object to serialize
         * @param writer    the destination {@link Appendable} to write the JSON output to
         */
        void toJson(Object src, Type typeOfSrc, Appendable writer);


        /**
         * Converts a JSON string into a Java object of the specified type.
         *
         * @param json   the JSON string to be converted
         * @param tClass the class type of the object to return
         * @param <T>    the type of the object
         * @return the Java object represented by the JSON string
         */
        <T> T fromJson(String json, Class<T> tClass);

        /**
         * Converts a JSON stream from a {@link Reader} into a Java object of the specified type.
         *
         * @param json    the {@link Reader} containing the JSON data
         * @param typeOfT the type of the object to be returned
         * @param <T>     the type of the object
         * @return the Java object represented by the JSON data from the {@link Reader}
         */
        <T> T fromReader(Reader json, Type typeOfT);
    }


}