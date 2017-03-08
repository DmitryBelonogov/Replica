package com.nougust3.replica.Model.Repository;

import com.nougust3.replica.Model.Database.DBHelper;
import com.nougust3.replica.Model.Note;

public class NotesRepository implements Repository<Note> {

    @Override
    public void add(final Note note) {
       DBHelper.getInstance().updateNote(note);
    }

    @Override
    public void update(Note note) {
        DBHelper.getInstance().updateNote(note);
    }

    @Override
    public void remove(Note note) {
        DBHelper.getInstance().remove(note.getCreation());
    }

}
