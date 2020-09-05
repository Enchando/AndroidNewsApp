package com.example.coursework.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Returns a Retrofit object to be used for constructing the HTTP call
 * request to the server newsapi.org.
 * Maps the JSON data to the main activity class using GsonConverterFactory,
 * to be used for displaying data.
 */

public class ApiClient {

    public static Retrofit retrofit;
    public static final String URL = "https://newsapi.org/v2/"; //News Source
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static Retrofit getApiClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        }

        return retrofit;
    }

}
