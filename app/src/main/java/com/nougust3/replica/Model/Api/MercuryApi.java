package com.nougust3.replica.Model.Api;

import com.nougust3.replica.Model.Content;
import com.nougust3.replica.Utils.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface MercuryApi {

    @Headers({
        "Content-Type: application/json;charset=utf-8",
            "x-api-key: " + Constants.PARSER_API_KEY
    })
    @GET("/parser")
    Call<Content> getData(@Query("url") String url);
}