package com.nougust3.diary.View;

import com.arellomobile.mvp.MvpView;

import java.util.ArrayList;

public interface EditorView extends MvpView {

    void showToolbar();
    void hideToolbar();

    void showFAB();
    void hideFAB();

    void enableViews();
    void disableViews();

    void createProgressDialog(String msg);
    void showProgressDialog();
    void hideProgressDialog();

    void showNewNoteDialog();

    void setTitle(String title);
    void setNotebook(String notebook);
    void setContent(String content);

    void populateSpinner(ArrayList<String> notebooks);

    void checkIntents();

    void showRemoveDialog();
    void showNewNotebookDialog();

    void closeEditor();

    void showMessage(String msg);

    void setScrollPosition(float position);

    void onFinish();

}
