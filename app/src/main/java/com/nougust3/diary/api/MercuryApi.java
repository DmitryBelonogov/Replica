package com.nougust3.diary.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import com.nougust3.diary.models.Content;
import com.nougust3.diary.Utils.Constants;

public interface MercuryApi {

    @Headers({
        "Content-Type: application/json;charset=utf-8",
            "x-api-key: " + Constants.PARSER_API_KEY
    })
    @GET("/parser")
    Call<Content> getData(@Query("url") String url);
}