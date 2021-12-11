package com.randeztrying.sil.AppActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.randeztrying.sil.Helpers.Prefs;
import com.randeztrying.sil.Helpers.StaticHelper;
import com.randeztrying.sil.Models.Note;
import com.randeztrying.sil.R;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    private List<Note> notes;
    private RecyclerView recyclerView;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        FloatingActionButton addNote = findViewById(R.id.add_note);
        recyclerView = findViewById(R.id.rec_notes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        notes = new ArrayList<>();
        read();

        addNote.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
            intent.putExtra("what", "null");
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        final Runnable runnable = this::read;
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    private void read() {
        final Runnable runnable = this::read;

        notes = Prefs.getNotes(getApplicationContext());
        NotesAdapter adapter = new NotesAdapter(getApplicationContext(), notes);
        recyclerView.setAdapter(adapter);

        handler.postDelayed(runnable, 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(0, 0);
        }
        return super.onKeyDown(keyCode, event);
    }

    private static class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

        private final Context context;
        private final List<Note> notes;

        public NotesAdapter(Context context, List<Note> notes) {
            this.context = context;
            this.notes = notes;
        }

        @NonNull
        @Override
        public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.note_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
            Note note = notes.get(position);

            holder.name.setText(note.getName());
            holder.date.setText(StaticHelper.getCoolerTime(note.getDate(), false));
            holder.text.setText(note.getText());

            holder.itemView.setOnClickListener(v -> openNote(note));
            holder.itemView.setOnLongClickListener(v -> {
                removeNote(v, note);
                return true;
            });
        }

        private void removeNote(View v, Note note) {
            Dialog dialogWindow = new Dialog(v.getRootView().getContext());

            dialogWindow.setContentView(R.layout.alert_remove_note);
            dialogWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Button remove = dialogWindow.findViewById(R.id.remove_note);
            remove.setText(R.string.remove_note);

            dialogWindow.show();

            remove.setOnClickListener(v1 -> {
                List<Note> notes = Prefs.getNotes(context);

                for (int i = 0; i < notes.size(); i++) {
                    if (notes.get(i).getName().equals(note.getName())
                            && notes.get(i).getDate().equals(note.getDate())
                            && notes.get(i).getText().equals(note.getText())) {
                        notes.remove(notes.get(i));
                        break;
                    }
                }

                Prefs.saveNotes(context, notes);
                dialogWindow.dismiss();
            });
        }

        private void openNote(Note note) {
            Intent intent = new Intent(context, CreateNoteActivity.class);
            intent.putExtra("what", note.getDate());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent, ActivityOptions.makeCustomAnimation(context, 0, 0).toBundle());
        }

        @Override
        public int getItemCount() {
            return notes.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView name;
            public TextView date;
            public TextView text;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.note_name);
                date = itemView.findViewById(R.id.note_date);
                text = itemView.findViewById(R.id.note_text);
            }
        }
    }
}