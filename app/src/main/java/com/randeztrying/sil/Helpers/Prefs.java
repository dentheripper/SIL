package com.randeztrying.sil.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randeztrying.sil.Models.App;
import com.randeztrying.sil.Models.Note;

import java.util.ArrayList;
import java.util.List;

public class Prefs {

    private static final String SHARED_PREFS = "SIL-Prefs";
    private static final Gson gson = new Gson();

    public static void write(Context context, String key, String text) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, text);
        editor.apply();
    }

    public static String read(Context context, String key) {
        return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
                .getString(key, null);
    }

    public static void writeObject(Context context, String key, List value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        prefsEditor.putString(key, json);
        prefsEditor.apply();
    }

    public static String getJson(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static void writeFolderApps(Context context, List<App> list, String folderName) {
        writeObject(context, folderName, list);
    }

    public static List<App> getFolderApps(Context context, String folderName) {
        String json = getJson(context, folderName);
        if (gson.fromJson(json, new TypeToken<List<App>>() {}.getType()) == null) {
            return new ArrayList<>();
        } else return gson.fromJson(json, new TypeToken<List<App>>() {}.getType());
    }

    public static List<Note> getNotes(Context context) {
        String json = getJson(context, "notes");
        List<Note> notes = gson.fromJson(json, new TypeToken<List<Note>>() {}.getType());
        if (notes == null)
            return new ArrayList<>();
        else return notes;
    }

    public static void saveNotes(Context context, List<Note> users) {
        writeObject(context, "notes", users);
    }
}
