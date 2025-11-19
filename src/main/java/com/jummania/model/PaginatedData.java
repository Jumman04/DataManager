package com.jummania.model;

import java.util.List;

/**
 * A generic class representing a paginated data set.
 * This class holds a list of data items and pagination information.
 * <p>
 * Created by Jummania on 8, May 2025.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 *
 * @param <E> the type of data items in the list
 */
public final class PaginatedData<E> {
    public final List<E> data;
    public final Pagination pagination;


    /**
     * Constructs a PaginatedData object with the specified data and pagination information.
     *
     * @param data       the list of data items for the current page
     * @param pagination the pagination information (e.g., current page, total pages)
     */
    public PaginatedData(List<E> data, Pagination pagination) {
        this.data = data;
        this.pagination = pagination;
    }


    /**
     * Returns a string representation of the PaginatedData object.
     * The string contains the data and pagination details.
     *
     * @return a string representing the PaginatedData object
     */
    @Override
    public String toString() {
        return String.format("PaginatedData{data = %s, pagination = %s}", data, pagination);
    }
}
