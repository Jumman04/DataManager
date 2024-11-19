package com.jummania;

import java.lang.reflect.Type;
import java.util.List;

/**
 * * Created by Jummania on 20,November,2024.
 * * Email: sharifuddinjumman@gmail.com
 * * Dhaka, Bangladesh.
 */
public interface Jummania {

    // Get methods for primitive types and objects
    String getString(String key, String defValue);

    int getInt(String key, int defValue);

    long getLong(String key, long defValue);

    float getFloat(String key, float defValue);

    boolean getBoolean(String key, boolean defValue);

    <T> T getObject(String key, Type type);

    <T> T getParameterized(String key, Type rawType, Type... typeArguments);

    <T> List<T> getList(String key, Type type);

    default String getString(String key) {
        return getString(key, null);
    }

    default int getInt(String key) {
        return getInt(key, 0);
    }

    default long getLong(String key) {
        return getLong(key, 0L);
    }

    default float getFloat(String key) {
        return getFloat(key, 0F);
    }

    default boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    boolean contains(String key);

    // Data change listener registration
    void registerOnDataChangeListener(OnDataChangeListener listener);

    void unregisterOnDataChangeListener();

    void putString(String key, String value);

    // void interface for data modification

    void putInt(String key, int value);

    void putLong(String key, long value);

    void putFloat(String key, float value);

    void putBoolean(String key, boolean value);

    void putObject(String key, Object value);

    <T> void putList(String key, List<T> value);

    void remove(String key);

    void clear();  // Clear all keys

    // Listener for data changes
    interface OnDataChangeListener {
        void onDataChanged(String key);
    }

}

