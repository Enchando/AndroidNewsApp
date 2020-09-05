package com.example.coursework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Used for getting the names of the source.
 */

public class Source {

    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }
}
