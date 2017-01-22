package com.nougust3.diary.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.nougust3.diary.R;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Note;
import com.nougust3.diary.models.adapters.NoteAdapter;
import com.nougust3.diary.utils.AnimateUtils;
import com.nougust3.diary.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    private EditText editFastNote;
    private DBHelper db;
    private ListAdapter adapter;
    private List<Note> notesList;
    private TextView listHeader;

    private MenuItem doneItem;
    private MenuItem moveItem;
    private MenuItem removeItem;
    private ListView notesListView;

    private boolean edit = false;

    public enum MODE {
        NORMAL_MODE, DONE_MODE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        init();
        initEditor();
        initToolbar();
        initNotesList();
        updateCounter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        doneItem = menu.findItem(R.id.app_bar_done);
        moveItem = menu.findItem(R.id.app_bar_move);
        removeItem = menu.findItem(R.id.app_bar_remove);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (doneItem != null) {
            AnimateUtils.safeAnimate(findViewById(R.id.app_bar_done), 300,
                    edit ? Techniques.FlipInX : Techniques.FlipOutX);
        }
        if (moveItem != null) {
            AnimateUtils.safeAnimate(findViewById(R.id.app_bar_done), 300,
                    edit ? Techniques.FlipInX : Techniques.FlipOutX);
        }
        if (removeItem != null) {
            AnimateUtils.safeAnimate(findViewById(R.id.app_bar_done), 300,
                    edit ? Techniques.FlipInX : Techniques.FlipOutX);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.equals(doneItem)) {
            saveNote();
            setMode(MODE.NORMAL_MODE);
        }

        return true;
    }

    private void init() {
        initNavigation();
        initFAB();
    }

    private void initToolbar() {
        setMode(MODE.NORMAL_MODE);
    }

    private void initEditor() {
        editFastNote = (EditText) findViewById(R.id.editFastNote);

        editFastNote.clearFocus();

        editFastNote.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setMode(MODE.DONE_MODE);
                editFastNote.requestFocusFromTouch();

                return false;
            }
        });

        editFastNote.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK &&
                        editFastNote.getText().toString().equals("") ) {
                    setMode(MODE.NORMAL_MODE);
                }

                return false;
            }
        });
    }

    private void initNotesList() {
        notesListView = (ListView) findViewById(R.id.notesListView);
        db = new DBHelper(getApplicationContext());
        notesList = new ArrayList<>();
        adapter = new NoteAdapter(HomeActivity.this, notesList);
        listHeader = (TextView)findViewById(R.id.listHeader);

        notesListView.setAdapter(adapter);
        notesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        notesListView.setItemsCanFocus(false);

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent = new Intent(HomeActivity.this, EditorActivity.class);
               intent.putExtra("creation", notesList.get(position).getCreation());
               startActivityForResult(intent, 1);
            }
        });

        notesListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) { }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount,
                                 int totalItemCount) {
                if(totalItemCount != 0) {
                    if(notesList.get(firstVisibleItem).isTask()) {
                        listHeader.setText("Задачи на сегодня");
                    }
                    else {
                        listHeader.setText(DateUtils.format(notesList.get(firstVisibleItem)
                                .getModification()));
                    }
                }
            }
        });

        notesListView.setScrollingCacheEnabled(true);

        getNotes();
    }

    private void setMode(MODE mode) {
        if(mode == MODE.DONE_MODE) {
            editFastNote.setGravity(Gravity.START| Gravity.TOP);
            editFastNote.setHint("");
            editFastNote.requestFocus();
            edit = true;
        }
        if(mode == MODE.NORMAL_MODE) {
            editFastNote.setGravity(Gravity.CENTER);
            editFastNote.setHint(R.string.edit_fast_note_hint);
            edit = false;
            editFastNote.setText("");
            editFastNote.clearFocus();
            //KeyboardUtils.hide(drawerLayout, getApplicationContext());
        }

        invalidateOptionsMenu();
    }

    private void saveNote() {
        Note note = new Note();

        if(editFastNote.getText().toString().equals("")) {
            showToast("Can't save empty note");
            return;
        }

        note.setCategory("Inbox");
        note.setTitle("");
        note.setContent(editFastNote.getText().toString());
        note.setIsTask(0);
        note.setArchive(0);
        note.setNotebook(0);

        db.updateNote(note);

        Toast.makeText(getApplicationContext(), "Note saved", Toast.LENGTH_SHORT).show();

        getNotes();
    }

    private void getNotes() {
        notesList = db.getAllNotes();
        adapter = new NoteAdapter(HomeActivity.this, notesList);
        notesListView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 1) getNotes();
    }
}