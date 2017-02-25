package com.nougust3.diary.Presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import com.nougust3.diary.Keep;
import com.nougust3.diary.View.EditorView;
import com.nougust3.diary.Model.Database.DBHelper;
import com.nougust3.diary.Model.Content;
import com.nougust3.diary.Model.Note;
import com.nougust3.diary.Model.Notebook;
import com.nougust3.diary.Utils.ContentUtils;
import com.nougust3.diary.Utils.DateUtils;
import com.nougust3.diary.Utils.ImageLoader;
import com.nougust3.diary.Utils.ImageLoaderInterface;
import com.nougust3.diary.Utils.Preferences;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@InjectViewState
public class EditorPresenter extends MvpPresenter<EditorView> implements ImageLoaderInterface {

    //private final Repository<Note> repository;

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
        note = DBHelper.getInstance().getNote(id);

        getViewState().setScrollPosition(note.getScrollPosition());
        getViewState().setTitle(note.getTitle());
        if(Preferences.getInstance().get("saveImages")) {
            String content = note.getContent();
            //content = ContentUtils.replaceUrls(content);
            content = ImageLoader.getInstance().createUrlsLocal(content);
            getViewState().setContent(content);
        }
        else {
            String content = note.getContent();
            //content = ContentUtils.replaceUrls(content);
            content = ImageLoader.getInstance().getUrlsOriginal(content);
            getViewState().setContent(content);
        }
        //getViewState().setContent(note.getContent().replace(",", "/"));
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

        if (scrollY > oldScrollY && scrollY > 0) {
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
        note.setContent(content);
        note.setScrollPosition((float) position);
        note.setModification(DateUtils.getTimeInMillis());

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
        ImageLoader.getInstance().setListener(this);
        ImageLoader.getInstance().getUrls(note.getContent());
        String content = ImageLoader.getInstance().createUrlsLocal(note.getContent());
        ImageLoader.getInstance().loadImages();
        //note.setContent(ImageLoader.getInstance().createUrlsLocal(note.getContent()));

        //if(Preferences.getInstance().get("saveImages")) {
        content = ContentUtils.replaceUrls(content);
        getViewState().setContent(content);
        //}
        //getViewState().setContent(note.getContent().replace(",", "/"));

        getViewState().setContent(ContentUtils.replaceUrls(note.getContent()));
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
