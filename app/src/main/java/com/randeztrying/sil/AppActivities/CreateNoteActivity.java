package com.randeztrying.sil.AppActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.randeztrying.sil.Helpers.Prefs;
import com.randeztrying.sil.Models.Note;
import com.randeztrying.sil.R;

import java.util.ArrayList;
import java.util.List;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText name;
    private EditText text;

    private List<Note> notes;

    private String what;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        Intent intent = getIntent();
        what = intent.getStringExtra("what");

        ImageButton back = findViewById(R.id.back_cn);
        name = findViewById(R.id.cn_name);
        text = findViewById(R.id.cn_text);

        notes = Prefs.getNotes(getApplicationContext());

        if (!what.equals("null")) {
            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).getDate().equals(what)) {
                    name.setText(notes.get(i).getName());
                    text.setText(notes.get(i).getText());
                }
            }
        }

        back.setOnClickListener(v -> {
            if (text.getText().toString().length() == 0) {
                finish();
                overridePendingTransition(0, 0);
            } else if (text.getText().toString().length() > 3 && text.getText().toString().length() != 0) {
                if (notes == null) {
                    notes = new ArrayList<>();
                }
                write();

                Prefs.saveNotes(getApplicationContext(), notes);

                finish();
                overridePendingTransition(0, 0);
            } else
                Toast.makeText(getApplicationContext(), "Text must be more than 3 symbols", Toast.LENGTH_SHORT).show();
        });
    }

    private void write() {
        if (what.equals("null")) {
            notes.add(new Note(
                    name.getText().toString(),
                    text.getText().toString(),
                    String.valueOf(System.currentTimeMillis())
            ));
        } else {
            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).getDate().equals(what)) {
                    notes.get(i).setName(name.getText().toString());
                    notes.get(i).setText(text.getText().toString());
                    notes.get(i).setDate(String.valueOf(System.currentTimeMillis()));
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (text.getText().toString().length() == 0) {
                finish();
            } else if (text.getText().toString().length() > 3 && text.getText().toString().length() != 0) {
                if (notes == null) {
                    notes = new ArrayList<>();
                }
                write();

                Prefs.saveNotes(getApplicationContext(), notes);

                finish();
                overridePendingTransition(0, 0);
            } else
                Toast.makeText(getApplicationContext(), "Text must be more than 3 symbols", Toast.LENGTH_SHORT).show();
        }
        return super.onKeyDown(keyCode, event);
    }
}