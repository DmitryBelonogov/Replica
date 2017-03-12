package com.nougust3.replica.View.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.nougust3.replica.Model.Notebook;
import com.nougust3.replica.Presenter.NotebooksPresenter;
import com.nougust3.replica.R;
import com.nougust3.replica.Utils.Constants;
import com.nougust3.replica.View.Adapter.NotebookAdapter;
import com.nougust3.replica.View.Dialog.NewNotebookFragment;
import com.nougust3.replica.View.Dialog.OnCompleteListener;
import com.nougust3.replica.View.Dialog.RemoveNotebookFragment;
import com.nougust3.replica.View.Dialog.RenameNotebookFragment;
import com.nougust3.replica.View.NotebooksView;

import java.util.ArrayList;

public class NotebooksActivity extends BaseActivity implements NotebooksView, OnCompleteListener {

    @InjectPresenter
    NotebooksPresenter notebooksPresenter;

    private NotebookAdapter adapter;

    private ListView listView;
    private MenuItem newNotebookItem;

    private NewNotebookFragment newNotebookDialog;
    private RenameNotebookFragment renameNotebookFragment;
    private RemoveNotebookFragment removeNotebookFragment;

    private View anchorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebooks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        newNotebookDialog = new NewNotebookFragment();
        renameNotebookFragment = new RenameNotebookFragment();
        removeNotebookFragment = new RemoveNotebookFragment();

        setNoteId(0);
        initNavigation();
        updateCounter();

        adapter = new NotebookAdapter(this);

        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                notebooksPresenter.onNotebookClick(adapter.getItem(i).getId());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                anchorView = view;
                notebooksPresenter.onShowMenu(adapter.getItem(i).getId());
                return false;
            }
        });

        notebooksPresenter = new NotebooksPresenter();
    }

    @Override
    public void updateList(ArrayList<Notebook> list) {
        ArrayList<Notebook> notebooks = new ArrayList<>();

        notebooks.add(getInboxNotebook());
        notebooks.addAll(list);

        adapter.setNotebooks(notebooks);
        adapter.notifyDataSetChanged();
    }

    public Notebook getInboxNotebook() {
        Notebook inboxNotebook = new Notebook();

        inboxNotebook.setId(0);
        inboxNotebook.setName("Inbox");
        inboxNotebook.setDescription("Unsorted notes");

        return inboxNotebook;
    }

    @Override
    public void showDialog(String type, String notebookName) {
        switch (type) {
            case Constants.DIALOG_NOTEBOOK_NEW:
                newNotebookDialog.show(getSupportFragmentManager(), "newNotebookDialog");
                break;
            case Constants.DIALOG_NOTEBOOK_RENAME:
                renameNotebookFragment.setName(notebookName);
                renameNotebookFragment.show(getSupportFragmentManager(), "renameNotebookDialog");
                break;
            case Constants.DIALOG_NOTEBOOK_REMOVE:
                removeNotebookFragment.setNotebook(notebookName);
                removeNotebookFragment.show(getSupportFragmentManager(), "removeNotebookDialog");
                break;
            default:
        }
    }

    @Override
    public void showPopupMenu(final long notebookId) {
        PopupMenu popup = new PopupMenu(this, anchorView);

        popup.getMenuInflater().inflate(R.menu.notebook_item_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.renameNotebookItem:
                        notebooksPresenter.onRenameMenu(notebookId);
                        return true;
                    case R.id.removeNotebookItem:
                        notebooksPresenter.onRemoveMenu(notebookId);
                        return true;
                    default:
                        return false;
                }
            }
        });

        popup.show();
    }

    @Override
    public void showNotebook(long id) {
        Intent intent = new Intent(NotebooksActivity.this, NotesActivity.class);
        intent.putExtra("notebookId", id);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notebooks_menu, menu);
        newNotebookItem = menu.findItem(R.id.newNotebookItem);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.equals(newNotebookItem)) {
            notebooksPresenter.onNewNotebook();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(listView != null) {
            notebooksPresenter.loadList();
        }
    }

    @Override
    public void onComplete() {
        notebooksPresenter.loadList();
    }

    @Override
    public void onRemoved() { }

    @Override
    public void onSelect(long id) { }
}
