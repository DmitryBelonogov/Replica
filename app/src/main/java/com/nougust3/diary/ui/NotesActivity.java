package com.nougust3.diary.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.nougust3.diary.R;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Note;
import com.nougust3.diary.models.adapters.NoteAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends BaseActivity {

    private ListView listView;

    private List<Note> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        listView = (ListView) findViewById(R.id.notesListView);
        list = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(NotesActivity.this, EditorActivity.class);
                intent.putExtra("creation", list.get(i).getCreation());
                startActivity(intent);
            }
        });

        long id = getIntent().getLongExtra("notebookId", 0L);

        initNavigation();
        initFAB();
        loadNotes(id);
        updateCounter();
    }

    public void loadNotes(long notebookId) {
        list = DBHelper.getInstance().getFromNotebook(notebookId);
        ListAdapter adapter = new NoteAdapter(this, list);
        listView.setAdapter(adapter);
    }
}
