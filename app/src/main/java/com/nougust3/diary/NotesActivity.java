package com.nougust3.diary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Note;
import com.nougust3.diary.models.NotebookModel;
import com.nougust3.diary.models.adapters.NoteAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

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
                Intent intent = new Intent(NotesActivity.this, EditorView.class);
                intent.putExtra("creation", list.get(i).getCreation());
                startActivity(intent);
            }
        });

        long id = getIntent().getLongExtra("notebookId", 0);

        loadNotes(id);
    }

    public void loadNotes(long notebookId) {
        list = DBHelper.getInstance().getFromNotebook(notebookId);
        ListAdapter adapter = new NoteAdapter(this, list);
        listView.setAdapter(adapter);
    }
}
