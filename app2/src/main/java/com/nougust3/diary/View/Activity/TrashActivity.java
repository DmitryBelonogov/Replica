package com.nougust3.diary.View.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.nougust3.diary.R;
import com.nougust3.diary.Model.Database.DBHelper;
import com.nougust3.diary.Model.Note;
import com.nougust3.diary.View.Adapter.NoteAdapter;

import java.util.List;

public class TrashActivity extends BaseActivity {

    private ListView listView;
    private ListAdapter adapter;
    private List<Note> notesList;

    private MenuItem removeItem;
    private MenuItem repairItem;

    private DBHelper db;

    private int selectedItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        db = new DBHelper(getApplicationContext());

        initNavigation();
        initList();
        updateCounter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trash_menu, menu);

        removeItem = menu.findItem(R.id.removeItem);
        repairItem = menu.findItem(R.id.repairItem);

        if (removeItem != null) {

        }

        if (repairItem != null) {

        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (removeItem != null) {

        }

        if (repairItem != null) {

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.equals(removeItem)) {
            remove(notesList.get(selectedItem).getCreation());
        }

        return true;
    }

    private void initList() {
        listView = (ListView) findViewById(R.id.listView);

        notesList = db.getRemovedNotes();
        adapter = new NoteAdapter(TrashActivity.this, notesList);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = position;
                repair(notesList.get(position).getCreation());
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = position;
                remove(notesList.get(position).getCreation());
            }
        });
    }

    private void remove(long id) {
        db.remove(id);
        getNotes();
    }

    private void repair(long id) {
        Note note = db.getNote(id);
        note.setArchive(0);
        db.updateNote(note);
        getNotes();
    }

    private void getNotes() {
        notesList = db.getRemovedNotes();
        adapter = new NoteAdapter(TrashActivity.this, notesList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        setResult(1);
        finish();
    }
}
