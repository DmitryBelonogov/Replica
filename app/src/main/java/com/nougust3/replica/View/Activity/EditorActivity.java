package com.nougust3.replica.View.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.fiberlink.maas360.android.richtexteditor.RichEditText;
import com.fiberlink.maas360.android.richtexteditor.RichTextActions;
import com.fiberlink.maas360.android.richtexteditor.RichWebView;
import com.nougust3.replica.Presenter.EditorPresenter;
import com.nougust3.replica.R;
import com.nougust3.replica.Utils.Preferences;
import com.nougust3.replica.View.Dialog.EditorDialogsListener;
import com.nougust3.replica.View.Dialog.NewNotebookFragment;
import com.nougust3.replica.View.Dialog.RemoveNoteFragment;
import com.nougust3.replica.View.EditorView;
import com.nougust3.thinprogressbar.ThinProgressBar;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditorActivity extends MvpAppCompatActivity implements EditorView, EditorDialogsListener {

    @InjectPresenter
    EditorPresenter editorPresenter;

    private FloatingActionButton fab;

    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;
    private CoordinatorLayout.LayoutParams params;
    private AppBarLayout.Behavior behavior;
    private EditText titleView;
    private RichEditText contentView;
    private Spinner spinner;
    private ThinProgressBar progressBar;

    private MenuItem doneItem;
    private MenuItem undoItem;
    private MenuItem redoItem;
    private MenuItem removeItem;
    private MenuItem editItem;

    private ProgressDialog progressDialog;
    private NewNotebookFragment newNotebookDialog;
    private boolean isEdit;
    private boolean headerVisible;
    private boolean actionsVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        titleView = (EditText) findViewById(R.id.titleView);

        progressBar = (ThinProgressBar) findViewById(R.id.progress_bar);
        progressBar.setColor(0xff55226E);

        contentView = (RichEditText) findViewById(R.id.contentView);
        contentView.setRichTextActionsView((RichTextActions) findViewById(R.id.rich_text_actions));
        contentView.getmEditor().setOnScrollChangedCallback(new RichWebView.OnScrollChangedCallback() {
            @Override
            public void onScrollChange(WebView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                progressBar.setMax((int)(contentView.getmEditor().getContentHeight() * contentView.getmEditor().getScale() - contentView.getmEditor().getHeight()));
                progressBar.setProgress(scrollY);
                editorPresenter.onScrollNote(scrollY, oldScrollY);


            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editorPresenter.onEditNote();
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == spinner.getCount() - 1) {
                    editorPresenter.onNewNotebook();
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parentView) { }
        });

        actionsVisible = contentView.isActionsVisible();
        headerVisible = true;
    }

    @Override
    public void showToolbar() {
        isEdit = true;
        if(getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
    }

    @Override
    public void hideToolbar() {
        isEdit = false;
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void showHeader() {
        if(!headerVisible) {
            headerVisible = true;

            params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            behavior = (AppBarLayout.Behavior) params.getBehavior();

            if (behavior != null) {
                behavior.onNestedFling(coordinatorLayout, appBarLayout, null, 0, -10000, true);
            }
        }
    }

    @Override
    public void hideHeader() {
        if(headerVisible) {
            headerVisible = false;

            params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            behavior = (AppBarLayout.Behavior) params.getBehavior();

            if (behavior != null) {
                behavior.onNestedFling(coordinatorLayout, appBarLayout, null, 0, 10000, true);
            }
        }
    }

    @Override
    public void showFAB() {
        if(!fab.isShown()) {
            fab.show();
            showHeader();
        }
    }

    @Override
    public void hideFAB() {
        if(fab.isShown()) {
            fab.hide();
            if(!isEdit) {
                hideHeader();
            }
        }
    }

    @Override
    public void disableViews() {
        titleView.setEnabled(false);
        spinner.setEnabled(false);
        contentView.setFocusable(false);
        closeEditor();
        contentView.hideActions();
        actionsVisible = false;
    }

    @Override
    public void enableViews() {
        titleView.setEnabled(true);
        spinner.setEnabled(true);
        contentView.setFocusable(true);
    }

    @Override
    public void showNewNoteDialog() {
        newNotebookDialog = new NewNotebookFragment();
        newNotebookDialog.show(getSupportFragmentManager(), "newNotebookDialog");
    }

    @Override
    public void createProgressDialog(String msg) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(msg);
    }

    @Override
    public void showProgressDialog() {
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.hide();
    }

    @Override
    public void setScrollPosition(final float position) {
        contentView.getmEditor().setScrollY(Math.round(position));
        progressBar.setMax((int)(contentView.getmEditor().getContentHeight() * contentView.getmEditor().getScale() - contentView.getmEditor().getHeight()));
        progressBar.setProgress(Math.round(position));

    }

    @Override
    public void setTitle(String title) {
        titleView.setText(title);
    }

    @Override
    public void setContent(String content) {
        contentView.setHtml(content);
    }

    @Override
    public void setNotebook(String notebook) {
        for (int i = 0; i < spinner.getCount(); i ++) {
            if(spinner.getItemAtPosition(i).toString().equals(notebook)) {
                spinner.setSelection(i);
            }
        }
    }

    @Override
    public void checkIntents() {
        if(getIntent().getType() != null &&
                Intent.ACTION_SEND.equals(getIntent().getAction()) &&
                "text/plain".equals(getIntent().getType())) {
            editorPresenter.onParseHtml(getIntent().getStringExtra(Intent.EXTRA_TEXT));
        }
        else {
            long creation = getIntent().getLongExtra("creation5", 0);

            if(creation != 0) {
                editorPresenter.onLoadNote(creation);
            }
            else {
                editorPresenter.onCreateNote();
                editorPresenter.onSetNotebook(getIntent().getLongExtra("notebook", 0));
            }
        }
    }

    @Override
    public void showRemoveDialog() {
        RemoveNoteFragment removeNoteFragment = new RemoveNoteFragment();
        removeNoteFragment.show(getSupportFragmentManager(), "Remove note fragment");
    }

    @Override
    public void onRemove() {
        editorPresenter.onRemoveConfirm();
    }

    @Override
    public void showNewNotebookDialog() {
        newNotebookDialog.show(getSupportFragmentManager(), "New notebook dialog");
    }

    @Override
    public void onNewNotebook() {
        editorPresenter.onNewNotebook();
    }

    @Override
    public void onFinish() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);

        doneItem = menu.findItem(R.id.app_bar_done);
        undoItem = menu.findItem(R.id.app_bar_undo);
        redoItem = menu.findItem(R.id.app_bar_redo);
        removeItem = menu.findItem(R.id.app_bar_remove);
        editItem = menu.findItem(R.id.app_bar_edit);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.equals(doneItem)) {
            editorPresenter.onSaveNote(
                    titleView.getText().toString(),
                    spinner.getSelectedItem().toString(),
                    contentView.getHtml(),
                    getScrollPosition()
            );
        }
        else if(item.equals(removeItem)) {
            editorPresenter.onRemoveNote();
        }
        else if(item.equals(undoItem)) {
            contentView.getmEditor().undo();
        }
        else if(item.equals(redoItem)) {
            contentView.showActions();
        }
        else if(item.equals(editItem)) {


            if(actionsVisible) {
                contentView.hideActions();
            }
            else {
                contentView.showActions();
            }
            actionsVisible = !actionsVisible;
        }

        return true;
    }

    @Override
    public void populateSpinner(ArrayList<String> notebooks) {
        notebooks.add("New notebook");
        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.editor_spinner_item, notebooks));
    }

    @Override
    public void closeEditor() {
        contentView.clearFocus();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public Context getContext() {
        return getApplicationContext();
    }

    private int getScrollPosition() {
        return contentView.getmEditor().getScrollY();
    }

    @Override
    public void onBackPressed() {
        if(isEdit) {
            editorPresenter.onSaveNote(
                    titleView.getText().toString(),
                    spinner.getSelectedItem().toString(),
                    contentView.getHtml(),
                    getScrollPosition()
            );
            return;
        }
        editorPresenter.onClose(getScrollPosition());
        super.onBackPressed();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        if (Preferences.getInstance().get("volumeScroll")) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (action == KeyEvent.ACTION_DOWN) {
                        contentView.getmEditor().pageUp(false);
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (action == KeyEvent.ACTION_DOWN) {
                        contentView.getmEditor().pageDown(false);
                    }
                    return true;
                default:
                    return super.dispatchKeyEvent(event);
            }
        }

        return super.dispatchKeyEvent(event);
    }
}
