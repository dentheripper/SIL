package com.randeztrying.sil.Controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randeztrying.sil.Models.App;

import java.util.ArrayList;
import java.util.List;

public class Prefs {
    private static final String SHARED_PREFS = "SIL-Prefs";
    public static void writeFolderApps(Context context, List<App> list, String folderName) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        prefsEditor.putString(folderName, json);
        prefsEditor.apply();
    }
    public static List<App> getFolderApps(Context context, String folderName) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String json = preferences.getString(folderName, "");
        if (new Gson().fromJson(json, new TypeToken<List<App>>() {}.getType()) == null) return new ArrayList<>();
        else return new Gson().fromJson(json, new TypeToken<List<App>>() {}.getType());
    }
}