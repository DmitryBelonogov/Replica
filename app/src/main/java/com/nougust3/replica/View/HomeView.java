package com.nougust3.replica.View;

import com.arellomobile.mvp.MvpView;
import com.nougust3.replica.Model.Note;

import java.util.ArrayList;

public interface HomeView extends MvpView {

    void updateNotesList(ArrayList<Note> notesList);
    void updateList(ArrayList<Note> notesList);
    void updateHeader(String text);

    void showMessage(String msg);

    void startSelection();
    void selectItem(int position);
    void doneSelection();

    void openEditor();
    void closeEditor();

}
