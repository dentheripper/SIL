package com.randeztrying.sil.AppActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.randeztrying.sil.Helpers.Prefs;
import com.randeztrying.sil.Models.App;
import com.randeztrying.sil.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppSelectActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Intent intent;
    public List<App> menuAppList;

    private static String folderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_layout);

        PackageManager packageManager = getPackageManager();
        intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        folderName = getIntent().getStringExtra("folder");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        updateAppList(packageManager);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private List<App> getDeviceAppsList(PackageManager packageManager) {
        List<App> checkAppList = new ArrayList<>();

        @SuppressLint("QueryPermissionsNeeded")
        List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo ri : availableActivities) {
            App item = new App();
            item.setPackageName(ri.activityInfo.packageName);
            item.setAppName(ri.loadLabel(packageManager).toString());
            checkAppList.add(item);
        }

        Collections.sort(checkAppList);

        return checkAppList;
    }

    private void updateAppList(PackageManager packageManager) {
        menuAppList = getDeviceAppsList(packageManager);

        List<App> systemApps = Prefs.getFolderApps(getApplicationContext(), "System");
        List<App> internetApps = Prefs.getFolderApps(getApplicationContext(), "Internet");
        List<App> socialApps = Prefs.getFolderApps(getApplicationContext(), "Social");
        List<App> gamesApps = Prefs.getFolderApps(getApplicationContext(), "Games");
        List<App> otherApps = Prefs.getFolderApps(getApplicationContext(), "Other");
        if (systemApps != null) {
            for (int i = 0; i < menuAppList.size(); i++) {
                for (int j = 0; j < systemApps.size(); j++) {
                    if (menuAppList.get(i).getPackageName().equals(systemApps.get(j).getPackageName())) {
                        menuAppList.remove(i);
                        i = 0;
                        j = 0;
                    }
                }
            }
        }
        if (internetApps != null) {
            for (int i = 0; i < menuAppList.size(); i++) {
                for (int j = 0; j < internetApps.size(); j++) {
                    if (menuAppList.get(i).getPackageName().equals(internetApps.get(j).getPackageName())) {
                        menuAppList.remove(i);
                        i = 0;
                        j = 0;
                    }
                }
            }
        }
        if (socialApps != null) {
            for (int i = 0; i < menuAppList.size(); i++) {
                for (int j = 0; j < socialApps.size(); j++) {
                    if (menuAppList.get(i).getPackageName().equals(socialApps.get(j).getPackageName())) {
                        menuAppList.remove(i);
                        i = 0;
                        j = 0;
                    }
                }
            }
        }
        if (gamesApps != null) {
            for (int i = 0; i < menuAppList.size(); i++) {
                for (int j = 0; j < gamesApps.size(); j++) {
                    if (menuAppList.get(i).getPackageName().equals(gamesApps.get(j).getPackageName())) {
                        menuAppList.remove(i);
                        i = 0;
                        j = 0;
                    }
                }
            }
        }
        if (otherApps != null) {
            for (int i = 0; i < menuAppList.size(); i++) {
                for (int j = 0; j < otherApps.size(); j++) {
                    if (menuAppList.get(i).getPackageName().equals(otherApps.get(j).getPackageName())) {
                        menuAppList.remove(i);
                        i = 0;
                        j = 0;
                    }
                }
            }
        }
        menuAppList.add(null);
        SelectAppsAdapter adapter = new SelectAppsAdapter(this, getApplicationContext(), menuAppList);
        recyclerView.setAdapter(adapter);
    }

    public static class SelectAppsAdapter extends RecyclerView.Adapter<SelectAppsAdapter.ViewHolder> {

        private final Activity activity;
        private final Context context;
        private final List<App> appList;

        public SelectAppsAdapter(Activity activity, Context context, List<App> appList) {
            this.activity = activity;
            this.context = context;
            this.appList = appList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.menu_app, parent, false));
            else return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.confirm_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position != appList.size() - 1) {
                holder.appName.setText(appList.get(position).getAppName());

                holder.appCheck.setVisibility(View.VISIBLE);
                holder.appCheck.setOnClickListener(v -> {
                    boolean isChecked = appList.get(position).isChecked();
                    if (isChecked) holder.appCheck.setImageResource(R.drawable.circle_outlined);
                    else holder.appCheck.setImageResource(R.drawable.circle);
                    appList.get(position).setChecked(!isChecked);
                });
            } else {
                holder.itemView.setOnClickListener(v -> {
                    List<App> checkedApps = Prefs.getFolderApps(context, folderName);
                    for (int i = 0; i < appList.size() - 1; i++) {
                        if (appList.get(i).isChecked()) {
                            checkedApps.add(appList.get(i));
                        }
                    }
                    Prefs.writeFolderApps(context, checkedApps, folderName);
                    activity.finish();
                });
            }
        }

        @Override
        public int getItemCount() {
            return appList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (appList.get(position) == null) return 0;
            else return 1;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView appName;
            public ImageView appCheck;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                appName = itemView.findViewById(R.id.app_name);
                appCheck = itemView.findViewById(R.id.app_check);
            }
        }
    }
}