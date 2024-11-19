package com.jummania;

import com.google.gson.Gson;
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
 * * Created by Jummania on 20,November,2024.
 * * Email: sharifuddinjumman@gmail.com
 * * Dhaka, Bangladesh.
 */
public class DataManager2 implements Jummania {
    private static DataManager2 dataManager;
    private final Gson gson;
    private final File filesDir;
    private OnDataChangeListener onDataChangeListener;


    public DataManager2(File filesDir) {
        if (filesDir == null)
            throw new IllegalArgumentException("The 'filesDir' argument cannot be null.");
        this.filesDir = new File(filesDir, "DataManager");
        gson = new Gson();
        if (!this.filesDir.exists()) {
            if (this.filesDir.mkdirs())
                System.out.println("Folder created successfully: " + this.filesDir.getAbsolutePath());
            else System.err.println("Failed to create folder");
        } else System.out.println("Folder already exists: " + this.filesDir.getAbsolutePath());
    }


    private InputStreamReader getInputStreamReader(String key) {
        throwExceptionIfNull();
        try {
            if (filesDir.canRead()) {
                File file = getFile(key);
                if (file.exists()) {
                    BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(file.toPath()), 16 * 1024);
                    return new InputStreamReader(inputStream);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    private File getFile(String key) {
        return new File(filesDir, key);
    }


    private void throwExceptionIfNull() {
        if (filesDir == null || gson == null) {
            throw new IllegalStateException(this + " is not properly initialized. Call initialize(filesDir) first.");
        }
    }

    @Override
    public String getString(String key, String defValue) {
        try {
            return getObject(key, String.class);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return defValue;
    }

    @Override
    public int getInt(String key, int defValue) {
        try {
            return getObject(key, Integer.class);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        try {
            return getObject(key, Long.class);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        try {
            return getObject(key, Float.class);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return defValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        try {
            return getObject(key, Boolean.class);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return defValue;
    }

    @Override
    public <T> T getObject(String key, Type type) {
        throwExceptionIfNull();
        if (key == null || type == null) {
            throw new IllegalArgumentException("Key or value cannot be null");
        }
        try (InputStreamReader inputStreamReader = getInputStreamReader(key)) {
            if (inputStreamReader != null) {
                return gson.fromJson(inputStreamReader, type);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public <T> T getParameterized(String key, Type rawType, Type... typeArguments) {
        return getObject(key, TypeToken.getParameterized(rawType, typeArguments).getType());
    }

    @Override
    public <T> List<T> getList(String key, Type type) {
        List<T> dataList = new ArrayList<>();
        int index = 0;
        boolean hasMoreData = true;

        Type listType = TypeToken.getParameterized(List.class, type).getType();

        while (hasMoreData) {
            // Attempt to retrieve a batch of data based on the current index
            List<T> batchData = getObject(key + "." + index, listType);
            if (batchData != null) {
                dataList.addAll(batchData);
                index++;
            } else {
                hasMoreData = false;
            }
        }

        return dataList;
    }

    @Override
    public boolean contains(String key) {
        return getFile(key).exists();
    }

    @Override
    public void registerOnDataChangeListener(OnDataChangeListener listener) {
        onDataChangeListener = listener;
    }

    @Override
    public void unregisterOnDataChangeListener() {
        onDataChangeListener = null;
    }

    @Override
    public void putString(String key, String value) {
        if (key == null || value == null)
            throw new IllegalArgumentException("Key or value cannot be null");

        throwExceptionIfNull();

        File file = getFile(key);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
                writer.write(value);
                writer.close();
                fos.close();

                if (onDataChangeListener != null) {
                    onDataChangeListener.onDataChanged(key);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void putInt(String key, int value) {
        putString(key, String.valueOf(value));
    }

    @Override
    public void putLong(String key, long value) {
        putString(key, String.valueOf(value));
    }

    @Override
    public void putFloat(String key, float value) {
        putString(key, String.valueOf(value));
    }

    @Override
    public void putBoolean(String key, boolean value) {
        putString(key, String.valueOf(value));
    }

    @Override
    public void putObject(String key, Object value) {
        putString(key, gson.toJson(value));
    }

    @Override
    public <T> void putList(String key, List<T> value) {

        int batchSize = calculateBatchSize(value.size(), 9999);
        int pos = 0;

        for (int i = 0; i < value.size(); i += batchSize) {
            List<T> batch = value.subList(i, Math.min(i + batchSize, value.size()));
            putString(key + "." + pos++, gson.toJson(batch));
        }
    }

    @Override
    public void remove(String key) {
        int index = 0;
        boolean hasMoreFile = true;

        while (hasMoreFile) {
            File file = getFile(key + "." + index);
            if (remove(file)) {
                index++;
            } else {
                remove(getFile(key));
                hasMoreFile = false;
            }
        }
    }

    @Override
    public void clear() {
        if (filesDir != null) {
            File[] files = filesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    remove(file);
                }
            } else {
                System.out.println("No files found in folder: " + filesDir.getAbsolutePath());
            }
        } else {
            System.err.println("Folder does not exist");
        }
    }

    private boolean remove(File file) {
        if (file != null && file.exists()) {
            if (file.delete()) {
                System.out.println("File deleted successfully: " + file.getAbsolutePath());
            } else {
                System.err.println("Failed to delete file: " + file.getAbsolutePath());
            }
            return true;
        }
        return false;
    }

    private int calculateBatchSize(int dataSize, int maxArraySize) {
        int batchSize = Math.min(dataSize, maxArraySize);
        return Math.max(batchSize, 1);
    }
}
