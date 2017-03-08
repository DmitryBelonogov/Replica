package com.nougust3.replica.Presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.nougust3.replica.Keep;
import com.nougust3.replica.Model.Content;
import com.nougust3.replica.Model.Database.DBHelper;
import com.nougust3.replica.Model.Note;
import com.nougust3.replica.Model.Notebook;
import com.nougust3.replica.Utils.ContentUtils;
import com.nougust3.replica.Utils.DateUtils;
import com.nougust3.replica.Utils.ImageLoader;
import com.nougust3.replica.Utils.ImageLoaderInterface;
import com.nougust3.replica.Utils.Preferences;
import com.nougust3.replica.View.EditorView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@InjectViewState
public class EditorPresenter extends MvpPresenter<EditorView> implements ImageLoaderInterface {

    private Note note;

    private boolean isNoteEditing;
    private boolean isNewNote = false;
    private boolean isParsedNote = false;

    public EditorPresenter() {
        getViewState().checkIntents();
    }

    public void onSetNotebook(long id) {
        getViewState().setNotebook(getNotebookName(id));
    }

    public void onParseHtml(String url) {
        isNewNote = true;
        isParsedNote = true;

        getViewState().createProgressDialog("Подождите. Загружаю...");
        getViewState().showProgressDialog();

        parseHtml(url);
    }

    public void onLoadNote(long id) {
        String content;

        note = DBHelper.getInstance().getNote(id);
        content = ContentUtils.updateUrls(note.getContent(), true);

        getViewState().setContent(content);
        getViewState().setScrollPosition(note.getScrollPosition());
        getViewState().setTitle(note.getTitle());
        getViewState().populateSpinner(getNotebooksNames());
        getViewState().setNotebook(getNotebookName(note.getNotebook()));
        getViewState().disableViews();
        getViewState().hideToolbar();
    }

    public void onCreateNote() {
        isNewNote = true;
        isNoteEditing = true;

        note = new Note();
        note.setCreation(DateUtils.getTimeInMillis());

        getViewState().setTitle("");
        getViewState().setContent("");
        getViewState().populateSpinner(getNotebooksNames());
        getViewState().setNotebook("Inbox");
        getViewState().enableViews();
        getViewState().showToolbar();
        getViewState().hideFAB();
    }

    public void onEditNote() {
        isNoteEditing = true;

        getViewState().enableViews();
        getViewState().showToolbar();
        getViewState().hideFAB();
    }

    public void onRemoveNote() {
        if(isNewNote) {
            getViewState().showRemoveDialog();
        }
        else {
            getViewState().onFinish();
        }
    }

    public void onRemoveConfirm() {
        note.setArchive(1);

        DBHelper.getInstance().updateNote(note);
    }

    public void onNewNotebook() {
        getViewState().showNewNotebookDialog();
    }

    public void onScrollNote(int scrollY, int oldScrollY) {
        if(isNoteEditing) {
            return;
        }

        if (scrollY > oldScrollY && scrollY > 50) {
            getViewState().hideFAB();
        }
        if (scrollY < oldScrollY) {
            getViewState().showFAB();
        }
    }

    public void onSaveNote(String title, String notebook, String content, int position) {
        isNoteEditing = false;

        long notebookId;

        if(notebook.equals("Inbox")) {
            notebookId = 0;
        }
        else {
            notebookId = DBHelper.getInstance().getNotebookId(notebook);
        }

        note.setTitle(title);
        note.setNotebook(notebookId);
        note.setScrollPosition((float) position);
        note.setModification(DateUtils.getTimeInMillis());

        content = ContentUtils.updateUrls(content, false);
        note.setContent(content);

        DBHelper.getInstance().updateNote(note);

        getViewState().disableViews();
        getViewState().closeEditor();
        getViewState().hideToolbar();
        getViewState().showFAB();
        getViewState().showMessage("Note saved");

        if(isParsedNote) {
            getViewState().onFinish();
        }
    }

    private void parseHtml(String url) {
        Keep.getApi().getData(url).enqueue(new Callback<Content>() {
            @Override
            public void onResponse(Call<Content> call, Response<Content> response) {
                Content content = response.body();

                note = new Note();
                note.setTitle(content.getTitle());
                note.setContent(content.getContent());
                note.setCreation(DateUtils.getTimeInMillis());

                getViewState().setTitle(content.getTitle());
                getViewState().populateSpinner(getNotebooksNames());
                getViewState().setNotebook("Inbox");
                onEditNote();

                parseImages();
            }
            @Override
            public void onFailure(Call<Content> call, Throwable t) {
                t.printStackTrace();
                getViewState().hideProgressDialog();
                getViewState().showMessage("Parsed fail");
            }
        });
    }

    private void parseImages() {
        String content;

        content = ContentUtils.updateUrls(note.getContent(), true);

        if(Preferences.getInstance().get("saveImages")) {
            ImageLoader.getInstance().setListener(this);
            ImageLoader.getInstance().getUrls(note.getContent());
            ImageLoader.getInstance().loadImages();
        }

        getViewState().setContent(content);
    }

    private ArrayList<String> getNotebooksNames() {
        ArrayList<String> notebooksNames = new ArrayList<>();
        notebooksNames.add("Inbox");

        for (Notebook notebook : DBHelper.getInstance().getAllNotebooks()) {
            notebooksNames.add(notebook.getName());
        }

        return notebooksNames;
    }

    private String getNotebookName(long id) {
        if(id == 0) {
            return "Inbox";
        }

        return DBHelper.getInstance().getNotebook(id).getName();
    }

    public void onClose(int position) {
        note.setScrollPosition((float) position);
        DBHelper.getInstance().updateNote(note);
    }

    @Override
    public void onLoadingDone() {
        getViewState().hideProgressDialog();
    }
}
