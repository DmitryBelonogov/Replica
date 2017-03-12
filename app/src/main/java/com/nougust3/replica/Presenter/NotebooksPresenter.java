package com.nougust3.replica.Presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.nougust3.replica.Model.Notebook;
import com.nougust3.replica.Model.Repository.NotebooksRepository;
import com.nougust3.replica.Utils.Constants;
import com.nougust3.replica.View.NotebooksView;

import java.util.ArrayList;

@InjectViewState
public class NotebooksPresenter  extends MvpPresenter<NotebooksView> {

    NotebooksRepository repository;

    public NotebooksPresenter() {
        loadList();
    }

    public void loadList() {
        getViewState().updateList(repository.getAll());
    }

    public void onNewNotebook() {
        getViewState().showDialog(Constants.DIALOG_NOTEBOOK_NEW, "");
    }

    public void onShowMenu(long id) {

    }

    public void onRenameMenu(long id) {
        getViewState().showDialog(Constants.DIALOG_NOTEBOOK_RENAME,
                repository.get(id).getName());
    }

    public void onRemoveMenu(long id) {
        getViewState().showDialog(Constants.DIALOG_NOTEBOOK_REMOVE,
                repository.get(id).getName());
    }

    public void onNotebookClick(long id) {
        getViewState().showNotebook(id);
    }

}
