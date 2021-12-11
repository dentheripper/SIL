package com.randeztrying.sil.Models.Weather;

import androidx.annotation.NonNull;

public class Common {

    public static String API_KEY = "da073df01e6336a9c4caf511148c22e7";
    public static String API_LINK = "http://api.openweathermap.org/data/2.5/forecast";

    @NonNull
    public static String apiRequest(String lat, String lng) {
        return API_LINK + String.format("?lat=%s&lon=%s&appid=%s&units=metric", lat, lng, API_KEY);
    }
}
