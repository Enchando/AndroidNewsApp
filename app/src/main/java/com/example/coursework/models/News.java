package com.example.coursework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Used for returning the article for the news.
 */

public class News {

    @SerializedName("articles")
    @Expose
    private List<Article> article;


    public List<Article> getArticle() {
        return article;
    }
}