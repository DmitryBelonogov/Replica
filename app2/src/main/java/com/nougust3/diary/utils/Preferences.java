package com.nougust3.diary.Utils;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nougust3.diary.Keep;

public class Preferences {

    private SharedPreferences preferences;

    @SuppressLint("StaticFieldLeak")
    private static Preferences instance = null;

    public static synchronized Preferences getInstance() {
        if (instance == null) {
            instance = new Preferences();
        }

        return instance;
    }

    private Preferences() {
        preferences = PreferenceManager.getDefaultSharedPreferences(Keep.getAppContext());
    }

    public boolean get(String item) {
        return preferences.getBoolean(item, false);
    }
}
