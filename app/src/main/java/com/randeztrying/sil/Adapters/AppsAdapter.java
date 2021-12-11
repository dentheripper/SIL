package com.randeztrying.sil.Adapters;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randeztrying.sil.Helpers.Prefs;
import com.randeztrying.sil.Models.App;
import com.randeztrying.sil.R;

import java.util.Collections;
import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    private final Context context;
    private final List<App> appList;
    private final Dialog dialogWindow;
    private final String folderName;
    private final PackageManager packageManager;

    public AppsAdapter(Context context, List<App> appList, Dialog dialogWindow, PackageManager packageManager, String folderName) {
        this.context = context;
        this.appList = appList;
        this.dialogWindow = dialogWindow;
        this.packageManager = packageManager;
        this.folderName = folderName;

        Collections.sort(appList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.menu_app, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.appName.setText(appList.get(position).getAppName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            intent = packageManager.getLaunchIntentForPackage(appList.get(position).getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent, ActivityOptions.makeCustomAnimation(context, 0, 0).toBundle());
            dialogWindow.cancel();
        });
        holder.itemView.setOnLongClickListener(v -> {
            Dialog dialogWindow = new Dialog(v.getRootView().getContext());

            dialogWindow.setContentView(R.layout.alert_app_action);
            dialogWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView appName = dialogWindow.findViewById(R.id.app_name);
            Button rmFromFolder = dialogWindow.findViewById(R.id.remove_from_folder);
            Button uninstall = dialogWindow.findViewById(R.id.remove_app);

            appName.setText(appList.get(position).getAppName());

            dialogWindow.show();

            uninstall.setOnClickListener(v1 -> {
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + appList.get(position).getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                dialogWindow.cancel();

                List<App> checkAppList = Prefs.getFolderApps(context, folderName);
                if (checkAppList != null) {
                    for (int i = 0; i < checkAppList.size(); i++) {
                        if (checkAppList.get(i).getPackageName().equals(appList.get(position).getPackageName())) {
                            checkAppList.remove(i);
                            break;
                        }
                    }
                }
                Prefs.writeFolderApps(context, checkAppList, folderName);
            });
            rmFromFolder.setOnClickListener(v1 -> {
                List<App> checkAppList = Prefs.getFolderApps(context, folderName);
                int pos = 0;
                if (checkAppList != null) {
                    for (int i = 0; i < checkAppList.size(); i++) {
                        if (checkAppList.get(i).getPackageName().equals(appList.get(position).getPackageName())) {
                            checkAppList.remove(i);
                            pos = i;
                            break;
                        }
                    }
                }
                Prefs.writeFolderApps(context, checkAppList, folderName);
                this.notifyItemRemoved(pos);
                dialogWindow.cancel();
            });
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView appName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.app_name);
        }
    }
}
