package com.jummania;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * DataManager class for managing data storage and retrieval using SharedPreferences.
 * *  * Created by Jummania on 07,January,2024.
 * *  * Email: sharifuddinjumman@gmail.com
 * *  * Dhaka, Bangladesh.
 */
public class DataManager {

    // Shared Preferences instance for data storage
    private static SharedPreferences sharedPreferences;

    // Gson instance for JSON serialization/deserialization
    private static Gson gson;

    /**
     * Gets the Type for a List of a specified data model class.
     *
     * @param dataModel The class of the data model.
     * @param <T>       The type of the data model.
     * @return Type representing a List of the specified data model.
     */
    private static <T> Type getListType(Class<T> dataModel) {
        return TypeToken.getParameterized(List.class, dataModel).getType();
    }

    /**
     * Retrieves a List of data of the specified data model from SharedPreferences.
     *
     * @param dataModel The class of the data model.
     * @param <T>       The type of the data model.
     * @return List of data of the specified data model, or an empty list if not found.
     */
    public static <T> List<T> getData(Class<T> dataModel) {
        throwExceptionIfNull();
        String jsonData = sharedPreferences.getString(dataModel.getSimpleName(), null);
        return (jsonData != null) ? gson.fromJson(jsonData, getListType(dataModel)) : new ArrayList<>();
    }

    /**
     * Saves a List of data of the specified data model to SharedPreferences.
     *
     * @param dataList The List of data to be saved.
     * @param <T>      The type of the data model.
     */
    public static <T> void saveData(List<T> dataList) {
        throwExceptionIfNull();
        sharedPreferences.edit().putString(dataList.getClass().getSimpleName(), gson.toJson(dataList, getListType(dataList.getClass()))).apply();
    }

    /**
     * Initializes the DataManager with the provided context.
     *
     * @param context The context used for initializing SharedPreferences.
     */
    public static synchronized void initialize(Context context) {
        if (gson == null)
            gson = new Gson();
        if (sharedPreferences == null)
            sharedPreferences = context.getSharedPreferences(DataManager.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * Throws an exception if either SharedPreferences or Gson is not properly initialized.
     */
    private static void throwExceptionIfNull() {
        if (sharedPreferences == null || gson == null)
            throw new IllegalStateException("DataManager is not properly initialized. Call initialize(Context) first.");
    }
}
