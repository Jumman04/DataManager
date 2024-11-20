package com.jummania;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

    // Gson instance for serializing and deserializing data
    private final Gson gson;

    // Directory where the data will be stored
    private final File filesDir;

    // Listener to notify data changes
    private OnDataChangeListener onDataChangeListener;

    /**
     * Constructor for initializing the DataManagerImpl.
     * Ensures that the specified directory exists or is created.
     *
     * @param filesDir The directory where data is stored.
     * @throws IllegalArgumentException If the provided filesDir is null.
     */
    DataManagerImpl(File filesDir) {
        // Ensure the filesDir argument is not null
        if (filesDir == null)
            throw new IllegalArgumentException("The 'filesDir' argument cannot be null.");

        // Initialize the filesDir with a subdirectory called "DataManagerFactory"
        this.filesDir = new File(filesDir, "DataManager");

        // Initialize Gson for object serialization
        gson = new Gson();

        // Check if the directory exists, and create it if necessary
        if (!this.filesDir.exists()) {
            if (this.filesDir.mkdirs())
                System.out.println("Folder created successfully: " + this.filesDir.getAbsolutePath());
            else System.err.println("Failed to create folder");
        } else {
            System.out.println("Folder already exists: " + this.filesDir.getAbsolutePath());
        }
    }


    /**
     * Retrieves an InputStreamReader for a specified key.
     * This method checks if the directory is readable and if the file associated with the given key exists.
     * If the file exists, it opens the file for reading and returns an InputStreamReader to read its contents.
     *
     * @param key The key used to identify the data file.
     * @return An InputStreamReader for the file associated with the key, or null if an error occurs or the file doesn't exist.
     */
    private InputStreamReader getInputStreamReader(String key) {
        // Ensure that the necessary data and files are initialized before proceeding
        throwExceptionIfNull();

        try {
            // Check if the directory is readable
            if (filesDir.canRead()) {
                // Retrieve the file associated with the key
                File file = getFile(key);

                // If the file exists, create an InputStreamReader to read it
                if (file.exists()) {
                    // Use a BufferedInputStream with a 16KB buffer for efficient reading
                    BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(file.toPath()), 16 * 1024);

                    // Return an InputStreamReader to read the file content
                    return new InputStreamReader(inputStream);
                }
            }
        } catch (Exception e) {
            // Print the exception message if something goes wrong
            System.err.println(e.getMessage());
        }

        // Return null if the file doesn't exist or an error occurs
        return null;
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
        if (filesDir == null || gson == null) {
            throw new IllegalStateException(this + " is not properly initialized. Call initialize(filesDir) first.");
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
            // Log the error message for debugging purposes
            System.err.println(e.getMessage());
        }
        // Return the default value if retrieval fails
        return defValue;
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
            // Log the error message for debugging purposes
            System.err.println(e.getMessage());
        }
        // Return the default value if retrieval fails
        return defValue;
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
            // Log the error message for debugging purposes
            System.err.println(e.getMessage());
        }
        // Return the default value if retrieval fails
        return defValue;
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
            // Log the error message for debugging purposes
            System.err.println(e.getMessage());
        }
        // Return the default value if retrieval fails
        return defValue;
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
            // Log the error message for debugging purposes
            System.err.println(e.getMessage());
        }
        // Return the default value if retrieval fails
        return defValue;
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
        // Ensure that the DataManager has been properly initialized before performing any operation
        throwExceptionIfNull();

        // Check if key or type are null and throw an IllegalArgumentException if they are
        if (key == null || type == null) {
            throw new IllegalArgumentException("Key or value cannot be null");
        }

        try (InputStreamReader inputStreamReader = getInputStreamReader(key)) {
            // If the InputStreamReader is successfully retrieved, deserialize the content to the specified type
            if (inputStreamReader != null) {
                return gson.fromJson(inputStreamReader, type);
            }
        } catch (Exception e) {
            // Print any exception that occurs during deserialization or file reading
            System.err.println(e.getMessage());
        }

        // Return null if the data could not be read or deserialized
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

        // Loop to retrieve data in batches until no more data is found
        while (hasMoreData) {
            // Try to get a batch of data (e.g., key.0, key.1, ...)
            List<T> batchData = getObject(key + "." + index, listType);

            // If batch data exists, add it to the dataList and move to the next batch
            if (batchData != null) {
                dataList.addAll(batchData);
                index++;  // Increment to check the next batch (key.1, key.2, ...)
            } else {
                // If no more data is found, exit the loop
                hasMoreData = false;
            }
        }

        // Return the full list of retrieved objects
        return dataList;
    }


    /**
     * Returns the Gson instance used for JSON serialization and deserialization.
     * <p>
     * This method ensures that the DataManager is properly initialized before returning the Gson instance.
     * If the DataManager has not been initialized (i.e., if the filesDir or gson is null), an exception is thrown.
     * </p>
     *
     * @return the Gson instance used for JSON operations.
     * @throws IllegalStateException if the DataManager has not been properly initialized.
     */
    @Override
    public Gson getGson() {
        // Check if the DataManager is properly initialized before accessing the Gson instance
        throwExceptionIfNull();
        // Return the initialized Gson instance
        return gson;
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
        return gson.fromJson(value, type);
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
        return gson.toJson(object);
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
     * Stores a String value in the storage associated with the provided key.
     * This method serializes the String and stores it in a file corresponding to the key.
     *
     * @param key   The key used to identify the stored String value.
     * @param value The String value to be stored. It cannot be null.
     */
    @Override
    public void putString(String key, String value) {
        // Delegate to putObject to handle the actual storage of the value
        putObject(key, value);
    }


    /**
     * Stores an int value in the storage associated with the provided key.
     * This method serializes the int value and stores it in a file corresponding to the key.
     *
     * @param key   The key used to identify the stored int value.
     * @param value The int value to be stored.
     */
    @Override
    public void putInt(String key, int value) {
        // Delegate to putObject to handle the actual storage of the value
        putObject(key, value);
    }


    /**
     * Stores a long value in the storage associated with the provided key.
     * This method serializes the long value and stores it in a file corresponding to the key.
     *
     * @param key   The key used to identify the stored long value.
     * @param value The long value to be stored.
     */
    @Override
    public void putLong(String key, long value) {
        // Delegate to putObject to handle the actual storage of the value
        putObject(key, value);
    }


    /**
     * Stores a float value in the storage associated with the provided key.
     * This method serializes the float value and stores it in a file corresponding to the key.
     *
     * @param key   The key used to identify the stored float value.
     * @param value The float value to be stored.
     */
    @Override
    public void putFloat(String key, float value) {
        // Delegate to putObject to handle the actual storage of the value
        putObject(key, value);
    }


    /**
     * Stores a boolean value in the storage associated with the provided key.
     * This method serializes the boolean value and stores it in a file corresponding to the key.
     *
     * @param key   The key used to identify the stored boolean value.
     * @param value The boolean value to be stored.
     */
    @Override
    public void putBoolean(String key, boolean value) {
        // Delegate to putObject to handle the actual storage of the value
        putObject(key, value);
    }


    /**
     * Saves an object as a JSON string in the storage associated with the provided key.
     * This method serializes the given object into JSON format and stores it in a file corresponding to the key.
     * If the key already exists, the method will overwrite the existing file.
     *
     * @param key   The key used to identify the stored object.
     * @param value The object to be serialized and stored. It cannot be null.
     * @throws IllegalArgumentException if either the key or the value is null.
     */
    @Override
    public void putObject(String key, Object value) {
        // Validate inputs: neither key nor value can be null
        if (key == null || value == null)
            throw new IllegalArgumentException("Key or value cannot be null");

        // Ensure that the DataManager is properly initialized before proceeding
        throwExceptionIfNull();

        // Convert the object to a JSON string using Gson
        String json = gson.toJson(value);

        // Get the file corresponding to the key where the object will be stored
        File file = getFile(key);

        // Write the JSON string to the file
        try (FileOutputStream fos = new FileOutputStream(file); BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {

            // Write the JSON string to the file
            writer.write(json);

            // Close the writer and output stream (handled by try-with-resources)
            if (onDataChangeListener != null) {
                // Notify the listener that the data has changed
                onDataChangeListener.onDataChanged(key);
            }
        } catch (Exception e) {
            // Print the exception message to standard error if an error occurs
            System.err.println(e.getMessage());
        }
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
    public <T> void putList(String key, List<T> value) {
        // Delegate to putList with a default max array size of 9999
        putList(key, value, 999);
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
    public <T> void putList(String key, List<T> value, int maxArraySize) {
        // Ensure the batch size is at least 1
        int batchSize = Math.max(Math.min(value.size(), maxArraySize), 1);
        int pos = 0;

        // Split the list into smaller batches and store each one
        for (int i = 0; i < value.size(); i += batchSize) {
            List<T> batch = value.subList(i, Math.min(i + batchSize, value.size()));
            putObject(key + "." + pos++, batch);  // Store each batch with a unique key
        }
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
        // Check if the file is not null and exists
        if (file != null && file.exists()) {
            // Try to delete the file
            if (file.delete()) {
                // If the deletion is successful, print a success message
                System.out.println("File deleted successfully: " + file.getAbsolutePath());
            } else {
                // If the deletion fails, print an error message
                System.err.println("Failed to delete file: " + file.getAbsolutePath());
            }
            return true; // Return true indicating the file exists and deletion attempt was made
        }
        // If the file doesn't exist or is null, return false
        return false;
    }


}
