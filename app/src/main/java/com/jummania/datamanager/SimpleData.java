package com.jummania.datamanager;

/**
 * The SimpleData class represents a simple data model with an integer and a string.
 * It is used for demonstration purposes in the DataManagerFactory library.
 */
public class SimpleData {

    private final int simpleInt;
    private final String simpleString;

    /**
     * Constructs a SimpleData object with the provided integer and string values.
     *
     * @param simpleInt    The integer value.
     * @param simpleString The string value.
     */
    public SimpleData(int simpleInt, String simpleString) {
        this.simpleInt = simpleInt;
        this.simpleString = simpleString;
    }

    /**
     * Gets the integer value of SimpleData.
     *
     * @return The integer value.
     */
    public int getSimpleInt() {
        return simpleInt;
    }

    /**
     * Gets the string value of SimpleData.
     *
     * @return The string value.
     */
    public String getSimpleString() {
        return simpleString;
    }
}
