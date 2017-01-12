package com.nougust3.diary.ui;

import android.content.Intent;
import android.os.Bundle;

import com.nougust3.diary.Keep;
import com.nougust3.diary.R;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Content;
import com.nougust3.diary.models.Note;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParserActivity extends BaseActivity {

    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parser);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
             if ("text/plain".equals(type)) {
                 parseHtml(intent.getStringExtra(Intent.EXTRA_TEXT));
             }
        }
    }

    private void parseHtml(String url) {
        Keep.getApi().getData(url).enqueue(new Callback<Content>() {
            @Override
            public void onResponse(Call<Content> call, Response<Content> response) {
                Content content = response.body();
                note = new Note();

                note.setTitle(content.getTitle());
                note.setContent(content.getContent());

                saveNote();
            }
            @Override
            public void onFailure(Call<Content> call, Throwable t) {
                t.printStackTrace();
                showToast("Fail");
            }
        });
    }

    private void saveNote() {
        DBHelper db = new DBHelper(getContext());
        db.updateNote(note);
        finish();
    }
}
