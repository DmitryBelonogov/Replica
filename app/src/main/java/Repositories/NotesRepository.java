package Repositories;

import com.nougust3.diary.db.DBHelper;
import com.nougust3.diary.models.Note;

import java.util.ArrayList;
import java.util.List;

public class NotesRepository implements Repository<Note> {

   @Override
    public void add(final Note note) {
       DBHelper.getInstance().updateNote(note);
   }

    @Override
    public void add(Iterable<Note> item) {

    }

    @Override
    public void update(Note note) {
        DBHelper.getInstance().updateNote(note);
    }

    @Override
    public void remove(Note note) {

    }

    @Override
    public void remove(SqlSpecification specification) {

    }

    @Override
    public List<Note> query(SqlSpecification specification) {
        return new ArrayList<>();
    }
}
