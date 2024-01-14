package com.jummania.datamanager;

import androidx.annotation.NonNull;

public class SimpleData {

    int simpleInt;
    String simpleString;
    public SimpleData(int simpleInt, String simpleString){
        this.simpleInt = simpleInt;
        this.simpleString = simpleString;
    }

    public int getSimpleInt() {
        return simpleInt;
    }

    public String getSimpleString() {
        return simpleString;
    }

    @NonNull
    @Override
    public String toString() {
        return "Position: " + simpleInt + ",   Text: " + simpleString;
    }
}
