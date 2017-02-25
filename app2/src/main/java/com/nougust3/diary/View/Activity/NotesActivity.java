package com.nougust3.diary.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nougust3.diary.R;
import com.nougust3.diary.Model.Database.DBHelper;
import com.nougust3.diary.Model.Note;
import com.nougust3.diary.View.Adapter.NoteAdapter;
import com.nougust3.diary.View.Dialog.OnCompleteListener;
import com.nougust3.diary.View.Dialog.SelectNotebookFragment;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends BaseActivity implements OnCompleteListener {

    private ListView listView;
    private Toolbar toolbar;

    private NoteAdapter adapter;
    private List<Note> list;

    private MenuItem doneSelection;
    private MenuItem moveItem;
    private MenuItem removeItem;

    private boolean groupEdit = false;

    public enum MODE {
        NORMAL_MODE, GROUP_EDIT
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_menu, menu);

        doneSelection = menu.findItem(R.id.app_bar_done_selection);
        moveItem = menu.findItem(R.id.app_bar_move);
        removeItem = menu.findItem(R.id.app_bar_remove);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (moveItem != null) {
            moveItem.setVisible(groupEdit);
        }
        if (removeItem != null) {
            removeItem.setVisible(groupEdit);
        }
        if (doneSelection != null) {
            doneSelection.setVisible(groupEdit);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.equals(removeItem)) {
            SparseBooleanArray selected = adapter.getSelectedId();
            for (int i = (selected.size() - 1); i >= 0; i--) {
                if (selected.valueAt(i)) {
                    Note note = adapter.getItem(selected.keyAt(i));
                    DBHelper.getInstance().removeNote(note.getCreation());
                }
            }
            loadNotes(getNotebookId());
            groupEdit = false;
            setMode(MODE.NORMAL_MODE);
        }
        if(item.equals(moveItem)) {
            SelectNotebookFragment selectNotebookFragment = new SelectNotebookFragment();
            selectNotebookFragment.show(getSupportFragmentManager(), "selectNotebookFragment");
        }
        if(item.equals(doneSelection)) {
            groupEdit = false;
            setMode(MODE.NORMAL_MODE);
        }

        return true;
    }
    private void setMode(MODE mode) {
        if(mode == MODE.NORMAL_MODE) {
            if(!groupEdit) {
                adapter.removeSelection();
                listView.clearChoices();
                for (int i = 0; i < listView.getCount(); i++) {
                    listView.setItemChecked(i, false);
                }
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                        listView.setItemsCanFocus(true);
                    }
                });
            }
        }
        if(mode == MODE.GROUP_EDIT) {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listView.setItemsCanFocus(false);
            groupEdit = true;
        }

        invalidateOptionsMenu();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setNoteId(0);
        setNotebookId(getIntent().getLongExtra("notebookId", 0L));

        toolbar.setTitle(DBHelper.getInstance().getNotebook(getNotebookId()).getName());

        listView = (ListView) findViewById(R.id.notesListView);
        list = new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(groupEdit) {
                    adapter.toggleSelection(i);
                    return;
                }
                Intent intent = new Intent(NotesActivity.this, EditorActivity.class);
                intent.putExtra("creation5", list.get(i).getCreation());
                intent.putExtra("notebook", getNotebookId());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                setMode(MODE.GROUP_EDIT);
                adapter.toggleSelection(i);
                listView.setItemChecked(i, true);
                return true;
            }
        });


        initNavigation();
        initFAB();
        loadNotes(getNotebookId());
        updateCounter();
        setMode(MODE.NORMAL_MODE);
    }

    public void loadNotes(long notebookId) {
        list = DBHelper.getInstance().getFromNotebook(notebookId);
        adapter = new NoteAdapter(this, list);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getNotebookId() != 0) {
            loadNotes(getNotebookId());
        }
    }

    @Override
    public void onSelect(long id) {
        SparseBooleanArray selected = adapter.getSelectedId();
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                Note note = adapter.getItem(selected.keyAt(i));
                note.setNotebook(id);
                DBHelper.getInstance().updateNote(note);
            }
        }
        loadNotes(getNotebookId());
        groupEdit = false;
        setMode(MODE.NORMAL_MODE);
    }

    @Override
    public void onComplete() {
        groupEdit = false;
        setMode(MODE.NORMAL_MODE);
    }

    @Override
    public void onRemoved() { }
}
