package com.jummania.model;


/**
 * Represents metadata for a paginated list.
 * <p>
 * This class stores information about a list that is split into pages:
 * <ul>
 *     <li>{@code totalPages} — the total number of pages currently in the list</li>
 *     <li>{@code itemCount} — the total number of items across all pages</li>
 *     <li>{@code batchSizeLimit} — the maximum number of items allowed per page</li>
 * </ul>
 * <p>
 * It also provides a utility method {@link #toMeta(int, int, int)} to generate
 * a JSON string representation of the metadata.
 */
public class MetaData {
    private final int totalPages;
    private final int itemCount;
    private final int maxBatchSize;

    /**
     * Constructs a new MetaData object.
     *
     * @param totalPages   the total number of pages
     * @param itemCount    the total number of items across all pages
     * @param maxBatchSize the maximum number of items per page
     */
    public MetaData(int totalPages, int itemCount, int maxBatchSize) {
        this.totalPages = totalPages;
        this.itemCount = itemCount;
        this.maxBatchSize = maxBatchSize;
    }

    /**
     * Generates a JSON string representing the metadata.
     *
     * @param totalPages   the total number of pages
     * @param itemCount    the total number of items
     * @param maxBatchSize the maximum number of items per page
     * @return a JSON string representing the metadata
     */
    public static String toMeta(int totalPages, int itemCount, int maxBatchSize) {
        return "{\"totalPages\":" + totalPages + ",\"itemCount\":" + itemCount + ",\"maxBatchSize\":" + maxBatchSize + "}";
    }

    /**
     * Returns the total number of pages.
     *
     * @return the total pages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * Returns the total number of items across all pages.
     *
     * @return the item count
     */
    public int getItemCount() {
        return itemCount;
    }

    /**
     * Returns the maximum number of items allowed per page.
     *
     * @return the max array size
     */
    public int getMaxBatchSize() {
        return maxBatchSize;
    }
}