package com.example.coursework.api;

import com.example.coursework.models.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Gets the relevant information with certain criteria from the web news server.
 */

public interface ApiInterface {

    //General headlines for the country of the mobile.
    @GET("top-headlines")
    Call<News> getNews(
            @Query("country") String country ,
            @Query("apiKey") String apiKey
    );

    //Get all news headlines which includes a keyword, specified by the search bar.
    @GET("everything")
    Call<News> getNewsSearch(
            @Query("q") String keyword,
            @Query("apiKey") String apiKey
    );

    //Get the top headlines within a specified category.
    @GET("top-headlines")
    Call<News> getSpecificSearch(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

}

