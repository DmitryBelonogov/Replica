package com.nougust3.diary.ui;

import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.fiberlink.maas360.android.richtexteditor.RichEditText;
import com.fiberlink.maas360.android.richtexteditor.RichTextActions;
import com.nougust3.diary.R;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Note;
import com.nougust3.diary.models.Notebook;
import com.nougust3.diary.utils.DateUtils;
import com.nougust3.diary.utils.KeyboardUtils;

import java.util.ArrayList;
import java.util.List;

public class EditorActivity extends BaseActivity {

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

    private enum MODE {
        VIEW_MODE, EDIT_MODE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editor);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

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

        setMode(MODE.VIEW_MODE);
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
            removeNote();
        }
        else if(item.equals(editItem)) {
            Log.i("dd", "Set edit mode");
            setMode(MODE.EDIT_MODE);
        }

        return true;
    }

    private void initToolbar() {
        titleView = (EditText) findViewById(R.id.titleView);
        spinner = (Spinner) findViewById(R.id.spinner);

        notebooks = new ArrayList<>();
        notebooks.add("Inbox");
        List<Notebook> list = DBHelper.getInstance().getAllNotebooks();

        for (Notebook notebook : list) {
            notebooks.add(notebook.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, notebooks);
        spinner.setAdapter(adapter);


    }

    private void initContent() {
        contentView = (RichEditText) findViewById(R.id.contentView);
        RichTextActions richTextActions = (RichTextActions) findViewById(R.id.rich_text_actions);
        contentView.setRichTextActionsView(richTextActions);
    }

    private void loadNote() {
        DBHelper db = new DBHelper(getApplicationContext());

        long creation = getIntent().getLongExtra("creation", 0);
        note = db.getNote(creation);

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

    private void removeNote() {
        DBHelper db = new DBHelper(getContext());

        note.setArchive(1);
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
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                //getSupportActionBar().invalidateOptionsMenu();
            }

            titleView.setEnabled(false);
        }

        if(mode == MODE.EDIT_MODE) {
            Log.i("f", "Режим изменен");
            editItem.setVisible(false);
            doneItem.setVisible(true);
            undoItem.setVisible(true);
            redoItem.setVisible(true);
            attachItem.setVisible(true);
            removeItem.setVisible(true);
            if (getSupportActionBar() != null){
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                //getSupportActionBar().invalidateOptionsMenu();
            }

            titleView.setEnabled(true);
        }

        //invalidateOptionsMenu();
    }
}
