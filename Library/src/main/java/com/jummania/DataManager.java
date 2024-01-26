package com.jummania;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * The DataManager class provides functionality for managing and storing data using JSON serialization.
 */
public class DataManager {

    private final Gson gson;
    private final File filesDir;

    /**
     * Initializes a new DataManager instance with the specified files directory.
     *
     * @param filesDir The directory where data will be stored.
     */
    public DataManager(File filesDir) {
        // Initialize the Gson object for JSON serialization/deserialization
        gson = new Gson();

        // Create the DataManager folder within the specified files directory
        this.filesDir = new File(filesDir, "DataManager");

        // Check if the DataManager folder already exists
        if (!this.filesDir.exists()) {
            // If it doesn't exist, attempt to create the folder
            if (this.filesDir.mkdirs())
                // Print a success message if the folder is created successfully
                System.out.println("Folder created successfully: " + this.filesDir.getAbsolutePath());
            else
                // Print an error message if folder creation fails
                System.err.println("Failed to create folder");
        } else
            // Print a message if the folder already exists
            System.out.println("Folder already exists: " + this.filesDir.getAbsolutePath());
    }


    /**
     * Gets the Type for a generic List based on the provided data model class.
     *
     * @param dataModel The class representing the data model.
     * @param <T>       The type of the data model.
     * @return The Type for a List of the specified data model class.
     */
    private <T> Type getListType(Class<T> dataModel) {
        // This method returns a Type object that represents a parameterized List type.
        // It is used for Gson serialization/deserialization of generic List types.

        // TypeToken.getParameterized is a Gson utility method that creates a parameterized type
        // for a generic class or interface, in this case, List<T>.
        // It takes two parameters: the raw type (List.class) and the type parameters (dataModel).

        // The .getType() at the end retrieves the Type object representing the parameterized List type.

        // Example usage:
        // If dataModel is, for instance, SimpleData.class, this method returns the Type
        // for List<SimpleData>.

        return TypeToken.getParameterized(List.class, dataModel).getType();
    }


    /**
     * Gets the File object for a given file name in the DataManager directory.
     *
     * @param fileName The name of the file.
     * @return The File object representing the file in the DataManager directory.
     */
    private File getFile(String fileName) {
        // This method generates a File object within the DataManager directory
        // using the specified file name.

        // It takes the DataManager directory (filesDir) and appends the provided
        // file name to create a new File object.

        // Example usage:
        // If filesDir is "/path/to/data" and fileName is "example.txt",
        // this method returns a File object representing "/path/to/data/DataManager/example.txt".

        return new File(filesDir, fileName);
    }


    /**
     * Gets the JSON data for a given data model class.
     *
     * @param dataModel The class representing the data model.
     * @param <T>       The type of the data model.
     * @return The JSON data as a String.
     */
    public <T> String getJsonData(Class<T> dataModel) {
        // This method retrieves JSON data for a given data model class by delegating
        // the call to the getJsonData(String key) method with the key derived from the
        // simple name of the data model class.

        // It's a convenient wrapper method that allows the caller to get JSON data
        // without explicitly specifying the key.

        // Example usage:
        // If dataModel is SimpleData.class, this method is equivalent to calling
        // getJsonData("SimpleData").

        return getJsonData(dataModel.getSimpleName());
    }


