package com.vnsolution.virtualassistant.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.vnsolution.virtualassistant.BuildConfig;

import static android.content.Context.MODE_PRIVATE;

public class AppCache {
    public static void save(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String retrieve(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        String value = sharedPref.getString(key, "");
        return value;
    }

    public static void remove(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.commit();
    }
}
