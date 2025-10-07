package com.jummania;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jummania.model.MetaData;
import com.jummania.model.PaginatedData;
import com.jummania.model.Pagination;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link DataManager} interface.
 * <p>
 * This class provides concrete logic for managing key-value data, including methods for:
 * - Saving and retrieving primitive types and objects
 * - JSON serialization/deserialization
 * - Managing lists with pagination
 * - Observing data changes
 * <p>
 * Typically used for local storage solutions such as SharedPreferences, file-based storage, or custom memory caches.
 * <p>
 * Created by Jummania on 20, November, 2024.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
class DataManagerImpl implements DataManager {

    // Converter instance for serializing and deserializing data
    private final Converter converter;

    // Directory where the data will be stored
    private final File filesDir;
    private final int defaultCharBufferSize = 16 * 1024;
    // Listener to notify data changes
    private DataObserver dataObserver;


    /**
     * Constructor for initializing the DataManagerImpl.
     * Ensures that the specified directory exists or is created.
     *
     * @param filesDir  The directory where data is stored.
     * @param converter The converter for serialization/deserialization.
     * @throws IllegalArgumentException If the provided filesDir or converter is null.
     */
    DataManagerImpl(File filesDir, Converter converter) {
        // Ensure the filesDir argument is not null
        if (filesDir == null) {
            throw new IllegalArgumentException("The 'filesDir' argument cannot be null.");
        }
        if (converter == null) {
            throw new IllegalArgumentException("The 'converter' argument cannot be null.");
        }

        // Initialize the filesDir with a subdirectory called "DataManager"
        this.filesDir = new File(filesDir, "DataManager");

        // Initialize the converter for object serialization
        this.converter = converter;

        // Check if the directory exists, and create it if necessary
        if (!this.filesDir.exists()) {
            if (!this.filesDir.mkdirs()) {
                System.err.println("Failed to create folder: " + this.filesDir.getAbsolutePath());
            }
        }
    }


    /**
     * Retrieves the raw String stored in the file associated with the given key.
     *
     * @param key The key associated with the stored String.
     * @return The raw String if the file exists and can be read; otherwise, {@code null}.
     */
    @Override
    public String getRawString(String key) {

        try (BufferedReader bufferedReader = new BufferedReader(getReader(key))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            notifyError(new IOException("Error reading file for key '" + key + "': " + e.getMessage(), e));
            return null;
        }

    }


    /**
     * Retrieves an object of the specified type from the stored data using the given key.
     * This method reads the stored data as a JSON file, deserializes it into the specified type using Gson,
     * and returns the object. If the key or type is invalid, or if an error occurs during deserialization, it returns null.
     *
     * @param key  The key used to identify the stored data.
     * @param type The Type object representing the desired type of the object to retrieve.
     * @param <T>  The type of the object to return.
     * @return The object of type T, or null if the data does not exist, deserialization fails, or an error occurs.
     * @throws IllegalArgumentException If the provided key or type is null.
     */
    @Override
    public <T> T getObject(String key, Type type) {

        // Validate input parameters
        if (type == null) throw new IllegalArgumentException("type cannot be null");

        try (Reader reader = getReader(key)) {
            if (reader != null) {
                return fromReader(reader, type);
            }
        } catch (IOException e) {
            notifyError(new IOException("Error reading file for key '" + key + "': " + e.getMessage(), e));
        } catch (JsonSyntaxException e) {
            notifyError(new JsonSyntaxException("Error deserializing JSON for key '" + key + "': " + e.getMessage(), e));
        }

        // Return null if an error occurs
        return null;
    }


    /**
     * Retrieves a list of objects associated with the given key from the stored data.
     * This method supports paginated or batched data retrieval, where data is stored in multiple parts
     * (e.g., "key.0", "key.1", ...) and will continue retrieving batches until no more data is found.
     *
     * @param key    The key used to identify the list of objects in the stored data.
     * @param tClass The type of individual objects in the list (e.g., `String.class`, `Book.class`).
     * @param <T>    The type of objects contained in the list.
     * @return A list of deserialized objects of type `T`. If no data is found, an empty list is returned.
     */
    @Override
    public <T> List<T> getFullList(String key, Class<T> tClass) {
        // Initialize the list that will hold the retrieved objects
        List<T> dataList = new ArrayList<>();

        // Determine the full Type for the parameterized List
        Type listType = getParameterized(List.class, tClass);

        int position = 0;

        while (true) {
            List<T> batchData = getObject(key + "." + ++position, listType);
            if (batchData == null) return dataList;
            dataList.addAll(batchData);
        }
    }


