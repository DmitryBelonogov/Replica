package com.nougust3.diary;

import android.content.Intent;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.fragments.NewNotebookFragment;
import com.nougust3.diary.models.NotebookModel;
import com.nougust3.diary.models.adapters.NotebookAdapter;

import java.util.List;

public class NotebooksActivity extends BaseActivity {

    private List<NotebookModel> notebooksList;
    private NotebookAdapter adapter;
    private DBHelper db;

    private ListView listView;
    private MenuItem newNotebookItem;

    private DialogFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebooks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DBHelper(getApplicationContext());
        dialog = new NewNotebookFragment();

        initList();
        loadNotebooks();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notebooks_menu, menu);

        newNotebookItem = menu.findItem(R.id.newNotebookItem);

        if (newNotebookItem != null) {

        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (newNotebookItem != null) {

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.equals(newNotebookItem)) {
            dialog.show(getFragmentManager(), "dialog");
        }

        return true;
    }

    private void initList() {
        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(NotebooksActivity.this, NotesActivity.class);
                intent.putExtra("notebookId", adapter.getItem(i).getId());
                startActivity(intent);
            }
        });
    }

    private void loadNotebooks() {
        notebooksList = db.getAllNotebooks();
        adapter = new NotebookAdapter(this, notebooksList);
        listView.setAdapter(adapter);
    }
}
