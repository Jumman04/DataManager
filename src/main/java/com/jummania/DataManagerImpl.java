package com.jummania;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.jummania.model.MetaData;
import com.jummania.model.PaginatedData;
import com.jummania.model.Pagination;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
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
 * Created by Jummania on 20, November 2024.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
final class DataManagerImpl implements DataManager {

    // Converter instance for serializing and deserializing data
    private final Converter converter;

    // Directory where the data will be stored
    private final File filesDir;
    private final int defaultCharBufferSize = 16 * 1024;
    private final ConcurrentHashMap<String, ReadWriteLock> lockMap = new ConcurrentHashMap<>();

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

        // Check if the directory exists and create it if necessary
        if (!this.filesDir.exists()) {
            if (!this.filesDir.mkdirs()) {
                System.err.println("Failed to create folder: " + this.filesDir.getAbsolutePath());
            }
        }
    }


    @Override
    public void saveObject(String key, Object value, Type typeOfSrc) {

        if (key == null) {
            throw new IllegalArgumentException("The 'key' argument cannot be null.");
        }

        // If the value is null, remove the corresponding entry and return
        if (value == null) {
            remove(key);
            return;
        }

        try {
            writeToFile(key, value, typeOfSrc);

            // Notify the listener about data changes, if applicable
            if (dataObserver != null) dataObserver.onDataChange(key);

        } catch (Exception e) {
            notifyError("Error saving data for key: '" + key + "'", e);
        }
    }


    @Override
    public <T> T getObject(String key, Type type) {
        try (Reader reader = getReader(key)) {
            return fromReader(reader, type);
        } catch (FileNotFoundException e) {
            notifyError("File not found for key '" + key + "'", e);
        } catch (IOException | JsonParseException e) {
            notifyError("Failed to read or parse data for key '" + key + "'", e);
        } catch (Exception e) {
            notifyError("Unexpected error while reading key '" + key + "'", e);
        }

        // Contract: return null on any failure
        return null;
    }


    @Override
    public String getString(String key, String defValue) {
        try (Reader reader = getReader(key)) {
            StringBuilder sb = new StringBuilder();
            int read;
            char[] buffer = new char[defaultCharBufferSize];
            while ((read = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, read);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            notifyError("File not found for key '" + key + "'", e);
        } catch (Exception e) {
            notifyError("Failed to read data for key '" + key + "'", e);
        }
        return defValue;
    }


    @Override
    public <E> List<E> getFullList(String key, Class<E> eClass, boolean reverse) {

        ReadWriteLock lock = getLock(key);
        lock.readLock().lock();

        try {
            MetaData metaData = getMetaData(key);

            // If metadata not found, return an empty list early
            if (metaData == null) {
                return new ArrayList<>();
            }

            final int totalPages = metaData.getTotalPages();
            if (totalPages <= 0) {
                return new ArrayList<>();
            }

            // Prepare the result list and the List<E> type token
            List<E> dataList = new ArrayList<>(Math.max(0, metaData.getItemCount()));

            // Ensure consistent key formatting
            key += ".";

            final int startPage = metaData.getStartPage();

            int start = reverse ? totalPages : startPage, end = reverse ? startPage : totalPages, step = reverse ? -1 : 1;
            List<E> batch;
            Type listType = getParameterized(List.class, eClass);

            for (int i = start; reverse ? i >= end : i <= end; i += step) {
                batch = getObject(key + i, listType);
                if (batch == null) break;
                dataList.addAll(batch);
            }

            return dataList;
        } finally {
            lock.readLock().unlock();
        }
    }


    @Override
    public <E> PaginatedData<E> getPagedList(String key, Class<E> eClass, int page, boolean reverse) {
        ReadWriteLock lock = getLock(key);
        lock.readLock().lock();

        try {
            MetaData metaData = getMetaData(key);
            if (metaData == null) {
                return getEmptyPaginateData(page, 0);
            }

            int start = metaData.getStartPage();
            int end = metaData.getTotalPages();
            int activePagesCount = (end - start) + 1;

            // Validation: Requested page must be within the current available range
            if (page < 1 || page > activePagesCount) {
                return getEmptyPaginateData(page, activePagesCount);
            }

            // Calculate the actual filename index
            // If reverse=false: Page 1 is startPage, Page 2 is startPage + 1
            // If reverse=true:  Page 1 is endPage, Page 2 is endPage - 1
            int targetFileIndex = reverse ? (end - page + 1) : (start + page - 1);

            Type listType = getParameterized(List.class, eClass);
            List<E> pageData = getObject(key + "." + targetFileIndex, listType);

            if (pageData == null) pageData = Collections.emptyList();

            // Handle navigation relative to the user's requested 'page' number
            Integer previousPage = (page > 1) ? page - 1 : null;
            Integer nextPage = (page < activePagesCount) ? page + 1 : null;

            Pagination pagination = new Pagination(previousPage, page, nextPage, activePagesCount);
            return new PaginatedData<>(pageData, pagination);

        } finally {
            lock.readLock().unlock();
        }
    }


    @Override
    public <E> void saveList(String key, List<E> list, int listSizeLimit, int maxBatchSize) {

        ReadWriteLock lock = getLock(key);
        lock.writeLock().lock();

        try {
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

                Class<?> listClass = list.getClass();

                // Split the list into smaller batches and store each one
                for (int i = 0; i < listSizeLimit; i += batchSizeLimit) {
                    List<E> batch = list.subList(i, Math.min(i + batchSizeLimit, listSizeLimit));
                    writeToFile(key + ++pos, batch, listClass);  // Store each batch with a unique key
                }

                // Save metadata about the paginated list
                writeToFile(key + "meta", MetaData.toMeta(1, pos, listSizeLimit, maxBatchSize), null);

                // Notify the listener about data changes, if applicable
                if (dataObserver != null) dataObserver.onDataChange(key);

            } else {
                remove(key);
            }
        } catch (Exception e) {
            notifyError("Error saving list for key: '" + key + "'", e);
        } finally {
            lock.writeLock().unlock();
        }
    }


    @Override
    public <E> void appendToList(String key, E element, Class<E> eClass, int listSizeLimit, int maxBatchSize, boolean addFirst, Function<E, String> idExtractor) {
        ReadWriteLock lock = getLock(key);
        lock.writeLock().lock();

        try {
            if (element == null) return;
            MetaData metaData = getMetaData(key);
            if (metaData == null) {
                saveList(key, Collections.singletonList(element), listSizeLimit, maxBatchSize);
                return;
            }

            int startPage = metaData.getStartPage();
            int totalPage = metaData.getTotalPages();
            int itemCount = metaData.getItemCount();
            maxBatchSize = metaData.getMaxBatchSize();

            String baseKey = key + ".";

            // Step 1: Handle List Size Limit (O(1) operation)
            if (itemCount >= listSizeLimit) {
                getFile(baseKey + startPage).delete();
                startPage++;
                itemCount = Math.max(0, itemCount - maxBatchSize);
            }

            // Step 2: Load the current active batch
            Type listType = getParameterized(List.class, eClass);
            List<E> lastPage = getObject(baseKey + totalPage, listType);
            if (lastPage == null) lastPage = new ArrayList<>(1); // Optimized capacity

            String uniqueId = null;

            // Step 3: Fast Indexed Duplicate Removal
            if (idExtractor != null) {
                uniqueId = idExtractor.apply(element);

                int position = getInt(baseKey + "index." + uniqueId);
                if (position >= startPage) {
                    boolean removed = false;
                    if (position == totalPage) {
                        removed = removeById(lastPage, uniqueId, idExtractor);
                    } else {
                        String oldPageKey = baseKey + position;
                        List<E> olderPage = getObject(oldPageKey, listType);
                        if (removeById(olderPage, uniqueId, idExtractor)) {
                            writeToFile(oldPageKey, olderPage, listType);
                            removed = true;
                        }
                    }
                    if (removed) --itemCount;
                }
            }

            // Step 4: Handle Page Rotation
            if (lastPage.size() >= maxBatchSize) {
                ++totalPage;
                lastPage = new ArrayList<>(1);
            }

            // Step 5: Add Element
            if (addFirst) lastPage.add(0, element);
            else lastPage.add(element);

            // Step 6: Atomic Writes
            writeToFile(baseKey + totalPage, lastPage, listType);
            writeToFile(baseKey + "meta", MetaData.toMeta(startPage, totalPage, itemCount + 1, maxBatchSize), null);

            if (uniqueId != null) {
                writeToFile(baseKey + "index." + uniqueId, Integer.toString(totalPage), null);
            }

            if (dataObserver != null) dataObserver.onDataChange(key);

        } catch (Exception e) {
            notifyError("Error in append cycle for: " + key, e);
        } finally {
            lock.writeLock().unlock();
        }
    }


    @Override
    public <E> boolean deleteFromListById(String key, Class<E> eClass, String uniqueId, Function<E, String> idExtractor) {
        ReadWriteLock lock = getLock(key);
        lock.writeLock().lock();

        try {
            if (uniqueId == null || idExtractor == null) return false;

            MetaData metaData = getMetaData(key);
            if (metaData == null) return false;

            int startPage = metaData.getStartPage();

            String baseKey = key + ".";
            String positionKey = baseKey + "index." + uniqueId;

            int position = getInt(positionKey);

            // Only proceed if the index points to an existing (not deleted) page
            if (position >= startPage) {
                Type listType = getParameterized(List.class, eClass);
                String targetPageKey = baseKey + position;

                List<E> pageData = getObject(targetPageKey, listType);

                // Use the private helper we wrote earlier
                if (removeById(pageData, uniqueId, idExtractor)) {
                    // 1. Save the updated page
                    writeToFile(targetPageKey, pageData, listType);

                    // 2. Update Metadata with decremented count
                    int totalPage = metaData.getTotalPages();
                    int itemCount = Math.max(0, metaData.getItemCount() - 1);
                    int maxBatchSize = metaData.getMaxBatchSize();
                    writeToFile(baseKey + "meta", MetaData.toMeta(startPage, totalPage, itemCount, maxBatchSize), null);

                    // 3. Delete the index file (Cleanup)
                    getFile(positionKey).delete();

                    // 4. Notify UI
                    if (dataObserver != null) dataObserver.onDataChange(key);

                    return true; // Successfully found and removed!
                }
            }
        } catch (Exception e) {
            notifyError("Error removing item by ID for key: '" + key + "'", e);
        } finally {
            lock.writeLock().unlock();
        }

        return false; // Item not found or error occurred
    }


    @Override
    public <E> boolean removeFromList(String key, Class<E> eClass, Predicate<? super E> itemToRemove) {
        ReadWriteLock lock = getLock(key);
        lock.writeLock().lock();

        try {
            if (itemToRemove == null) return false;

            MetaData metaData = getMetaData(key);
            if (metaData == null) return false;

            int startPage = metaData.getStartPage();
            int totalPage = metaData.getTotalPages();

            Type listType = getParameterized(List.class, eClass);
            String baseKey = key + ".";

            for (int i = totalPage; i >= startPage; --i) {
                String currentPageKey = baseKey + i;
                List<E> currentPage = getObject(currentPageKey, listType);

                if (currentPage == null)
                    return false; // immediately return false for corrupted/missing pages

                for (int j = currentPage.size() - 1; j >= 0; --j) {
                    if (itemToRemove.test(currentPage.get(j))) {
                        currentPage.remove(j);

                        // Save the modified page
                        writeToFile(currentPageKey, currentPage, listType);

                        // Update Metadata
                        int itemCount = Math.max(0, metaData.getItemCount() - 1);
                        int maxBatchSize = metaData.getMaxBatchSize();
                        writeToFile(baseKey + "meta", MetaData.toMeta(startPage, totalPage, itemCount, maxBatchSize), null);

                        if (dataObserver != null) dataObserver.onDataChange(key);

                        return true;
                    }
                }
            }

        } catch (Exception e) {
            notifyError("Error removing item for key: '" + key + "'", e);
        } finally {
            lock.writeLock().unlock();
        }
        return false; // Only returns false if NO page contained the item
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

    @Override
    public void remove(String key) {

        getFile(key + "." + "meta").delete();
        getFile(key).delete();

        // Notify the listener about data changes, if applicable
        if (dataObserver != null) dataObserver.onDataChange(key);
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
                    file.delete();
                }
            }
        } else {
            // If the directory does not exist, log the error
            notifyError("Folder does not exist", null);
        }
    }


    @Override
    public boolean exists(String key) {
        // Check if the file corresponding to the provided key exists in the data directory
        return getFile(key).exists();
    }


    @Override
    public long lastModified(String key) {
        return getFile(key).lastModified();
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


    private Reader getReader(String key) throws Exception {
        return new InputStreamReader(new FileInputStream(getFile(key)), StandardCharsets.UTF_8);
    }


    private File getFile(String key) {
        // Return the File object located in the directory with the provided key
        return new File(filesDir, key);
    }


    private MetaData getMetaData(String key) {
        return getObject(key + ".meta", MetaData.class);
    }


    private void notifyError(String message, Throwable error) {
        if (dataObserver != null) dataObserver.onError(new Throwable(message, error));

    }


    private <E> PaginatedData<E> getEmptyPaginateData(int currentPage, int totalPages) {
        return new PaginatedData<>(Collections.emptyList(), new Pagination(null, currentPage, null, totalPages));
    }


    private <E> boolean removeById(List<E> list, String uniqueId, Function<E, String> idExtractor) {
        if (list == null || uniqueId == null || idExtractor == null) return false;

        for (int i = list.size() - 1; i >= 0; --i) {
            E item = list.get(i);
            if (item == null) continue;

            // Compare the extracted ID with the one we are looking for
            if (uniqueId.equals(idExtractor.apply(item))) {
                list.remove(i);
                return true; // Match found and removed
            }
        }
        return false;
    }

    private ReadWriteLock getLock(String key) {
        return lockMap.computeIfAbsent(key, k -> new ReentrantReadWriteLock());
    }

    private void writeToFile(String key, Object value, Type typeOfSrc) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getFile(key)), StandardCharsets.UTF_8), defaultCharBufferSize)) {
            if (value instanceof String) writer.write((String) value);
            else toJson(value, typeOfSrc, writer);
        }
    }

}