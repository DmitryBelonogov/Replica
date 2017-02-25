package com.nougust3.diary.View;

import com.arellomobile.mvp.MvpView;
import com.nougust3.diary.models.Note;

import java.util.ArrayList;

public interface HomeView extends MvpView {

    void updateNotesList(ArrayList<Note> notesList);
    void updateHeader(String text);

    void startSelection();
    void selectItem(int position);
    void doneSelection();

    void openEditor();
    void closeEditor();

    void updateCounter(int count);

}
