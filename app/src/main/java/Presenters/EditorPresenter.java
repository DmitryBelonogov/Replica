package Presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import com.nougust3.diary.Keep;
import com.nougust3.diary.Views.EditorView;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Content;
import com.nougust3.diary.models.Note;
import com.nougust3.diary.models.Notebook;
import com.nougust3.diary.utils.DateUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@InjectViewState
public class EditorPresenter extends MvpPresenter<EditorView> {

    //private final Repository<Note> repository;

    private Note note;

    private boolean isNoteEditing;
    private boolean isNewNote = false;

    public EditorPresenter() {
        getViewState().checkIntents();
    }

    public void onSetNotebook(long id) {
        getViewState().setNotebook(getNotebookName(id));
    }

    public void onParseHtml(String url) {
        isNewNote = true;

        getViewState().createProgressDialog("Подождите. Загружаю...");
        getViewState().showProgressDialog();

        parseHtml(url);
    }

    public void onLoadNote(long id) {
        note = DBHelper.getInstance().getNote(id);

        getViewState().setTitle(note.getTitle());
        getViewState().setContent(note.getContent());
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

    public void onSaveNote(String title, String notebook, String content) {
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
        note.setModification(DateUtils.getTimeInMillis());

        DBHelper.getInstance().updateNote(note);

        getViewState().disableViews();
        getViewState().hideToolbar();
        getViewState().showFAB();
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
                getViewState().setContent(content.getContent());
                getViewState().populateSpinner(getNotebooksNames());
                getViewState().setNotebook("Inbox");
                getViewState().disableViews();
                getViewState().hideToolbar();
                getViewState().hideProgressDialog();
            }
            @Override
            public void onFailure(Call<Content> call, Throwable t) {
                t.printStackTrace();
                getViewState().hideProgressDialog();
            }
        });
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

}
