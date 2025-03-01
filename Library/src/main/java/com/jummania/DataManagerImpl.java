package com.jummania;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the DataManager interface.
 * <p>
 * This class provides concrete methods for storing, retrieving, and managing data.
 * It utilizes Gson for serializing and deserializing objects to and from JSON format and
 * stores the data in a specified directory.
 * <p>
 * The class ensures thread-safety and proper directory management for storing the data.
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

    // Listener to notify data changes
    private OnDataChangeListener onDataChangeListener;


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
            if (this.filesDir.mkdirs()) {
                System.out.println("Folder created successfully: " + this.filesDir.getAbsolutePath());
            } else {
                System.err.println("Failed to create folder");
            }
        } else {
            System.out.println("Folder already exists: " + this.filesDir.getAbsolutePath());
        }
    }


    /**
     * Retrieves a `String` from stored data using the specified key.
     * This method delegates the retrieval task to `getObject`, passing `String.class` to deserialize the stored JSON data into a `String` object.
     * If an error occurs during retrieval or deserialization, it logs the error and returns the default value.
     *
     * @param key      The key used to identify the stored data.
     * @param defValue The default value to return if the data is not found or an error occurs.
     * @return The retrieved `String` value, or the default value if an error occurs.
     */
    @Override
    public String getString(String key, String defValue) {
        try {
            return getObject(key, String.class);
        } catch (Exception e) {
            // Return the default value if retrieval fails
            return defValue;
        }
    }


    /**
     * Retrieves an `int` from stored data using the specified key.
     * This method delegates the retrieval task to `getObject`, passing `Integer.class` to deserialize the stored JSON data into an `int` object.
     * If an error occurs during retrieval or deserialization, it logs the error and returns the default value.
     *
     * @param key      The key used to identify the stored data.
     * @param defValue The default value to return if the data is not found or an error occurs.
     * @return The retrieved `int` value, or the default value if an error occurs.
     */
    @Override
    public int getInt(String key, int defValue) {
        try {
            return getObject(key, Integer.class);
        } catch (Exception e) {
            // Return the default value if retrieval fails
            return defValue;
        }
    }


    /**
     * Retrieves a `long` from stored data using the specified key.
     * This method delegates the retrieval task to `getObject`, passing `Long.class` to deserialize the stored JSON data into a `long` object.
     * If an error occurs during retrieval or deserialization, it logs the error and returns the default value.
     *
     * @param key      The key used to identify the stored data.
     * @param defValue The default value to return if the data is not found or an error occurs.
     * @return The retrieved `long` value, or the default value if an error occurs.
     */
    @Override
    public long getLong(String key, long defValue) {
        try {
            return getObject(key, Long.class);
        } catch (Exception e) {
            // Return the default value if retrieval fails
            return defValue;
        }
    }


    /**
     * Retrieves a `float` from stored data using the specified key.
     * This method delegates the retrieval task to `getObject`, passing `Float.class` to deserialize the stored JSON data into a `float` object.
     * If an error occurs during retrieval or deserialization, it logs the error and returns the default value.
     *
     * @param key      The key used to identify the stored data.
     * @param defValue The default value to return if the data is not found or an error occurs.
     * @return The retrieved `float` value, or the default value if an error occurs.
     */
    @Override
    public float getFloat(String key, float defValue) {
        try {
            return getObject(key, Float.class);
        } catch (Exception e) {
            // Return the default value if retrieval fails
            return defValue;
        }
    }


    /**
     * Retrieves a `boolean` from stored data using the specified key.
     * This method delegates the retrieval task to `getObject`, passing `Boolean.class` to deserialize the stored JSON data into a `boolean` object.
     * If an error occurs during retrieval or deserialization, it logs the error and returns the default value.
     *
     * @param key      The key used to identify the stored data.
     * @param defValue The default value to return if the data is not found or an error occurs.
     * @return The retrieved `boolean` value, or the default value if an error occurs.
     */
    @Override
    public boolean getBoolean(String key, boolean defValue) {
        try {
            return getObject(key, Boolean.class);
        } catch (Exception e) {
            // Return the default value if retrieval fails
            return defValue;
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
        // Ensure DataManager is initialized
        throwExceptionIfNull();

        // Validate input parameters
        if (key == null || type == null)
            throw new IllegalArgumentException("Key or type cannot be null");

        File file = getFile(key);
        if (!file.exists()) {
            // File does not exist; return null
            return null;
        }

        try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(file.toPath()), 16 * 1024); InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            // Deserialize the file content to the given type
            return fromReader(inputStreamReader, type);
        } catch (IOException e) {
            // Log the error for debugging purposes
            System.err.println("Error reading file for key " + key + ": " + e.getMessage());
        } catch (JsonSyntaxException e) {
            // Log JSON deserialization issues
            System.err.println("Error deserializing JSON for key " + key + ": " + e.getMessage());
        }

        // Return null if an error occurs
        return null;
    }


    /**
     * Retrieves a parameterized object associated with the given key from the stored data.
     * This method allows deserialization of complex types with generic type parameters.
     * The generic type arguments are passed in as varargs, enabling support for parameterized types
     * like lists or maps.
     *
     * @param key           The key used to identify the stored parameterized object.
     * @param rawType       The raw type of the object to be retrieved (e.g., `List.class`, `Map.class`).
     * @param typeArguments The type arguments (e.g., `String.class`, `Integer.class`) to specify
     *                      the specific types for generics.
     * @param <T>           The type of the object to be returned.
     * @return The deserialized object of type `T`, or `null` if the object could not be retrieved.
     */
    @Override
    public <T> T getParameterized(String key, Type rawType, Type... typeArguments) {
        // Deserialize the object using the provided raw type and type arguments
        return getObject(key, TypeToken.getParameterized(rawType, typeArguments).getType());
    }


    /**
     * Retrieves a list of objects associated with the given key from the stored data.
     * This method supports paginated or batched data retrieval, where data is stored in multiple parts
     * (e.g., "key.0", "key.1", ...) and will continue retrieving batches until no more data is found.
     *
     * @param key  The key used to identify the list of objects in the stored data.
     * @param type The type of individual objects in the list (e.g., `String.class`, `Book.class`).
     * @param <T>  The type of objects contained in the list.
     * @return A list of deserialized objects of type `T`. If no data is found, an empty list is returned.
     */
    @Override
    public <T> List<T> getList(String key, Type type) {
        // Initialize the list that will hold the retrieved objects
        List<T> dataList = new ArrayList<>();
        int index = 0;  // To iterate over the different parts of the data (key.0, key.1, ...)
        boolean hasMoreData = true;  // Flag to control the loop for fetching multiple parts of data

        // Determine the full Type for the parameterized List
        Type listType = TypeToken.getParameterized(List.class, type).getType();

        // Try to get a batch of data (e.g., key.0, key.1, ...)

        // Loop to retrieve data in batches until no more data is found
        while (hasMoreData) {
            // Try to get a batch of data (e.g., key.0, key.1, ...)
            List<T> batchData = getObject(key + "." + index, listType);

            // If batch data exists, add it to the dataList and move to the next batch
            if (batchData != null) {
                dataList.addAll(batchData);
                index++;  // Increment to check the next batch (key.1, key.2, ...)
            } else {
                batchData = getObject(key, listType);
                if (batchData != null) dataList.addAll(batchData);
                // If no more data is found, exit the loop
                hasMoreData = false;
            }
        }

        // Return the full list of retrieved objects
        return dataList;
    }


    /**
     * Converts the provided JSON string to an object of the specified type.
     * <p>
     * This method uses Gson to deserialize the JSON string into an object of the specified type.
     * The type parameter allows the conversion to a specific object type, including generic types.
     * </p>
     *
     * @param value the JSON string to deserialize.
     * @param type  the type of the object to deserialize into.
     * @param <T>   the type of the object.
     * @return the deserialized object of type T.
     * @throws JsonSyntaxException if the JSON string is not a valid representation for the specified type.
     */
    @Override
    public <T> T fromJson(String value, Type type) {
        return converter.fromJson(value, type);
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
    public void registerOnDataChangeListener(OnDataChangeListener listener) {
        // Set the provided listener to be notified of data changes
        onDataChangeListener = listener;
    }


    /**
     * Unregisters the currently registered data change listener.
     * This method removes the previously registered listener, preventing it from being notified of any future data changes.
     */
    @Override
    public void unregisterOnDataChangeListener() {
        // Set the listener to null, effectively unregistering it
        onDataChangeListener = null;
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

        // Ensure that the DataManager is properly initialized before proceeding
        throwExceptionIfNull();

        // Get the file corresponding to the key where the value will be stored
        File file = getFile(key);

        // Write the string to the file using BufferedWriter for efficiency
        try (FileOutputStream fos = new FileOutputStream(file); BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {

            // Write the value to the file
            writer.write(value);

            // Notify the listener about data changes, if applicable
            if (onDataChangeListener != null) {
                onDataChangeListener.onDataChanged(key);
            }

        } catch (Exception e) {
            // Log the exception to standard error output
            System.err.println("Failed to save data for key: " + key + ". Error: " + e.getMessage());
        }
    }


    /**
     * Stores an int value in the storage associated with the provided key.
     * This method serializes the int value and stores it in a file corresponding to the key.
     *
     * @param key   The key used to identify the stored int value.
     * @param value The int value to be stored.
     */
    @Override
    public void saveInt(String key, int value) {
        // Delegate to saveObject to handle the actual storage of the value
        saveString(key, Integer.toString(value));
    }


    /**
     * Stores a long value in the storage associated with the provided key.
     * This method serializes the long value and stores it in a file corresponding to the key.
     *
     * @param key   The key used to identify the stored long value.
     * @param value The long value to be stored.
     */
    @Override
    public void saveLong(String key, long value) {
        // Delegate to saveObject to handle the actual storage of the value
        saveString(key, Long.toString(value));
    }


    /**
     * Stores a float value in the storage associated with the provided key.
     * This method serializes the float value and stores it in a file corresponding to the key.
     *
     * @param key   The key used to identify the stored float value.
     * @param value The float value to be stored.
     */
    @Override
    public void saveFloat(String key, float value) {
        // Delegate to saveObject to handle the actual storage of the value
        saveString(key, Float.toString(value));
    }


    /**
     * Stores a boolean value in the storage associated with the provided key.
     * This method serializes the boolean value and stores it in a file corresponding to the key.
     *
     * @param key   The key used to identify the stored boolean value.
     * @param value The boolean value to be stored.
     */
    @Override
    public void saveBoolean(String key, boolean value) {
        // Delegate to saveObject to handle the actual storage of the value
        saveString(key, Boolean.toString(value));
    }


    /**
     * Saves an object as a JSON string in the storage associated with the provided key.
     * This method serializes the given object into JSON format and stores it in a file corresponding to the key.
     * If the key already exists, the method will overwrite the existing file.
     *
     * @param key   The key used to identify the stored object.
     * @param value The object to be serialized and stored. It cannot be null.
     */
    @Override
    public void saveObject(String key, Object value) {
        // Convert the object to a JSON string using Gson
        saveString(key, toJson(value));
    }


    /**
     * Stores a list of objects in the storage associated with the provided key.
     * This method divides the list into smaller batches and stores each batch separately.
     * The default batch size is 999, but you can specify a different size with the second method.
     *
     * @param key   The key used to identify the stored list of objects.
     * @param value The list of objects to be stored.
     */
    @Override
    public <E> void saveList(String key, List<E> value) {
        // Delegate to saveList with a default max array size of 9999
        saveList(key, value, 99);
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
        if (value.isEmpty()) {
            remove(key);
        } else {
            // Ensure the batch size is at least 1
            int batchSize = Math.max(Math.min(value.size(), maxArraySize), 1);
            int pos = 0;

            // Split the list into smaller batches and store each one
            for (int i = 0; i < value.size(); i += batchSize) {
                List<E> batch = value.subList(i, Math.min(i + batchSize, value.size()));
                saveObject(key + "." + pos++, batch);  // Store each batch with a unique key
            }
        }
    }


    /**
     * Inserts an element at the specified index in a JSON-stored list.
     * If the element already exists in the list, it will be removed before insertion.
     *
     * @param key             The key associated with the list in storage.
     * @param index           The position where the new element should be inserted.
     * @param element         The element to be added to the list.
     * @param removeDuplicate If true, removes any existing occurrences of the element before adding.
     * @throws IndexOutOfBoundsException If the index is out of range for the list size.
     * @throws IllegalArgumentException  If the stored list type does not match the element's type.
     */
    @Override
    public void appendToList(String key, int index, Object element, boolean removeDuplicate) {

        if (element == null) return;

        // Retrieve the list from storage, or initialize it if null
        List<Object> list = getList(key, Object.class);

        // Ensure the retrieved list contains elements of the same type
        if (!list.isEmpty()) {
            Object obj = list.get(0);
            if (!element.getClass().isInstance(obj)) {
                throw new IllegalArgumentException("Type mismatch: Expected " + obj.getClass().getSimpleName() + ", but got " + element.getClass().getSimpleName());
            }
        }


        // Remove existing instances if needed
        if (removeDuplicate) {
            list.remove(element);
        }

        // Ensure index is within bounds
        if (index < 0 || index > list.size()) {
            index = list.size(); // Append at the end if out of bounds
        }

        // Insert at the specified index
        list.add(index, element);

        // Save the updated list
        saveList(key, list);
    }


    /**
     * Removes an element from the list stored in JSON at the specified index.
     *
     * <p>This method retrieves the list associated with the given key, removes the element
     * at the specified index, and then updates the storage.</p>
     *
     * @param key   The unique identifier for the list stored in storage. Must not be null.
     * @param index The position of the element to be removed.
     * @throws IndexOutOfBoundsException If the index is out of range for the list size.
     */
    @Override
    public void removeFromList(String key, int index) {
        // Retrieve the list from storage
        List<Object> list = getList(key, Object.class);

        // Ensure the index is within the valid range
        if (index < 0 || index >= list.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index + ". List size: " + list.size());
        }

        // Remove element at the specified index
        list.remove(index);

        // Save the updated list back to storage
        saveList(key, list);
    }


    /**
     * Removes all files associated with the given key. This method checks for multiple
     * files associated with a key by appending an index (e.g., key.0, key.1, ...) and deletes them.
     * Once no more indexed files are found, it removes the main file associated with the key.
     *
     * @param key The key whose associated files are to be deleted.
     */
    @Override
    public void remove(String key) {
        int index = 0; // Initialize the index for searching files
        boolean hasMoreFile = true; // Flag to check if there are more files to remove

        // Loop through files associated with the key by checking for indexed filenames
        while (hasMoreFile) {
            // Construct the file path using the key and index (e.g., key.0, key.1, ...)
            File file = getFile(key + "." + index);

            // Attempt to remove the file
            if (remove(file)) {
                index++; // Increment index to check the next batch file
            } else {
                // If no more indexed files are found, remove the main file
                remove(getFile(key));
                hasMoreFile = false; // Stop searching for more files
            }
        }

        // Notify the listener about data changes, if applicable
        if (onDataChangeListener != null) {
            onDataChangeListener.onDataChanged(key);
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
            if (files != null && files.length > 0) {
                // Loop through each file and remove it
                for (File file : files) {
                    remove(file); // Call the remove method for each file
                }
            } else {
                // If the directory is empty, log the message
                System.out.println("No files found in folder: " + filesDir.getAbsolutePath());
            }
        } else {
            // If the directory does not exist, log the error
            System.err.println("Folder does not exist");
        }
    }


    /**
     * Attempts to delete the specified file.
     * <p>
     * This method first checks if the provided file is not null and exists in the filesystem.
     * If the file exists, it attempts to delete the file. Depending on the outcome of the deletion attempt,
     * it will print a success or error message to the standard output.
     * </p>
     *
     * @param file The file to be deleted.
     * @return true if the deletion was attempted, false if the file is null or does not exist.
     */
    private boolean remove(File file) {
        // Check if the file is not null
        if (file != null) {
            // Try to delete the file
            if (file.delete()) {
                // If the deletion is successful, print a success message
                System.out.println("File deleted successfully: " + file.getAbsolutePath());

                // Return true indicating the file was deleted
                return true; // Return true indicating the file exists and deletion attempt was made
            } else {
                // If the deletion fails, print an error message
                System.err.println("Failed to delete file: " + file.getAbsolutePath());
            }
        }
        // If the file doesn't exist or is null, return false
        return false;
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
     * Checks if the DataManager is properly initialized.
     * This method verifies that the necessary resources, such as `filesDir` and `gson`, are properly set before performing any operations.
     * If any of the resources are null, an exception is thrown.
     *
     * @throws IllegalStateException If `filesDir` or `gson` are not properly initialized.
     */
    private void throwExceptionIfNull() {
        // Check if filesDir or gson are null and throw an exception if they are
        if (filesDir == null || converter == null) {
            throw new IllegalStateException(this + " is not properly initialized. Call DataManagerFactory.create(filesDir) first.");
        }
    }

}