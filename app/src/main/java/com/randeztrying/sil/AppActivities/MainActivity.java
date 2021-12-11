package com.randeztrying.sil.AppActivities;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randeztrying.sil.Adapters.AppsAdapter;
import com.randeztrying.sil.Helpers.Prefs;
import com.randeztrying.sil.Helpers.StaticHelper;
import com.randeztrying.sil.Helpers.SwipeActivity;
import com.randeztrying.sil.Models.App;
import com.randeztrying.sil.Models.Weather.Common;
import com.randeztrying.sil.Models.Weather.Helper;
import com.randeztrying.sil.Models.Weather.Models.OpenWeatherMap;
import com.randeztrying.sil.R;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends SwipeActivity {

    private OpenWeatherMap openWeatherMap;

    private final Handler timeHandler = new Handler();
    private final Handler weatherHandler = new Handler();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desktop);
        isPermissionGranted(this);

        RelativeLayout timeDate = findViewById(R.id.time_date);
        TextView time = findViewById(R.id.time);
        TextView date = findViewById(R.id.date);

        TextView systemFolder = findViewById(R.id.system_folder);
        TextView internetFolder = findViewById(R.id.internet_folder);
        TextView socialFolder = findViewById(R.id.social_folder);
        TextView gamesFolder = findViewById(R.id.games_folder);
        TextView otherFolder = findViewById(R.id.other_folder);

        ImageView call = findViewById(R.id.call);
        ImageView settings = findViewById(R.id.settings);

        updateTime(time, date);
        getWeather();

        timeDate.setOnClickListener(v -> {
            Dialog dialogWindow = new Dialog(v.getRootView().getContext());
            dialogWindow.setContentView(R.layout.layout_calendar);
            dialogWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogWindow.show();
        });
        systemFolder.setOnClickListener(v -> openFolder(v, "System"));
        internetFolder.setOnClickListener(v -> openFolder(v, "Internet"));
        socialFolder.setOnClickListener(v -> openFolder(v, "Social"));
        gamesFolder.setOnClickListener(v -> openFolder(v, "Games"));
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
        dialogWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView folderName = dialogWindow.findViewById(R.id.folder_name);
        ImageView addApp = dialogWindow.findViewById(R.id.add_app);
        ImageView clearFolder = dialogWindow.findViewById(R.id.clear_folder);
        RecyclerView recyclerView = dialogWindow.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        List<App> appList = Prefs.getFolderApps(getApplicationContext(), folder);
        if (appList != null) {
            recyclerView.setAdapter(
                    new AppsAdapter(getApplicationContext(), appList, dialogWindow, getPackageManager(), folder)
            );
        }
        folderName.setText(folder);

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
                recyclerView.setAdapter(
                        new AppsAdapter(getApplicationContext(), appList, dialogWindow, getPackageManager(), folder)
                );
            }
        });
    }

    @Override
    protected void onSwipeRight() {
        startActivity(new Intent(MainActivity.this, NotesActivity.class),
                ActivityOptions.makeCustomAnimation(getApplicationContext(), 0, 0).toBundle());
    }

    @Override
    protected void onSwipeLeft() {}

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
    private void updateTime(TextView time, TextView date) {
        Runnable runnable = () -> updateTime(time, date);

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String dow;
        switch (dayOfWeek) {
            case 1:
                dow = "Sun";
                break;
            case 2:
                dow = "Mon";
                break;
            case 3:
                dow = "Tue";
                break;
            case 4:
                dow = "Wed";
                break;
            case 5:
                dow = "Thu";
                break;
            case 6:
                dow = "Fri";
                break;
            case 7:
                dow = "Sat";
                break;
            default:
                dow = "_";
                break;
        }

        time.setText(StaticHelper.getCoolerTime(String.valueOf(System.currentTimeMillis()), true));
        if (openWeatherMap == null) date.setText(dow + "," + StaticHelper.getCoolerTime(String.valueOf(System.currentTimeMillis()), false));
        else date.setText(dow + ", " + StaticHelper.getCoolerTime(String.valueOf(System.currentTimeMillis()), false) + ", " + openWeatherMap.getCity().getName());

        timeHandler.postDelayed(runnable, 10000);
    }

    private void getWeather() {
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

    private void updateWeather(double lat, double lng) {
        Runnable runnable = () -> updateWeather(lat, lng);
        new GetWeather().execute(Common.apiRequest(String.valueOf(lat), String.valueOf(lng)));
        weatherHandler.postDelayed(runnable, 3600000);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetWeather extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String stream;
            String urlString = strings[0];

            Helper helper = new Helper();
            stream = helper.getHTTPData(urlString);
            return stream;
        }

        @SuppressLint({"DefaultLocale", "CheckResult", "SetTextI18n"})
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>() {
            }.getType();
            openWeatherMap = gson.fromJson(s, mType);

            if (openWeatherMap != null) {
                TextView weather = findViewById(R.id.weather);
                weather.setText(String.format("%.0f°", openWeatherMap.getList().get(0).getMain().getTemp()) + ", " +
                        openWeatherMap.getList().get(0).getWeather().get(0).getDescription().substring(0, 1).toUpperCase()
                        + openWeatherMap.getList().get(0).getWeather().get(0).getDescription().substring(1));
            }
        }
    }
}