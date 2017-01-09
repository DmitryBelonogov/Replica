package com.nougust3.diary;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Note;
import com.nougust3.diary.models.adapters.NoteAdapter;
import com.nougust3.diary.utils.AnimateUtils;
import com.nougust3.diary.utils.DateUtils;
import com.nougust3.diary.utils.KeyboardUtils;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends BaseActivity {

    private EditText editFastNote;
    private DBHelper db;
    private ListAdapter adapter;
    private List<Note> notesList;
    private DrawerLayout drawerLayout;
    private TextView listHeader;

    private Toolbar toolbar;
    private MenuItem doneItem;
    private ListView notesListView;

    private boolean edit = false;

    public enum MODE {
        NORMAL_MODE, DONE_MODE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home2);

        initNavigation();
        initEditor();
        initToolbar();
        initNotesList();

        setMode(MODE.NORMAL_MODE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        doneItem = menu.findItem(R.id.app_bar_done);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (doneItem != null) {
           // AnimateUtils.safeAnimate((View) doneItem, 300,
            //        edit ? Techniques.FlipInX : Techniques.FlipOutX);
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

    private void initNavigation() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();

                if(item.getItemId() == R.id.actionTrashItem) {
                    Intent intent = new Intent(HomeActivity.this, TrashActivity.class);
                    startActivityForResult(intent, 1);
                }

                return false;
            }
        });

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);

        setSupportActionBar(toolbar);
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

        editFastNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if(editFastNote.getText().toString().equals("")) {
                        setMode(MODE.NORMAL_MODE);
                    }
                    editFastNote.setGravity(Gravity.CENTER);
                    editFastNote.setHint(R.string.edit_fast_note_hint);
                }
                else {
                    editFastNote.setGravity(Gravity.START| Gravity.TOP);
                    editFastNote.setHint("");
                }
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

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeActivity.this, EditorView.class);

                intent.putExtra("creation", notesList.get(position).getCreation());

                startActivityForResult(intent, 1);
            }
        });

        notesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent,
                                           View view, int position, long id) {
                notesList.get(position).setArchive(1);
                getNotes();
                return false;
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
            toolbar.setTitle("Save");
            edit = true;
        }
        if(mode == MODE.NORMAL_MODE) {
            toolbar.setTitle("Keep");
            edit = false;
            editFastNote.setText("");
            editFastNote.clearFocus();
            KeyboardUtils.hide(drawerLayout, getApplicationContext());
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
        if(resultCode == 1) {
            getNotes();
        }
    }
}