    /**
     * Gets the JSON data for a given key.
     *
     * @param key The key associated with the data.
     * @return The JSON data as a String.
     */
    public String getJsonData(String key) {
        // This method retrieves JSON data from a file associated with the specified key.

        // It first checks for null values in essential components using throwExceptionIfNull().
        throwExceptionIfNull();

        // StringBuilder to hold the JSON data
        StringBuilder jsonString = new StringBuilder();

        try (InputStreamReader inputStreamReader = getInputStreamReader(key)) {
            // Attempt to create an InputStreamReader based on the provided key
            if (inputStreamReader != null) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    // Read the contents of the file into the StringBuilder
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        jsonString.append(line);
                    }
                }
            }
        } catch (IOException e) {
            // Handle IOException by printing the stack trace
            e.printStackTrace();
        }

        // Return the JSON data as a String
        return jsonString.toString();
    }


    /**
     * Gets the combined data from multiple batches for a given data model class.
     *
     * @param dataModel The class representing the data model.
     * @param <T>       The type of the data model.
     * @return The combined data as a List.
     */
    public <T> List<T> getData(Class<T> dataModel) {
        // This method retrieves a List of data of a specified data model class.
        // It does this by combining multiple batches of data until no more data is found.

        // Initialize an ArrayList to hold the combined data
        List<T> combinedData = new ArrayList<>();

        // Initialize an index for batch retrieval
        int index = 0;

        // Flag to indicate whether there is more data
        boolean hasMoreData = true;

        // Continue looping until no more data is found
        while (hasMoreData) {
            // Attempt to retrieve a batch of data based on the current index
            List<T> batchData = getDataBatch(dataModel.getSimpleName() + "." + index, dataModel);

            // Check if the batchData is not null (i.e., more data is found)
            if (batchData != null) {
                // Add the batch data to the combined result
                combinedData.addAll(batchData);
                index++; // Move to the next batch
            } else {
                // No more data found, exit the loop
                hasMoreData = false;
            }
        }

        // Return the combined data
        return combinedData;
    }


    /**
     * Gets a batch of data for a given key and data model class.
     *
     * @param key       The key associated with the data.
     * @param dataModel The class representing the data model.
     * @param <T>       The type of the data model.
     * @return The batch of data as a List.
     */
    private <T> List<T> getDataBatch(String key, Class<T> dataModel) {
        // This method retrieves a batch of data of a specified data model class from a file associated with the given key.
        // It is part of the getData() method, which retrieves data in batches until no more data is found.

        try (InputStreamReader inputStreamReader = getInputStreamReader(key)) {
            // Attempt to create an InputStreamReader based on the provided key
            if (inputStreamReader != null) {
                // If the InputStreamReader is successfully created, use Gson to deserialize the JSON data
                // into a List of the specified data model class.
                return gson.fromJson(inputStreamReader, getListType(dataModel));
            }
        } catch (IOException e) {
            // Handle IOException by printing the stack trace
            e.printStackTrace();
        }

        // If there's an error or the InputStreamReader is null, return null
        return null;
    }


    /**
     * Gets an InputStreamReader for a given key.
     *
     * @param key The key associated with the data.
     * @return An InputStreamReader for reading the data.
     */
    private InputStreamReader getInputStreamReader(String key) {
        // This method creates an InputStreamReader for reading from a file associated with the given key.
        // It is used in the getDataBatch() method to retrieve data from files.

        // Check for null values in essential components using throwExceptionIfNull().
        throwExceptionIfNull();

        try {
            // Check if the DataManager directory is readable
            if (filesDir.canRead()) {
                // Create a File object based on the provided key
                File file = getFile(key);

                // Check if the file exists
                if (file.exists()) {
                    // If the file exists, create a BufferedInputStream for efficient reading
                    // and wrap it with an InputStreamReader.
                    BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(file.toPath()), 16 * 1024);
                    return new InputStreamReader(inputStream);
                }
            }
        } catch (Exception e) {
            // Handle any exceptions by printing the stack trace
            e.printStackTrace();
        }

        // If there's an error or the file does not exist, return null
        return null;
    }


    /**
     * Saves a List of data with the specified data model class.
     *
     * @param dataList The List of data to be saved.
     * @param tClass   The class representing the data model.
     * @param <T>      The type of the data model.
     */
    public <T> void saveData(List<T> dataList, Class<T> tClass) {
        // This method is responsible for saving a list of data to a file in batches
        // with a maximum array size of 9999 elements.

        // It delegates the call to the overloaded saveData() method with the default
        // maxArraySize parameter of 9999.

        saveData(dataList, tClass, 9999);
    }


    /**
     * Saves a List of data with the specified data model class and maximum array size.
     *
     * @param dataList     The List of data to be saved.
     * @param tClass       The class representing the data model.
     * @param maxArraySize The maximum array size for each batch.
     * @param <T>          The type of the data model.
     */
    public <T> void saveData(List<T> dataList, Class<T> tClass, int maxArraySize) {
        // This method saves a list of data to multiple files in batches,
        // where each file contains a JSON representation of a subset of the data.

        // Check if the provided dataList is null, and throw an exception if so.
        if (dataList == null) {
            throw new IllegalArgumentException("dataList cannot be null");
        }

        // Determine the batch size based on the size of the data and the specified maxArraySize
        int batchSize = calculateBatchSize(dataList.size(), maxArraySize);

        // Initialize a position index for naming batches/files
        int pos = 0;

        // Iterate over the dataList in batches
        for (int i = 0; i < dataList.size(); i += batchSize) {
            // Create a subList representing the current batch
            List<T> batch = dataList.subList(i, Math.min(i + batchSize, dataList.size()));

            // Save the current batch to a file, naming it based on the data model class and position index
            saveData(tClass.getSimpleName() + "." + pos++, gson.toJson(batch, getListType(tClass)));
        }
    }


    /**
     * Calculates the batch size based on the data size and maximum array size.
     *
     * @param dataSize     The size of the data.
     * @param maxArraySize The maximum array size for each batch.
     * @return The calculated batch size.
     */
    private int calculateBatchSize(int dataSize, int maxArraySize) {
        // This method calculates the batch size based on the total size of the data and a specified maximum array size.

        // Calculate the batch size as the minimum value between the total data size and the specified maxArraySize.
        int batchSize = Math.min(dataSize, maxArraySize);

        // Ensure that the calculated batch size is greater than zero.
        // If dataSize and maxArraySize are both zero, set the batch size to 1 to avoid division by zero.
        return Math.max(batchSize, 1);
    }


    /**
     * Saves JSON data with the specified key.
     *
     * @param key  The key associated with the data.
     * @param json The JSON data to be saved.
     */
    public void saveData(String key, String json) {
        // This method saves a JSON string to a file in the app's internal storage using the specified key.

        // Check if either the JSON string or the key is null, and throw an exception if so.
        if (json == null || key == null)
            throw new IllegalArgumentException("Key or json cannot be null");

        // Check for null values in essential components using throwExceptionIfNull().
        throwExceptionIfNull();

        try {
            // Construct the full path to the file in the app's internal storage based on the provided key
            File file = getFile(key);

            // Open a private file in the app's internal storage for writing
            FileOutputStream fos = new FileOutputStream(file);

            // Wrap the FileOutputStream with a BufferedWriter for efficient writing
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

            // Write the JSON string to the file
            writer.write(json);

            // Close the BufferedWriter and FileOutputStream to release resources
            writer.close();
            fos.close();
        } catch (IOException e) {
            // Handle IOException by printing the stack trace
            e.printStackTrace();
        }
    }


    /**
     * Deletes data associated with the specified data model class.
     *
     * @param tClass The class representing the data model.
     * @param <T>    The type of the data model.
     */
    public <T> void deleteData(Class<T> tClass) {
        // This method deletes data files associated with a specified data model class.

        // It delegates the call to the overloaded deleteData() method with the key
        // generated from the simple name of the provided data model class.

        deleteData(tClass.getSimpleName());
    }


    /**
     * Deletes data associated with the specified key.
     *
     * @param name The key associated with the data.
     */
    public void deleteData(String name) {
        // This method deletes multiple data files associated with a specified key.

        // It iteratively attempts to delete files with names generated by appending
        // an index to the provided name until no more files are found.

        int index = 0;
        boolean hasMoreFile = true;

        while (hasMoreFile) {
            // Generate the full path to the file based on the name and index
            File file = getFile(name + "." + index);

            // Check if the file exists
            if (file.exists()) {
                // If the file exists, attempt to delete it
                if (file.delete())
                    System.out.println("File deleted successfully: " + file.getAbsolutePath());
                else
                    System.err.println("Failed to delete file: " + file.getAbsolutePath());
                index++;
            } else {
                // If the file does not exist, exit the loop
                hasMoreFile = false;
            }
        }
    }


    /**
     * Clears all data stored in the DataManager directory.
     */
    public void clearAll() {
        // This method deletes all files within the DataManager's designated folder.

        // Check if the DataManager's folder (filesDir) is not null
        if (filesDir != null) {
            // List all files within the DataManager's folder
            File[] files = filesDir.listFiles();

            // Check if there are files in the folder
            if (files != null) {
                // Iterate over each file in the folder
                for (File file : files) {
                    // Check if the file is not null and exists
                    if (file != null && file.exists()) {
                        // Attempt to delete the file
                        if (file.delete()) {
                            System.out.println("File deleted successfully: " + file.getAbsolutePath());
                        } else {
                            System.err.println("Failed to delete file: " + file.getAbsolutePath());
                        }
                    }
                }
            } else {
                // Print a message if no files are found in the folder
                System.out.println("No files found in folder: " + filesDir.getAbsolutePath());
            }
        } else {
            // Print an error message if the DataManager's folder is null
            System.err.println("Folder does not exist");
        }
    }


    /**
     * Throws an exception if the DataManager is not properly initialized.
     */
    private void throwExceptionIfNull() {
        // This method checks if essential components (filesDir and gson) are null,
        // and throws an IllegalStateException if they are, indicating that the DataManager
        // is not properly initialized and that the user should call initialize(Context) first.

        if (filesDir == null || gson == null) {
            // If either filesDir or gson is null, throw an IllegalStateException
            throw new IllegalStateException(this + " is not properly initialized. Call initialize(Context) first.");
        }
    }

}

