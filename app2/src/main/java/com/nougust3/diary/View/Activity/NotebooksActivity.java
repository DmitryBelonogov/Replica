package com.nougust3.diary.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nougust3.diary.R;
import com.nougust3.diary.Model.Database.DBHelper;
import com.nougust3.diary.View.Dialog.NewNotebookFragment;
import com.nougust3.diary.Model.Notebook;
import com.nougust3.diary.View.Adapter.NotebookAdapter;
import com.nougust3.diary.View.Dialog.OnCompleteListener;
import com.nougust3.diary.View.Dialog.RemoveNotebookFragment;
import com.nougust3.diary.View.Dialog.RenameNotebookFragment;

import java.util.List;

public class NotebooksActivity extends BaseActivity implements OnCompleteListener {

    private Toolbar toolbar;

    private List<Notebook> notebooksList;
    private NotebookAdapter adapter;
    private DBHelper db;

    private ListView listView;
    private MenuItem newNotebookItem;

    private NewNotebookFragment newNotebookDialog;
    private RenameNotebookFragment renameNotebookFragment;
    private RemoveNotebookFragment removeNotebookFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebooks);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        db = new DBHelper(getApplicationContext());
        newNotebookDialog = new NewNotebookFragment();
        renameNotebookFragment = new RenameNotebookFragment();
        removeNotebookFragment = new RemoveNotebookFragment();

        setNoteId(0);

        initNavigation();
        initList();
        loadNotebooks();
        updateCounter();
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
            newNotebookDialog.show(getSupportFragmentManager(), "newNotebookDialog");
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showMenu(view, i);
                return false;
            }
        });
    }

    private void loadNotebooks() {
        Notebook inboxNotebook = new Notebook();
        inboxNotebook.setId(0);
        inboxNotebook.setName("Inbox");
        inboxNotebook.setDescription("Unsorted notes");
        notebooksList = db.getAllNotebooks();
        notebooksList.add(0, inboxNotebook);
        adapter = new NotebookAdapter(this, notebooksList);
        listView.setAdapter(adapter);
    }

    private void showMenu(View v, int id) {
        final int notebookId = id;
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.notebook_item_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.renameNotebookItem:
                        rename(notebooksList.get(notebookId).getName());
                        return true;
                    case R.id.removeNotebookItem:
                        remove(notebooksList.get(notebookId).getName());
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.show();
    }

    private void rename(String name) {
        renameNotebookFragment.setName(name);
        renameNotebookFragment.show(getSupportFragmentManager(), "renameNotebookDialog");
    }

    private void remove(String name) {
        removeNotebookFragment.setNotebook(name);
        removeNotebookFragment.show(getSupportFragmentManager(), "removeNotebookDialog");
    }

   //@Override
    //public void onDoneClick(DialogFragment dialog) {
     //   loadNotebooks();
    //}

    @Override
    protected void onResume() {
        super.onResume();
        if(listView != null) {
            loadNotebooks();
        }
    }

    @Override
    public void onComplete() {
        loadNotebooks();
    }

    @Override
    public void onRemoved() { }

    @Override
    public void onSelect(long id) { }
}
