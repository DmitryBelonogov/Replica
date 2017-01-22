package com.nougust3.diary.ui;

import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.fiberlink.maas360.android.richtexteditor.RichEditText;
import com.fiberlink.maas360.android.richtexteditor.RichTextActions;
import com.nougust3.diary.R;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Note;
import com.nougust3.diary.models.Notebook;
import com.nougust3.diary.ui.dialogs.NewNotebookFragment;
import com.nougust3.diary.ui.dialogs.OnCompleteListener;
import com.nougust3.diary.ui.dialogs.RemoveNoteFragment;
import com.nougust3.diary.utils.DateUtils;
import com.nougust3.diary.utils.KeyboardUtils;

import java.util.ArrayList;
import java.util.List;

public class EditorActivity extends BaseActivity implements OnCompleteListener {

    private DrawerLayout drawerLayout;

    private EditText titleView;
    private RichEditText contentView;
    private Spinner spinner;

    private MenuItem doneItem;
    private MenuItem undoItem;
    private MenuItem redoItem;
    private MenuItem attachItem;
    private MenuItem removeItem;
    private MenuItem editItem;

    private List<String> notebooks;

    private Note note;

    private NewNotebookFragment newNotebookDialog;
    private RemoveNoteFragment removeNoteFragment;

    private enum MODE {
        VIEW_MODE, EDIT_MODE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editor);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        newNotebookDialog = new NewNotebookFragment();
        removeNoteFragment = new RemoveNoteFragment();

        initToolbar();
        initContent();

        loadNote();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);

        doneItem = menu.findItem(R.id.app_bar_done);
        undoItem = menu.findItem(R.id.app_bar_undo);
        redoItem = menu.findItem(R.id.app_bar_redo);
        attachItem = menu.findItem(R.id.app_bar_attach);
        removeItem = menu.findItem(R.id.app_bar_remove);
        editItem = menu.findItem(R.id.app_bar_edit);

        if(getIntent().getLongExtra("creation", 0L) == 0L) {
            setMode(MODE.EDIT_MODE);
        }
        else {
            setMode(MODE.VIEW_MODE);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.equals(doneItem)) {
            setMode(MODE.VIEW_MODE);
            saveNote();
        }
        else if(item.equals(removeItem)) {
            removeNoteFragment.setNote(note.getCreation());
            removeNoteFragment.show(getSupportFragmentManager(), "removeNoteFragment");
        }
        else if(item.equals(editItem)) {
            setMode(MODE.EDIT_MODE);
        }
        else if(item.equals(undoItem)) {
            contentView.getmEditor().undo();
        }
        else if(item.equals(redoItem)) {
            contentView.getmEditor().redo();
        }

        return true;
    }

    private void initToolbar() {
        titleView = (EditText) findViewById(R.id.titleView);

        initNotebooksSpinner();
    }

    private void initNotebooksSpinner() {
        spinner = (Spinner) findViewById(R.id.spinner);

        notebooks = new ArrayList<>();
        notebooks.add("Inbox");
        List<Notebook> list = DBHelper.getInstance().getAllNotebooks();

        for (Notebook notebook : list) {
            notebooks.add(notebook.getName());
        }

        notebooks.add("New notebook");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.editor_spinner_item, notebooks);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == spinner.getCount() - 1) {
                    newNotebookDialog.show(getSupportFragmentManager(), "newNotebookDialog");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });
    }

    private void initContent() {
        contentView = (RichEditText) findViewById(R.id.contentView);
        RichTextActions richTextActions = (RichTextActions) findViewById(R.id.rich_text_actions);
        contentView.setRichTextActionsView(richTextActions);
    }

    private void loadNote() {
        DBHelper db = new DBHelper(getApplicationContext());

        long creation = getIntent().getLongExtra("creation", 0);

        if(creation == 0) {
            //setMode(MODE.EDIT_MODE);
            note = new Note();
        }
        else {
            note = db.getNote(creation);
        }

        if(note.getTitle().equals("") || note.getTitle().equals("title")){
            titleView.setText("");
            titleView.setHint("Без названия");
        }
        else {
            titleView.setText(note.getTitle());
        }

        if(note.getNotebook() == 0) {
            spinner.setSelection(0);
        }
        else {
            String name = DBHelper.getInstance().getNotebook(note.getNotebook()).getName();
            for (int i = 0; i < notebooks.size(); i ++) {
                if(notebooks.get(i).equals(name)) {
                    spinner.setSelection(i);
                }
            }
        }

        contentView.setHtml(note.getContent());
    }

    private void saveNote() {
        DBHelper db = new DBHelper(getContext());

        note.setModification(DateUtils.getTimeInMillis());
        note.setTitle(titleView.getText().toString());
        note.setContent(contentView.getHtml());
        if(spinner.getSelectedItem().toString().equals("Inbox")) {
            note.setNotebook(0);
        }
        else {
            List<Notebook> list = DBHelper.getInstance().getAllNotebooks();
            for(Notebook nb : list) {
                if(nb.getName().equals(spinner.getSelectedItem().toString())){
                    note.setNotebook(nb.getId());
                }
            }
        }

        db.updateNote(note);

        KeyboardUtils.hide(drawerLayout, getContext());

        setResult(1);
        finish();
    }

    private void setMode(MODE mode) {
        if(mode == MODE.VIEW_MODE) {
            editItem.setVisible(true);
            doneItem.setVisible(false);
            undoItem.setVisible(false);
            redoItem.setVisible(false);
            attachItem.setVisible(false);
            removeItem.setVisible(false);

            if (getSupportActionBar() != null){
                //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                //getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            titleView.setEnabled(false);
        }

        if(mode == MODE.EDIT_MODE) {
            editItem.setVisible(false);
            doneItem.setVisible(true);
            undoItem.setVisible(true);
            redoItem.setVisible(true);
            attachItem.setVisible(true);
            removeItem.setVisible(true);

            if (getSupportActionBar() != null){
                //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                //getSupportActionBar().setDisplayShowHomeEnabled(false);
            }

            titleView.setEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onComplete() {
        initNotebooksSpinner();
        spinner.setSelection(1);
    }

    @Override
    public void onRemoved() {
        KeyboardUtils.hide(drawerLayout, getContext());
        setResult(1);
        finish();
    }
}
