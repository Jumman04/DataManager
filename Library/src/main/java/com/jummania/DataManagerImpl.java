package com.jummania;

import com.google.gson.JsonIOException;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

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


    @Override
    public void saveObject(String key, Object value, Type typeOfSrc) {

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
            if (value instanceof String) writer.write((String) value);
            else toJson(value, typeOfSrc, writer);

            // Notify the listener about data changes, if applicable
            if (dataObserver != null) {
                dataObserver.onDataChange(key);
            }

        } catch (Exception e) {
            notifyError(new IOException("Error saving data for key: '" + key + "'. Error: " + e.getMessage(), e));
        }
    }


    @Override
    public <T> T getObject(String key, Type type) {

        try (Reader reader = getReader(key)) {
            return fromReader(reader, type);
        } catch (FileNotFoundException e) {
            notifyError(new IOException("Failed to open file for key '" + key + "': " + e.getMessage(), e));
        } catch (IOException | JsonIOException e) {
            notifyError(new IOException("Error reading file for key '" + key + "': " + e.getMessage(), e));
        } catch (Exception e) {
            notifyError(new JsonSyntaxException("Error deserializing JSON for key '" + key + "': " + e.getMessage(), e));
        }

        // Return null if an error occurs
        return null;
    }


    @Override
    public String getRawString(String key) {

        try (BufferedReader bufferedReader = new BufferedReader(getReader(key))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            notifyError(new IOException("Failed to open file for key '" + key + "': " + e.getMessage(), e));
        } catch (Exception e) {
            notifyError(new IOException("Error reading file for key '" + key + "': " + e.getMessage(), e));
        }
        return null;
    }


    @Override
    public <E> List<E> getFullList(String key, Class<E> eClass) {
        // Initialize the list that will hold the retrieved objects
        List<E> dataList = new ArrayList<>();

        // Determine the full Type for the parameterized List
        Type listType = getParameterized(List.class, eClass);

        int position = 0;

        key += ".";

        while (true) {
            List<E> batchData = getObject(key + ++position, listType);
            if (batchData == null) return dataList;
            dataList.addAll(batchData);
        }
    }


    @Override
    public <E> PaginatedData<E> getPagedList(String key, Class<E> eClass, int page, boolean reverse) {
        MetaData metaData = getMetaData(key);
        if (metaData == null) {
            return getEmptyPaginateData(page, 0);
        }

        int totalPages = metaData.getTotalPages();

        // If reverse: flip the page index (1 => N, 2 => N-1, etc.)
        int targetPage = reverse ? (totalPages - page + 1) : page;
        if (targetPage < 1 || targetPage > totalPages) {
            return getEmptyPaginateData(page, totalPages);
        }

        Type listType = getParameterized(List.class, eClass);
        List<E> pageData = getObject(key + "." + targetPage, listType);
        if (pageData == null) pageData = Collections.emptyList();

        // Previous/Next still follow the user's requested page (not the flipped index)
        Integer previousPage = (page > 1) ? page - 1 : null;
        Integer nextPage = (page < totalPages) ? page + 1 : null;

        Pagination pagination = new Pagination(previousPage, page, nextPage, totalPages);
        return new PaginatedData<>(pageData, pagination);
    }


    @Override
    public <E> void saveList(String key, List<E> list, int listSizeLimit, int maxBatchSize) {
        // If the list becomes empty after removal, delete the key from storage
        if (list != null) {

            listSizeLimit = Math.min(list.size(), listSizeLimit);

            if (listSizeLimit == 0) {
                remove(key);
                return;
            }

            // Ensure the batch size is at least 1
            int batchSizeLimit = Math.max(Math.min(listSizeLimit, maxBatchSize), 1);
            int pos = 0;

            key += ".";

            // Split the list into smaller batches and store each one
            for (int i = 0; i < listSizeLimit; i += batchSizeLimit) {
                List<E> batch = list.subList(i, Math.min(i + batchSizeLimit, listSizeLimit));
                saveObject(key + ++pos, batch);  // Store each batch with a unique key
            }

            // Save metadata about the paginated list
            saveObject(key + "meta", MetaData.toMeta(pos, listSizeLimit, maxBatchSize));

            // Remove any old batch file beyond the new total
            remove(getFile(key + ++pos));

        } else {
            remove(key);
        }
    }


    @Override
    public <E> void appendToList(String key, E element, Class<E> eClass, int listSizeLimit, int maxBatchSize, Predicate<? super E> itemToRemove) {

        if (element == null) return;

        MetaData metaData = getMetaData(key);
        if (metaData == null) {
            // Create a new list if none exists
            saveList(key, Collections.singletonList(element), listSizeLimit, maxBatchSize);
            return;
        }

        int totalPage = metaData.getTotalPages();
        int itemCount = metaData.getItemCount();
        maxBatchSize = metaData.getMaxBatchSize();

        key += ".";

        // If list exceeds the size limit, shift files to remove the oldest batch
        if (itemCount >= listSizeLimit) {
            Path rootPath = filesDir.toPath();
            for (int i = 2; i <= totalPage; i++) {
                Path oldPath = rootPath.resolve(key + i);
                Path newPath = rootPath.resolve(key + (i - 1));
                try {
                    Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    break;
                }
            }
            --totalPage;
            itemCount -= maxBatchSize; // Approximation (actual size may vary)
        }

        // Load the last batch
        String fileKey = key + totalPage;
        Type listType = getParameterized(List.class, eClass);
        List<E> lastPage = getObject(fileKey, listType);
        if (lastPage == null) lastPage = new ArrayList<>(1);

        // Remove an existing matching item if predicate is provided
        if (itemToRemove != null) {
            boolean removed = removeFirstMatch(lastPage, itemToRemove);
            if (!removed) {
                List<E> removedList;
                for (int i = totalPage - 1; i > 0; --i) {
                    fileKey = key + i;
                    removedList = getObject(fileKey, listType);
                    if (removedList == null) break;
                    removed = removeFirstMatch(removedList, itemToRemove);
                    if (removed) {
                        saveObject(fileKey, removedList, listType);
                        break;
                    }
                }
            }
            if (removed) --itemCount;
        }

        // If the last batch is full, create a new page
        if (lastPage.size() >= maxBatchSize) {
            ++totalPage;
            lastPage = new ArrayList<>(1);
        }

        // Add the new element to the current batch and update metadata
        lastPage.add(element);
        saveObject(key + totalPage, lastPage, listType);
        saveObject(key + "meta", MetaData.toMeta(totalPage, itemCount + 1, maxBatchSize));
        remove(getFile(key + ++totalPage));
    }


    @Override
    public <T> T fromJson(String value, Class<T> tClass) {
        return converter.fromJson(value, tClass);
    }


    @Override
    public <T> T fromReader(Reader json, Type typeOfT) {
        return converter.fromReader(json, typeOfT);
    }


    @Override
    public String toJson(Object object) {
        return converter.toJson(object);
    }

    @Override
    public void toJson(Object src, Type typeOfSrc, Appendable writer) {
        converter.toJson(src, typeOfSrc, writer);
    }


    @Override
    public Type getParameterized(Type rawType, Type... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }


    public void remove(String key) {
        int position = 0;

        String baseKey = key + ".";

        while (remove(getFile(baseKey + ++position))) {
            // keep deleting until no more files exist
        }

        remove(getFile(baseKey + "meta"));

        // Remove the base file
        remove(getFile(key));

        // Notify the listener about data changes, if applicable
        if (dataObserver != null) {
            dataObserver.onDataChange(key);
        }
    }


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


    @Override
    public boolean contains(String key) {
        // Check if the file corresponding to the provided key exists in the data directory
        return getFile(key).exists();
    }


    @Override
    public void addDataObserver(DataObserver listener) {
        // Set the provided listener to be notified of data changes
        dataObserver = listener;
    }


    @Override
    public void removeDataObserver() {
        // Set the listener to null, effectively unregistering it
        dataObserver = null;
    }


    private Reader getReader(String key) throws FileNotFoundException {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return new InputStreamReader(new BufferedInputStream(new FileInputStream(getFile(key)), defaultCharBufferSize));
    }


    private boolean remove(File file) {
        return file != null && file.delete();
    }


    private File getFile(String key) {
        // Return the File object located in the directory with the provided key
        return new File(filesDir, key);
    }


    private MetaData getMetaData(String key) {
        return getObject(key + ".meta", MetaData.class);
    }


    private void notifyError(Throwable error) {
        if (dataObserver != null) {
            dataObserver.onError(error);
        }
    }


    private <E> PaginatedData<E> getEmptyPaginateData(int currentPage, int totalPages) {
        return new PaginatedData<>(Collections.emptyList(), new Pagination(null, currentPage, null, totalPages));
    }


    private <E> boolean removeFirstMatch(List<E> list, Predicate<? super E> filter) {
        if (list == null || filter == null) return false;
        for (int i = list.size() - 1; i >= 0; --i) {
            if (filter.test(list.get(i))) {
                list.remove(i);
                return true; // removed 1 element
            }
        }
        return false; // nothing removed
    }

}