    /**
     * Retrieves a paginated list of data associated with the specified key.
     * This method fetches a subset of data corresponding to the specified page and returns it along with pagination details.
     * <p>
     * The pagination details include the current page, previous page, next page, and total number of pages.
     *
     * @param <T>    the type of the data in the paginated list
     * @param key    the key used to fetch the data from storage
     * @param tClass the type of the items in the list
     * @param page   the page number to retrieve
     * @return a {@link PaginatedData} object containing the list of data for the specified page and pagination information
     */
    @Override
    public <T> PaginatedData<T> getPagedList(String key, Class<T> tClass, int page) {
        List<T> pageData = getPageItems(key, tClass, page);
        if (pageData == null) pageData = new ArrayList<>();

        MetaData metaData = getMetaData(key);
        int totalPages = metaData.getTotalPages();

        // Pagination logic
        Integer previousPage = (page > 1 && page <= totalPages) ? page - 1 : null;
        Integer nextPage = (page < totalPages) ? page + 1 : null;

        Pagination pagination = new Pagination(previousPage, page, nextPage, totalPages);
        return new PaginatedData<>(pageData, pagination);
    }


    /**
     * Stores a string value in persistent storage associated with the given key.
     * If the provided value is `null`, the corresponding entry will be removed.
     *
     * <p>This method serializes the string and writes it to a file corresponding to the key.
     * If an error occurs during the writing process, an error message is logged.</p>
     *
     * @param key   The unique identifier for the stored string value. Must not be null.
     * @param value The string value to be stored. If null, the corresponding key will be removed.
     * @throws IllegalArgumentException If the key is null.
     */
    @Override
    public void saveString(String key, String value) {

        // Validate inputs: Ensure the key is not null
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        // If the value is null, remove the corresponding entry and return
        if (value == null) {
            remove(key);
            return;
        }

        // Write the string to the file using BufferedWriter for efficiency
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getFile(key)), StandardCharsets.UTF_8), defaultCharBufferSize)) {
            // Write the value to the file
            writer.write(value);

            // Notify the listener about data changes, if applicable
            if (dataObserver != null) {
                dataObserver.onDataChange(key);
            }

        } catch (IOException e) {
            notifyError(new IOException("Error saving data for key: '" + key + "'. Error: " + e.getMessage(), e));
        }
    }


    /**
     * Stores a list of objects in the storage associated with the provided key.
     * This method divides the list into smaller batches (based on maxArraySize) and stores each batch separately.
     *
     * @param key          The key used to identify the stored list of objects.
     * @param value        The list of objects to be stored.
     * @param maxArraySize The maximum size of each batch. If the list is larger than this, it will be split into multiple batches.
     */
    @Override
    public <E> void saveList(String key, List<E> value, int maxArraySize) {
        // If the list becomes empty after removal, delete the key from storage
        if (value == null) {
            remove(key);
        } else {
            int valueSize = value.size();
            if (valueSize == 0) {
                remove(key);
                return;
            }

            // Ensure the batch size is at least 1
            int batchSize = Math.max(Math.min(valueSize, maxArraySize), 1);
            int pos = 0;

            // Split the list into smaller batches and store each one
            for (int i = 0; i < valueSize; i += batchSize) {
                List<E> batch = value.subList(i, Math.min(i + batchSize, valueSize));
                saveObject(key + "." + ++pos, batch);  // Store each batch with a unique key
            }

            saveString(key + ".meta", MetaData.toMeta(pos, valueSize, maxArraySize));
        }
    }


    /**
     * Appends an element to the end of a paginated list stored under the given key.
     * <p>
     * The list is divided into pages of a fixed maximum size. This method retrieves
     * the last page, adds the element to it, and creates a new page if the last page
     * is already full. Metadata is updated to reflect the new total number of items
     * and pages.
     *
     * @param key     the base key of the list to which the element will be appended
     * @param element the element to append; if {@code null}, no action is taken
     * @param tClass  the class type of the list items
     * @param <T>     the type of the list items
     */
    public <T> void appendToList(String key, T element, Class<T> tClass) {
        if (element == null) return;

        MetaData metaData = getMetaData(key);
        if (metaData == null) return;

        int totalPage = metaData.getTotalPages(), maxArraySize = metaData.getMaxArraySize();
        List<T> lastPage = getPageItems(key, tClass, totalPage);
        if (lastPage == null) lastPage = new ArrayList<>();

        if (lastPage.size() >= maxArraySize) {
            ++totalPage;
            lastPage = new ArrayList<>();
        }

        lastPage.add(element);
        saveObject(key + "." + totalPage, lastPage);
        saveString(key + ".meta", MetaData.toMeta(totalPage, metaData.getItemCount() + 1, maxArraySize));
    }


    /**
     * Converts the provided JSON string to an object of the specified type.
     * <p>
     * This method uses Gson to deserialize the JSON string into an object of the specified type.
     * The type parameter allows the conversion to a specific object type, including generic types.
     * </p>
     *
     * @param value  the JSON string to deserialize.
     * @param tClass the type of the object to deserialize into.
     * @param <T>    the type of the object.
     * @return the deserialized object of type T.
     * @throws JsonSyntaxException if the JSON string is not a valid representation for the specified type.
     */
    @Override
    public <T> T fromJson(String value, Class<T> tClass) {
        return converter.fromJson(value, tClass);
    }


    /**
     * Converts a JSON stream from a Reader into a Java object of the specified type.
     *
     * @param json    the Reader containing the JSON data to be converted
     * @param typeOfT the type of the object to be returned
     * @param <T>     the type of the object
     * @return the Java object represented by the JSON data from the Reader
     * @throws IllegalArgumentException if the JSON data cannot be parsed into the specified type
     */
    @Override
    public <T> T fromReader(Reader json, Type typeOfT) {
        return converter.fromReader(json, typeOfT);
    }


    /**
     * Converts the given object to a JSON string.
     * <p>
     * This method uses Gson to serialize an object into its JSON representation.
     * It can handle any object type and converts it into a JSON string.
     * </p>
     *
     * @param object the object to serialize into JSON.
     * @return the JSON string representation of the object.
     * @throws JsonSyntaxException if the object cannot be serialized into JSON.
     */
    @Override
    public String toJson(Object object) {
        return converter.toJson(object);
    }


    /**
     * Generates a parameterized {@link Type} with the specified raw type and type arguments.
     * <p>
     * This method is useful for dynamically constructing generic type representations
     * at runtime, particularly when working with serialization libraries like Gson.
     * </p>
     *
     * @param rawType       The base class type that the generic type is based on.
     *                      For example, {@code List.class} for a List type.
     * @param typeArguments The type parameters for the generic type.
     *                      For example, {@code String.class} for {@code List<String>}.
     * @return A {@link Type} representing the parameterized type with the given type arguments.
     * @throws IllegalArgumentException If the number of type arguments does not match
     *                                  the generic type parameters of the raw type.
     * @see TypeToken#getParameterized(Type, Type...)
     * @see java.lang.reflect.ParameterizedType
     */
    @Override
    public Type getParameterized(Type rawType, Type... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }


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
    public void remove(String key) {
        int position = 0;

        while (true) {
            if (!remove(getFile(key + "." + ++position))) break;
        }

        remove(getFile(key + ".meta"));

        // Remove the base file
        remove(getFile(key));

        // Notify the listener about data changes, if applicable
        if (dataObserver != null) {
            dataObserver.onDataChange(key);
        }
    }


    /**
     * Clears all files in the directory (filesDir). This method iterates over all files in the
     * directory and removes each one. If the directory is empty or does not exist, appropriate
     * messages are logged.
     */
    @Override
    public void clear() {
        // Ensure the directory exists before attempting to list files
        if (filesDir != null && filesDir.exists()) {
            File[] files = filesDir.listFiles(); // List all files in the directory

            // Check if there are any files to delete
            if (files != null) {
                // Loop through each file and remove it
                for (File file : files) {
                    remove(file); // Call the remove method for each file
                }
            }
        } else {
            // If the directory does not exist, log the error
            notifyError(new IOException("Folder does not exist"));
        }
    }


    /**
     * Checks if a file associated with the given key exists in the stored data.
     * This method determines whether a file corresponding to the provided key is present in the data storage directory.
     *
     * @param key The key used to identify the file in the stored data.
     * @return `true` if the file exists, `false` otherwise.
     */
    @Override
    public boolean contains(String key) {
        // Check if the file corresponding to the provided key exists in the data directory
        return getFile(key).exists();
    }


    /**
     * Registers a listener to be notified when data changes.
     * This method allows you to register a listener that will be notified whenever the stored data is modified.
     *
     * @param listener The listener that will be notified of data changes.
     */
    @Override
    public void addDataObserver(DataObserver listener) {
        // Set the provided listener to be notified of data changes
        dataObserver = listener;
    }


    /**
     * Unregisters the currently registered data change listener.
     * This method removes the previously registered listener, preventing it from being notified of any future data changes.
     */
    @Override
    public void removeDataObserver() {
        // Set the listener to null, effectively unregistering it
        dataObserver = null;
    }


    /**
     * Retrieves a {@link Reader} for the file associated with the given key.
     *
     * @param key The unique identifier for the file.
     * @return A {@link Reader} to read the file's content, or {@code null} if the file does not exist or an error occurs.
     * @throws IllegalArgumentException if the key is {@code null}.
     */
    private Reader getReader(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(getFile(key)), defaultCharBufferSize);
            return new InputStreamReader(inputStream);
        } catch (Exception e) {
            notifyError(new IOException("Failed to open file for key '" + key + "': " + e.getMessage(), e));
        }

        return null;
    }


    /**
     * Deletes the specified file.
     * This method attempts to delete the file and returns a boolean indicating whether the deletion was successful.
     *
     * @param file the file to delete
     * @return {@code true} if the file was successfully deleted, {@code false} otherwise
     */
    private boolean remove(File file) {
        return file != null && file.delete();
    }


    /**
     * Retrieves the file associated with a specified key.
     * This method constructs a `File` object using the provided key and the directory managed by the DataManager.
     *
     * @param key The key used to identify the data file.
     * @return A `File` object representing the file located in the directory for the given key.
     */
    private File getFile(String key) {
        // Return the File object located in the directory with the provided key
        return new File(filesDir, key);
    }


    /**
     * Retrieves the metadata associated with a specific list key.
     * <p>
     * The metadata contains information about the list such as:
     * <ul>
     *     <li>Total number of pages</li>
     *     <li>Total number of items</li>
     *     <li>Maximum array size per page</li>
     * </ul>
     * This metadata is stored under the key suffix ".meta".
     *
     * @param key the base key of the list for which metadata is retrieved
     * @return the {@link MetaData} object containing information about the list,
     * or {@code null} if no metadata exists for the given key
     */
    private MetaData getMetaData(String key) {
        return getObject(key + ".meta", MetaData.class);
    }


    /**
     * Notifies the registered DataObserver of an error.
     *
     * @param error The exception or error encountered.
     */
    private void notifyError(Throwable error) {
        if (dataObserver != null) {
            dataObserver.onError(error);
        }
    }

    /**
     * Retrieves a specific page (batch) of items from a stored list.
     * <p>
     * The list is split into pages with a fixed maximum size, and each page is
     * stored under a key in the format {@code key.page}. This method fetches
     * the data for the requested page.
     *
     * @param key    the base key of the list
     * @param tClass the class type of the list items
     * @param page   the page number to retrieve (1-based index)
     * @param <T>    the type of items in the list
     * @return a {@link List} of items for the specified page, or {@code null} if the page does not exist
     */
    private <T> List<T> getPageItems(String key, Class<T> tClass, int page) {
        Type listType = getParameterized(List.class, tClass);
        return getObject(key + "." + page, listType);
    }

}
