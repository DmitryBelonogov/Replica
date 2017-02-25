package com.nougust3.diary.Presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.nougust3.diary.View.HomeView;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Note;
import com.nougust3.diary.Utils.DateUtils;

import java.util.ArrayList;

@InjectViewState
public class HomePresenter extends MvpPresenter<HomeView> {

    ArrayList<Note> notesList;

    public HomePresenter() {
        loadNotes();
    }

    private void loadNotes() {
        notesList = DBHelper.getInstance().getAllNotes();
    }

    public void onUpdateNotes() {
        loadNotes();
        getViewState().updateNotesList(notesList);
        getViewState().updateCounter(Integer.parseInt(DBHelper.getInstance().getInboxSize()));
    }

    public void onScrollList(int position) {
        getViewState().updateHeader(DateUtils.format(notesList.get(position).getModification()));
    }

    public void onStartSelection(int firstItem) {
        getViewState().startSelection();
        getViewState().selectItem(firstItem);
    }

    public void onSelectItem(int position) {

    }

    public void onEditNote() {
        getViewState().openEditor();
    }

    public void onSaveNote(String text) {
        Note note = new Note();

        if(text.equals("")) {
            return;
        }

        note.setCategory("Inbox");
        note.setTitle("");
        note.setContent(text);
        note.setIsTask(0);
        note.setArchive(0);
        note.setNotebook(0);

        DBHelper.getInstance().updateNote(note);

        loadNotes();
        getViewState().closeEditor();
        getViewState().updateNotesList(notesList);
    }
}
