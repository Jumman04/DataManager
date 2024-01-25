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
 * The DataManager class is responsible for managing data serialization and deserialization using Gson.
 */
public class DataManager {

    private final Gson gson;
    private final File filesDir;

    /**
     * Constructs a DataManager object with the specified files directory.
     *
     * @param filesDir The directory where data files will be stored.
     */
    public DataManager(File filesDir) {
        gson = new Gson();

        // Create the folder
        this.filesDir = new File(filesDir, "DataManager");

        if (!this.filesDir.exists()) {
            if (this.filesDir.mkdirs())
                System.out.println("Folder created successfully: " + this.filesDir.getAbsolutePath());
            else
                System.err.println("Failed to create folder");
        } else
            System.out.println("Folder already exists: " + this.filesDir.getAbsolutePath());
    }

    /**
     * Gets the Type for a List of the specified data model class.
     *
     * @param dataModel The data model class.
     * @param <T>       The type of the data model.
     * @return The Type for a List of the specified data model class.
     */
    private <T> Type getListType(Class<T> dataModel) {
        return TypeToken.getParameterized(List.class, dataModel).getType();
    }

    /**
     * Gets the File object for the specified file name within the DataManager's directory.
     *
     * @param fileName The name of the file.
     * @return The File object for the specified file name.
     */
    private File getFile(String fileName) {
        return new File(filesDir, fileName);
    }

    /**
     * Reads JSON data from a file and returns it as a string for the specified data model class.
     *
     * @param dataModel The data model class.
     * @param <T>       The type of the data model.
     * @return The JSON data as a string.
     */
    public <T> String getJsonData(Class<T> dataModel) {
        throwExceptionIfNull();
        StringBuilder jsonString = new StringBuilder();

        try (InputStreamReader inputStreamReader = getInputStreamReader(dataModel)) {
            if (inputStreamReader != null) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    // Read the contents of the file into a StringBuilder
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        jsonString.append(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString.toString();
    }

    /**
     * Reads JSON data from a file and returns it as a list for the specified data model class.
     *
     * @param dataModel The data model class.
     * @param <T>       The type of the data model.
     * @return The list of data objects.
     */
    public <T> List<T> getData(Class<T> dataModel) {
        try (InputStreamReader inputStreamReader = getInputStreamReader(dataModel)) {
            if (inputStreamReader != null)
                return gson.fromJson(inputStreamReader, getListType(dataModel));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Gets an InputStreamReader for the specified data model class.
     *
     * @param dataModel The data model class.
     * @param <T>       The type of the data model.
     * @return The InputStreamReader for the specified data model class.
     */
    private <T> InputStreamReader getInputStreamReader(Class<T> dataModel) {
        throwExceptionIfNull();

        try {
            if (filesDir.canRead()) {
                File file = getFile(dataModel.getSimpleName());
                if (file.exists()) {
                    BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(file.toPath()), 16 * 1024);
                    return new InputStreamReader(inputStream);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Saves a list of data objects to a file for the specified data model class.
     *
     * @param dataList The list of data objects to be saved.
     * @param tClass   The class of the data model.
     * @param <T>      The type of the data model.
     */
    public <T> void saveData(List<T> dataList, Class<T> tClass) {
        if (dataList == null)
            throw new IllegalArgumentException("dataList cannot be null");
        throwExceptionIfNull();
        String data = gson.toJson(dataList, getListType(tClass));
        try {
            // Construct the full path to the file in the app's internal storage
            File file = getFile(tClass.getSimpleName());

            // Open a private file in the app's internal storage
            FileOutputStream fos = new FileOutputStream(file);

            // Wrap the FileOutputStream with a BufferedWriter for efficient writing
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

            // Write the data to the file
            writer.write(data);

            // Close the streams
            writer.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the data file associated with the specified data model class.
     *
     * @param tClass The class of the data model.
     * @param <T>    The type of the data model.
     */
    public <T> void deleteData(Class<T> tClass) {
        File file = getFile(tClass.getSimpleName());
        if (file.exists()) {
            if (file.delete())
                System.out.println("File deleted successfully: " + file.getAbsolutePath());
            else
                System.err.println("Failed to delete file: " + file.getAbsolutePath());

        } else
            System.out.println("File does not exist: " + file.getAbsolutePath());
    }

    /**
     * Deletes all data files in the DataManager's directory.
     */
    public void clearAll() {
        if (filesDir != null) {
            File[] files = filesDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file != null && file.exists()) {
                        if (file.delete()) {
                            System.out.println("File deleted successfully: " + file.getAbsolutePath());
                        } else {
                            System.err.println("Failed to delete file: " + file.getAbsolutePath());
                        }
                    }
                }
            } else {
                System.out.println("No files found in folder: " + filesDir.getAbsolutePath());
            }
        } else {
            System.err.println("Folder does not exist");
        }
    }

    /**
     * Throws an exception if the DataManager is not properly initialized.
     */
    private void throwExceptionIfNull() {
        if (filesDir == null || gson == null)
            throw new IllegalStateException("DataManager is not properly initialized. Call initialize(Context) first.");
    }
}
