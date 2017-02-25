package com.nougust3.diary.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.nougust3.diary.R;
import com.nougust3.diary.View.EditorView;
import com.nougust3.diary.View.HomeView;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Note;
import com.nougust3.diary.models.adapters.NoteAdapter;
import com.nougust3.diary.ui.dialogs.SelectNotebookFragment;
import com.nougust3.diary.Utils.ContentUtils;
import com.nougust3.diary.Utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import com.nougust3.diary.Presenter.EditorPresenter;
import com.nougust3.diary.Presenter.HomePresenter;


public class HomeActivity extends MvpAppCompatActivity implements HomeView {

    @InjectPresenter
    HomePresenter homePresenter;

    private EditText editFastNote;
    private ImageButton editFastNoteDone;
    private DBHelper db;
    private NoteAdapter adapter;
    private List<Note> notesList;
    private TextView listHeader;

    private MenuItem doneSelection;
    private MenuItem moveItem;
    private MenuItem removeItem;
    private ListView notesListView;

    private boolean edit = false;
    private boolean groupEdit = false;


    private FloatingActionButton fab;
    private TextView inboxCount;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public enum MODE {
        NORMAL_MODE, GROUP_EDIT, DONE_MODE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("All notes");
        setSupportActionBar(toolbar);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, EditorActivity.class);
                intent.putExtra("creation5", 0);
                intent.putExtra("notebook", 0);
                startActivityForResult(intent, 1);
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigation);

        inboxCount = (TextView) navigationView.getMenu().getItem(0).getActionView();
        inboxCount.setGravity(Gravity.CENTER_VERTICAL);
        inboxCount.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.navigation_text));

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if(item.getItemId() == R.id.actionInboxItem) {
                            Intent intent = new Intent(HomeActivity.this, NotesActivity.class);
                            intent.putExtra("notebookId", 0L);
                            startActivityForResult(intent, 1);
                        }
                        else if(item.getItemId() == R.id.actionTrashItem) {
                            Intent intent = new Intent(HomeActivity.this, TrashActivity.class);
                            startActivityForResult(intent, 1);
                        }
                        else if(item.getItemId() == R.id.actionNotebooksItem) {
                            Intent intent = new Intent(HomeActivity.this, NotebooksActivity.class);
                            startActivityForResult(intent, 1);
                        }
                        else if(item.getItemId() == R.id.actionSettingsItem) {
                            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                            startActivity(intent);
                        }

                        return false;
                    }
                });

        initEditor();
        initNotesList();
        initToolbar();



        homePresenter.onUpdateNotes();
    }

    @Override
    public void updateNotesList(ArrayList<Note> notesList) {
        adapter = new NoteAdapter(HomeActivity.this, notesList);
        notesListView.setAdapter(adapter);
    }

    @Override
    public void updateHeader(String text) {
        listHeader.setText(text);
    }

    @Override
    public void startSelection() {
        notesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        notesListView.setItemsCanFocus(false);
    }

    @Override
    public void selectItem(int position) {
        adapter.toggleSelection(position);
        notesListView.setItemChecked(position, true);
    }

    @Override
    public void doneSelection() {

    }

    @Override
    public void openEditor() {
        editFastNoteDone.setVisibility(View.VISIBLE);
        editFastNoteDone.requestFocus();
        editFastNote.requestFocus();
    }

    @Override
    public void closeEditor() {
        editFastNote.setText("");
        editFastNoteDone.setVisibility(View.GONE);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void updateCounter(int count) {
        inboxCount.setText(String.valueOf(count));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

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
            getNotes();
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

    private void initToolbar() {
        setMode(MODE.NORMAL_MODE);
    }

    private void initEditor() {
        editFastNote = (EditText) findViewById(R.id.editFastNote);
        editFastNoteDone = (ImageButton) findViewById(R.id.editFastNoteDone);


        editFastNoteDone.setVisibility(View.GONE);


        editFastNote.clearFocus();

        editFastNote.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                homePresenter.onEditNote();
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

        editFastNoteDone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                homePresenter.onSaveNote(editFastNote.getText().toString());
                return true;
            }
        });
    }

    private void initNotesList() {
        notesListView = (ListView) findViewById(R.id.notesListView);
        notesListView.setNestedScrollingEnabled(true);
        db = new DBHelper(getApplicationContext());
        notesList = new ArrayList<>();
        adapter = new NoteAdapter(HomeActivity.this, notesList);
        listHeader = (TextView)findViewById(R.id.listHeader);

        notesListView.setAdapter(adapter);

       notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(groupEdit) {
                    adapter.toggleSelection(position);
                    return;
                }

                Log.i("KEEP", ContentUtils.htmlToText(notesList.get(position).getContent()));
               Intent intent = new Intent(HomeActivity.this, EditorActivity.class);
               intent.putExtra("creation5", notesList.get(position).getCreation());
               startActivityForResult(intent, 1);
            }
        });

        notesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                homePresenter.onStartSelection(i);
                return true;
            }
        });

        notesListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) { }

            @Override
            public void onScroll(AbsListView view, int i, int visCount, int totCount) {
                homePresenter.onScrollList(i);
            }
        });


        getNotes();
    }

    private void setMode(MODE mode) {
        if(mode == MODE.DONE_MODE) {
            if(!groupEdit) {
                edit = true;
            }
        }
        if(mode == MODE.NORMAL_MODE) {
            if(!groupEdit) {
                adapter.removeSelection();
                notesListView.clearChoices();
                for (int i = 0; i < notesListView.getCount(); i++) {
                    notesListView.setItemChecked(i, false);
                }
                notesListView.post(new Runnable() {
                    @Override
                    public void run() {
                        notesListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                        notesListView.setItemsCanFocus(true);
                    }
                });

                edit = false;
            }
        }
        if(mode == MODE.GROUP_EDIT) {
            groupEdit = true;
            edit = false;
        }

        invalidateOptionsMenu();
    }

    private void getNotes() {
        notesList = db.getAllNotes();
        adapter = new NoteAdapter(HomeActivity.this, notesList);
        notesListView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(notesListView != null) {
            getNotes();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 1) getNotes();
    }

    /*@Override
    public void onSelect(long id) {
        SparseBooleanArray selected = adapter.getSelectedId();
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                Note note = adapter.getItem(selected.keyAt(i));
                note.setNotebook(id);
                DBHelper.getInstance().updateNote(note);
            }
        }
        getNotes();
        groupEdit = false;
        setMode(MODE.NORMAL_MODE);
    }*/

    //@Override
   // public void onComplete() {
    //    groupEdit = false;
    //    setMode(MODE.NORMAL_MODE);
   // }

    //@Override
    //public void onRemoved() { }
}