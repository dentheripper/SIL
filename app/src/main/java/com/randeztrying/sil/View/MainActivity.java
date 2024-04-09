package com.randeztrying.sil.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.randeztrying.sil.Controller.Prefs;
import com.randeztrying.sil.Models.App;
import com.randeztrying.sil.R;
import com.randeztrying.sil.View.Adapters.AppsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private JSONObject weatherJson;
    private final Handler timeHandler = new Handler();
    private final Handler weatherHandler = new Handler();
    private TextView time, date;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desktop);
        isPermissionGranted(this);

        time = findViewById(R.id.time);
        date = findViewById(R.id.date);

        TextView systemFolder = findViewById(R.id.system_folder);
        TextView internetFolder = findViewById(R.id.internet_folder);
        TextView socialFolder = findViewById(R.id.social_folder);
        TextView lifeFolder = findViewById(R.id.life_folder);
        TextView otherFolder = findViewById(R.id.other_folder);

        ImageView call = findViewById(R.id.call);
        ImageView settings = findViewById(R.id.settings);

        updateTime();
        getWeather();

        time.setOnClickListener(v -> {
            Dialog dialogWindow = new Dialog(v.getRootView().getContext());
            dialogWindow.setContentView(R.layout.layout_calendar);
            dialogWindow.show();
        });
        systemFolder.setOnClickListener(v -> openFolder(v, "System"));
        internetFolder.setOnClickListener(v -> openFolder(v, "Internet"));
        socialFolder.setOnClickListener(v -> openFolder(v, "Social"));
        lifeFolder.setOnClickListener(v -> openFolder(v, "Life"));
        otherFolder.setOnClickListener(v -> openFolder(v, "Other"));
        call.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, null);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent, ActivityOptions.makeCustomAnimation(getApplicationContext(), 0, 0).toBundle());
        });
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_SETTINGS, null);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent, ActivityOptions.makeCustomAnimation(getApplicationContext(), 0, 0).toBundle());
        });
    }

    private void openFolder(View v, String folder) {
        Dialog dialogWindow = new Dialog(v.getRootView().getContext());
        dialogWindow.setContentView(R.layout.alert_folder);

        TextView folderName = dialogWindow.findViewById(R.id.folder_name);
        ImageView addApp = dialogWindow.findViewById(R.id.add_app);
        ImageView clearFolder = dialogWindow.findViewById(R.id.clear_folder);
        RecyclerView recyclerView = dialogWindow.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        folderName.setText(folder);
        List<App> appList = Prefs.getFolderApps(getApplicationContext(), folder);
        recyclerView.setAdapter(new AppsAdapter(getApplicationContext(), appList, dialogWindow, getPackageManager(), folder));

        dialogWindow.show();

        addApp.setOnClickListener(v1 -> {
            Intent intent = new Intent(MainActivity.this, AppSelectActivity.class);
            intent.putExtra("folder", folder);
            startActivity(intent, ActivityOptions.makeCustomAnimation(getApplicationContext(), 0, 0).toBundle());
            dialogWindow.cancel();
        });
        clearFolder.setOnClickListener(v1 -> {
            if (appList != null) {
                appList.clear();
                Prefs.writeFolderApps(getApplicationContext(), appList, folder);
                recyclerView.setAdapter(new AppsAdapter(getApplicationContext(), appList, dialogWindow, getPackageManager(), folder));
            }
        });
    }

    public void isPermissionGranted(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                1);
    }

    @SuppressLint("SetTextI18n")
    private void updateTime() {
        Runnable runnable = this::updateTime;
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        Calendar calendar = Calendar.getInstance();
        String[] dowName = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        time.setText(getDateTime((double) System.currentTimeMillis(), "HH:mm"));
        date.setText(dowName[calendar.get(Calendar.DAY_OF_WEEK)] + ", " + getDateTime((double) System.currentTimeMillis(), "dd MMM yyyy") + ", " + percentage + "%");
        timeHandler.postDelayed(runnable, 50000);
    }

    private void getWeather() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", (dialog, which) ->
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).setNegativeButton("No", (dialog, which) -> dialog.cancel());
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        List<String> providers = manager.getProviders(true);
                        Location location = null;
                        for (String provider : providers) {
                            Location l = manager.getLastKnownLocation(provider);
                            if (l == null) continue;
                            if (location == null || l.getAccuracy() < location.getAccuracy()) location = l;
                        }
                        if (location != null) updateWeather(location.getLatitude(), location.getLongitude());
                        else Toast.makeText(this, "Unable to find location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateWeather(double lat, double lng) {
        Runnable runnable = () -> updateWeather(lat, lng);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String url = getString(R.string.owm_api_link) + String.format("?lat=%s&lon=%s&appid=%s&units=metric", lat, lng, getString(R.string.owm_api_key));
            String stream = getHTTPData(url);
            handler.post(() -> {
                try {
                    weatherJson = new JSONObject(stream);
                    JSONArray weatherArray = weatherJson.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    JSONObject main = weatherJson.getJSONObject("main");
                    JSONObject wind = weatherJson.getJSONObject("wind");
                    JSONObject sys = weatherJson.getJSONObject("sys");

                    if (weatherJson != null) {
                        TextView weather = findViewById(R.id.weather);
                        weather.setText(weatherJson.getString("name") + "\n" +
                                weatherObject.getString("description").substring(0, 1).toUpperCase() +
                                weatherObject.getString("description").substring(1) + ", " +
                                String.format("%.0f°", main.getDouble("temp")));
                        weather.setOnClickListener(v -> {
                            Dialog dialogWindow = new Dialog(v.getRootView().getContext());
                            dialogWindow.setContentView(R.layout.layout_weather);
                            Objects.requireNonNull(dialogWindow.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            ImageView img = dialogWindow.findViewById(R.id.weather_img);
                            TextView desc = dialogWindow.findViewById(R.id.description);
                            TextView temp = dialogWindow.findViewById(R.id.temp);
                            TextView feelsLike = dialogWindow.findViewById(R.id.feels_like);
                            TextView info = dialogWindow.findViewById(R.id.info);

                            try {
                                Glide.with(getApplicationContext()).load("https://openweathermap.org/img/wn/" +
                                        weatherObject.getString("icon") + ".png").into(img);
                                desc.setText(weatherObject.getString("description").substring(0, 1).toUpperCase() +
                                        weatherObject.getString("description").substring(1));

                                temp.setText(String.format("%.0f°", main.getDouble("temp")));
                                feelsLike.setText("Feels like: " + String.format("%.0f°", main.getDouble("feels_like")));

                                info.setText("Wind speed: " + (int) wind.getDouble("speed") + " m/s\n" +
                                        "Pressure: " + (int) main.getDouble("pressure") + " hPa\n" +
                                        "Humidity: " + (int) main.getDouble("humidity") + "%\n" +
                                        "Sunrise: " + getDateTime(sys.getDouble("sunrise") * 1000, "HH:mm") + "\n" +
                                        "Sunset: " + getDateTime(sys.getDouble("sunset") * 1000, "HH:mm"));
                            } catch (JSONException ignored) {}

                            dialogWindow.show();
                        });
                    }
                } catch (JSONException ignored) {}
            });
        });
        weatherHandler.postDelayed(runnable, 3600000);
    }

    public static String getDateTime(double unixTimeStamp, String pattern) {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        Date date = new Date();
        date.setTime((long) unixTimeStamp);
        return dateFormat.format(date);
    }

    private String getHTTPData(String urlString) {
        String stream = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            assert httpURLConnection != null;
            if (httpURLConnection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                stream = sb.toString();
                httpURLConnection.disconnect();
            }
        } catch (IOException ignored) {}
        return stream;
    }
}