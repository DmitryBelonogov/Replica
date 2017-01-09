package com.nougust3.diary;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.fiberlink.maas360.android.richtexteditor.RichEditText;
import com.fiberlink.maas360.android.richtexteditor.RichTextActions;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Note;
import com.nougust3.diary.utils.DateUtils;
import com.nougust3.diary.utils.KeyboardUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditorView extends BaseActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private EditText titleView;
    private RichEditText contentView;

    private MenuItem doneItem;
    private MenuItem editItem;
    private MenuItem removeItem;

    private Note note;

    private enum MODE {
        VIEW_MODE, EDIT_MODE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MyriadPro-Light.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.editor_view);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        initToolbar();
        initContent();


loadNote();
        setMode(MODE.VIEW_MODE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);

        doneItem = menu.findItem(R.id.app_bar_done);
        editItem = menu.findItem(R.id.app_bar_edit);
        removeItem = menu.findItem(R.id.app_bar_remove);

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
            setMode(MODE.EDIT_MODE);
        }

        return true;
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);

        setSupportActionBar(toolbar);

        titleView = (EditText) findViewById(R.id.titleView);
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

        contentView.setHtml(note.getContent());
    }

    private void saveNote() {
        DBHelper db = new DBHelper(getContext());

        note.setModification(DateUtils.getTimeInMillis());
        note.setTitle(titleView.getText().toString());
        note.setContent(contentView.getHtml());

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
            toolbar.setTitle("");

            if (getSupportActionBar() != null){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            titleView.setEnabled(false);
            //contentView.setEnabled(false);
        }

        if(mode == MODE.EDIT_MODE) {
            toolbar.setTitle("Save");

            if (getSupportActionBar() != null){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            titleView.setEnabled(true);
            //contentView.setEnabled(true);
        }

        invalidateOptionsMenu();
    }
}
