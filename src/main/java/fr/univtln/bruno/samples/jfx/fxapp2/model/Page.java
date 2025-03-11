package fr.univtln.bruno.samples.jfx.fxapp2.model;

import java.util.List;

/**
 * Represents a page of results for pagination.
 *
 * @param <T> the type of elements in the page
 */
public record Page<T>(
    long dataSize,             // Total number of items in the dataset
    int pageSize,              // Number of items per page
    int pageNumber,            // Current page number (1-based)
    List<T> content,           // Content of the current page
    int totalPages             // Total number of pages available
) {
    /**
     * Constructor with backward compatibility for code that doesn't provide totalPages.
     */
    public Page(long dataSize, int pageSize, int pageNumber, List<T> content) {
        this(dataSize, pageSize, pageNumber, content, 
             (int) Math.ceil((double) dataSize / pageSize));
    }
}