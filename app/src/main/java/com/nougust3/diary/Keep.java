package com.nougust3.diary;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import com.nougust3.diary.api.MercuryApi;
import com.nougust3.diary.Utils.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Keep extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private static MercuryApi mercuryApi;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.PARSER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

       CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mercuryApi = retrofit.create(MercuryApi.class);
    }

    public static MercuryApi getApi() {
        return mercuryApi;
    }

    public static Context getAppContext() {
        return Keep.context;
    }

}

