package Presenters;

import android.os.AsyncTask;
import android.util.SparseBooleanArray;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.nougust3.diary.Views.HomeView;
import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Note;
import com.nougust3.diary.utils.DateUtils;

import java.util.ArrayList;

@InjectViewState
public class HomePresenter extends MvpPresenter<HomeView> {

    ArrayList<Note> notesList;

    public HomePresenter() {
        notesList = new ArrayList<>();
        //loadNotes();
    }

    private void loadNotes() {
        notesList = DBHelper.getInstance().getAllNotes();
    }

    public void onUpdateNotes() {
        //loadNotes();
        //getViewState().updateNotesList(notesList);
        NotesLoader loader = new NotesLoader();
        loader.execute();
    }

    public void onScrollList(int position) {
        if(notesList.size() > 0) {
            getViewState().updateHeader(DateUtils.format(notesList.get(position).getModification()));
        }
    }

    public void onStartSelection(int firstItem) {
        getViewState().startSelection();
        getViewState().selectItem(firstItem);
    }

    public void onRemoveNotes(SparseBooleanArray selected) {
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                Note note = notesList.get(selected.keyAt(i));
                note.setArchive(1);
                DBHelper.getInstance().updateNote(note);
            }
        }
        loadNotes();
        getViewState().updateNotesList(notesList);
        getViewState().showMessage("Notes removed");
    }

    public void onMoveNotes(SparseBooleanArray selected, long id) {
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                Note note = DBHelper.getInstance().getNote(notesList.get(selected.keyAt(i)).getCreation());
                note.setNotebook(id);
                DBHelper.getInstance().updateNote(note);
            }
        }
        loadNotes();
        getViewState().updateNotesList(notesList);
        getViewState().showMessage("Notes moved");
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
        getViewState().showMessage("Note saved");
    }

    class NotesLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() { }

        @Override
        protected Void doInBackground(Void... params) {
            loadNotes();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            getViewState().updateList(notesList);
        }
    }
}
