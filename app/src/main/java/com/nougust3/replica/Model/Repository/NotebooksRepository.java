package com.nougust3.replica.Model.Repository;

import com.nougust3.replica.Model.Database.DBHelper;
import com.nougust3.replica.Model.Notebook;

import java.util.ArrayList;

public class NotebooksRepository implements Repository<Notebook> {

    public ArrayList<Notebook> getAll() {
        return new ArrayList<>(DBHelper.getInstance().getAllNotebooks());
    }

    @Override
    public Notebook get(long id) {
        return DBHelper.getInstance().getNotebook(id);
    }

    @Override
    public void update(Notebook notebook) {
        DBHelper.getInstance().updateNotebook(notebook);
    }

    @Override
    public void remove(Notebook notebook) {
        DBHelper.getInstance().removeNotebook(notebook.getName());
    }

}