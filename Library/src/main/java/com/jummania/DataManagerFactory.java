package com.jummania;

import java.io.File;


/**
 * Factory class for creating and managing a singleton instance of DataManager.
 * Ensures that only one instance of DataManager is created and provides global access to it.
 * <p>
 * This class follows the Singleton design pattern to manage the instance of DataManager.
 * It is thread-safe and provides methods to create and retrieve the DataManager instance.
 * <p>
 * Created by Jummania on 20, November, 2024.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
public class DataManagerFactory {

    // The singleton instance of DataManager
    private static DataManager dataManager;

    // Private constructor to prevent instantiation of the factory
    private DataManagerFactory() {
    }


    /**
     * Creates and returns the singleton instance of DataManager.
     * If the DataManager instance does not exist, it is created with the provided filesDir.
     * This method is synchronized to ensure thread safety when creating the instance.
     *
     * @param filesDir The directory where data is to be stored.
     * @return The singleton instance of DataManager.
     */
    public static synchronized DataManager create(File filesDir) {
        // If the instance does not exist, create a new one
        if (dataManager == null) {
            dataManager = new DataManagerImpl(filesDir);
        }
        return dataManager;
    }


    /**
     * Retrieves the singleton instance of DataManager.
     * If the instance is not yet created, throws an IllegalStateException.
     *
     * @return The singleton instance of DataManager.
     * @throws IllegalStateException If the DataManager instance is not yet created.
     */
    public static DataManager getDataManager() {
        // Check if DataManager is initialized; if not, throw an exception
        if (dataManager == null) {
            throw new IllegalStateException("DataManagerFactory is not created. Call create(getFilesDir()) first.");
        }
        return dataManager;
    }

}