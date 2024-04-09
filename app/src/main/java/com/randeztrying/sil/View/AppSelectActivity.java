package com.randeztrying.sil.View;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.randeztrying.sil.View.Adapters.ViewHolder;
import com.randeztrying.sil.Controller.Prefs;
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
        check(Prefs.getFolderApps(getApplicationContext(), "System"));
        check(Prefs.getFolderApps(getApplicationContext(), "Internet"));
        check(Prefs.getFolderApps(getApplicationContext(), "Social"));
        check(Prefs.getFolderApps(getApplicationContext(), "Life"));
        check(Prefs.getFolderApps(getApplicationContext(), "Other"));
        menuAppList.add(null);
        SelectAppsAdapter adapter = new SelectAppsAdapter(this, getApplicationContext(), menuAppList);
        recyclerView.setAdapter(adapter);
    }

    private void check(List<App> apps) {
        for (int i = 0; i < menuAppList.size(); i++) {
            for (int j = 0; j < apps.size(); j++) {
                if (menuAppList.get(i).getPackageName().equals(apps.get(j).getPackageName())) {
                    menuAppList.remove(i);
                    i = 0;
                    j = 0;
                }
            }
        }
    }

    public static class SelectAppsAdapter extends RecyclerView.Adapter<ViewHolder> {

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
            if (viewType == 1) return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.text_item, parent, false));
            else return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.confirm_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position != appList.size() - 1) {
                holder.text.setText(appList.get(position).getAppName());
                holder.check.setVisibility(View.VISIBLE);
                holder.check.setOnClickListener(v -> {
                    boolean isChecked = appList.get(position).isChecked();
                    if (isChecked) holder.check.setImageResource(R.drawable.circle_outlined);
                    else holder.check.setImageResource(R.drawable.circle);
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
    }
}