package com.randeztrying.sil.View.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.randeztrying.sil.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView text;
    public ImageView check;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        text = itemView.findViewById(R.id.app_name);
        check = itemView.findViewById(R.id.app_check);
    }
}