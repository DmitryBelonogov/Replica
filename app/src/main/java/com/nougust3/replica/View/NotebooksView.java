package com.nougust3.replica.View;

import com.arellomobile.mvp.MvpView;
import com.nougust3.replica.Model.Notebook;

import java.util.ArrayList;

public interface NotebooksView extends MvpView {

    void updateList(ArrayList<Notebook> list);
    void showDialog(String type, String notebookName);
    void showPopupMenu(final long notebookId);
    void showNotebook(long id);

}
