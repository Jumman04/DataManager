package com.jummania.model;

/**
 * A class representing pagination information.
 * This class holds details about the current page, previous page, next page, and total pages.
 * <p>
 * Created by Jummania on 8, May, 2025.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
public class Pagination {
    public Integer previousPage;
    public int currentPage;
    public Integer nextPage;
    public int totalPages;


    /**
     * Constructs a Pagination object with the specified page information.
     *
     * @param previousPage the previous page number, or {@code null} if there is no previous page
     * @param currentPage  the current page number
     * @param nextPage     the next page number, or {@code null} if there is no next page
     * @param totalPages   the total number of pages
     */
    public Pagination(Integer previousPage, int currentPage, Integer nextPage, int totalPages) {
        this.previousPage = previousPage;
        this.currentPage = currentPage;
        this.nextPage = nextPage;
        this.totalPages = totalPages;
    }


    /**
     * Returns a string representation of the Pagination object.
     * The string contains the pagination details: previous page, current page, next page, and total pages.
     *
     * @return a string representing the Pagination object
     */
    @Override
    public String toString() {
        return String.format("Pagination{previousPage = %s, currentPage = %d, nextPage = %s, totalPages = %d}", previousPage, currentPage, nextPage, totalPages);
    }
}

