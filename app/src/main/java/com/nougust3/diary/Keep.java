package com.nougust3.diary;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import com.nougust3.diary.models.MercuryApi;
import com.nougust3.diary.utils.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Keep extends Application {

    private static MercuryApi mercuryApi;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.PARSER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MartaRegular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mercuryApi = retrofit.create(MercuryApi.class);
    }

    public static MercuryApi getApi() {
        return mercuryApi;
    }

}

