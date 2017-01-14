package com.nougust3.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.util.Log;

import com.nougust3.diary.Keep;
import com.nougust3.diary.models.Note;
import com.nougust3.diary.models.Notebook;
import com.nougust3.diary.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = Constants.DATABASE_NAME;
    private static final int DATABASE_VERSION = Constants.DATABASE_VERSION;
    private static final String DB_DIR = "db";

    private static final String TABLE_NOTES = "notes";
    private static final String TABLE_NOTEBOOKS = "notebooks";

    private static final String KEY_CREATION = "creation";
    private static final String KEY_MODIFICATION = "modification";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_TASK = "task";
    private static final String KEY_ARCHIVE = "archive";
    private static final String KEY_NOTEBOOK = "notebook";

    private static final String KEY_NOTEBOOK_ID = "id";
    private static final String KEY_NOTEBOOK_PARENT = "parent";
    private static final String KEY_NOTEBOOK_NAME = "name";
    private static final String KEY_NOTEBOOK_DESCRIPTION = "description";

    private static final String CREATE_QUERY = "create.sql";

    private SQLiteDatabase db = null;

    private final Context context;
    private static DBHelper instance = null;

    public static synchronized DBHelper getInstance() {
        return getInstance(Keep.getAppContext());
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            execSqlFile(CREATE_QUERY, db);
        } catch (IOException e) {
            throw new RuntimeException("Database creation failed", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void execSqlFile(String file, SQLiteDatabase db) throws SQLException, IOException {
        for(String instruction : SQLParser.parseSqlFile(DB_DIR + "/" + file, context.getAssets())) {
            try {
                db.execSQL(instruction);
            } catch (Exception e) {
                Log.e("a", "Error executing command: " + instruction, e);
            }
        }
    }

    private SQLiteDatabase getDatabase(boolean forceWritable) {
        try {
            SQLiteDatabase db = getReadableDatabase();

            if(forceWritable && db.isReadOnly()) {
                throw new SQLiteReadOnlyDatabaseException("Required writable database read-only");
            }

            return db;
        } catch (IllegalStateException e) {
            return this.db;
        }
    }

    public Note updateNote(Note note) {
        SQLiteDatabase db = getDatabase(true);
        ContentValues values = new ContentValues();

        db.beginTransaction();

        values.put(KEY_CREATION, note.getCreation());
        values.put(KEY_MODIFICATION, note.getModification());
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_CATEGORY, note.getCategory());
        values.put(KEY_TASK, note.isTask() ? 1 : 0);
        values.put(KEY_ARCHIVE, note.isArchive() ? 1 : 0);
        values.put(KEY_NOTEBOOK, note.getNotebook());

        db.insertWithOnConflict(TABLE_NOTES, KEY_CREATION, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.setTransactionSuccessful();
        db.endTransaction();

        return note;
    }

    public Notebook updateNotebook(Notebook notebook) {
        SQLiteDatabase db = getDatabase(true);
        ContentValues values = new ContentValues();

        db.beginTransaction();

        values.put(KEY_NOTEBOOK_ID, notebook.getId());
        values.put(KEY_NOTEBOOK_PARENT, notebook.getParent());
        values.put(KEY_NOTEBOOK_NAME, notebook.getName());
        values.put(KEY_NOTEBOOK_DESCRIPTION, notebook.getDescription());

        db.insertWithOnConflict(TABLE_NOTEBOOKS, KEY_NOTEBOOK_ID, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.setTransactionSuccessful();
        db.endTransaction();

        return notebook;
    }

    public Note getNote(long id) {
        String where = " WHERE " + KEY_CREATION + " = " + id;
        List<Note> notes = getNotes(where);

        if(notes.size() > 0) {
            return notes.get(0);
        }

        return null;
    }

    public Notebook getNotebook(long id) {
        String where = " WHERE " + KEY_NOTEBOOK_ID + " = " + id;
        List<Notebook> notebooks = getNotebooks(where);

        if(notebooks.size() > 0) {
            return notebooks.get(0);
        }

        return null;
    }

    private List<Note> getNotes(String where) {
        List<Note> noteList = new ArrayList<>();

        String query = "SELECT " + KEY_CREATION + ","
                + KEY_MODIFICATION + ","
                + KEY_TITLE + ","
                + KEY_CONTENT + ","
                + KEY_CATEGORY + ","
                + KEY_TASK + ","
                + KEY_ARCHIVE + ","
                + KEY_NOTEBOOK + " FROM "
                + TABLE_NOTES
                + where + " ORDER BY "
                + KEY_MODIFICATION + " DESC ";

        Cursor cursor = null;

        try {
            cursor = getDatabase(false).rawQuery(query, null);

            if(cursor.moveToFirst()) {
                do {
                    Note note = new Note();
                    note.setCreation(Long.parseLong(cursor.getString(0)));
                    note.setModification(Long.parseLong(cursor.getString(1)));
                    note.setTitle(cursor.getString(2));
                    note.setContent(cursor.getString(3));
                    note.setCategory(cursor.getString(4));
                    note.setIsTask(Integer.parseInt(cursor.getString(5)));
                    note.setArchive(Integer.parseInt(cursor.getString(6)));
                    note.setNotebook(Long.parseLong(cursor.getString(7)));

                    noteList.add(note);
                } while (cursor.moveToNext());
            }
        }
        finally {
            if(cursor != null) {
                cursor.close();
            }
        }

        return noteList;
    }

    private List<Notebook> getNotebooks(String where) {
        List<Notebook> notebooksList = new ArrayList<>();

        String query = "SELECT " + KEY_NOTEBOOK_ID + ","
                + KEY_NOTEBOOK_PARENT + ","
                + KEY_NOTEBOOK_NAME + ","
                + KEY_NOTEBOOK_DESCRIPTION + " FROM "
                + TABLE_NOTEBOOKS
                + where + " ORDER BY "
                + KEY_NOTEBOOK_ID + " DESC ";

        Cursor cursor = null;

        try {
            cursor = getDatabase(false).rawQuery(query, null);

            if(cursor.moveToFirst()) {
                do {
                    Notebook notebook = new Notebook();
                    notebook.setId(Long.parseLong(cursor.getString(0)));
                    notebook.setParent(Long.parseLong(cursor.getString(1)));
                    notebook.setName(cursor.getString(2));
                    notebook.setDescription(cursor.getString(3));

                    notebooksList.add(notebook);
                } while (cursor.moveToNext());
            }
        }
        finally {
            if(cursor != null) {
                cursor.close();
            }
        }

        return notebooksList;
    }

    public List<Note> getAllNotes() {
        return getNotes(" where task = 0 and archive = 0 ");
    }

    public List<Note> getFromNotebook(long id) {
        return getNotes(" where notebook = " + id);
    }

    public List<Notebook> getAllNotebooks() {
        return getNotebooks("");
    }

    public long getNotebookId(String name) {
        return getNotebooks(" where name = " + name).get(0).getId();
    }

    public List<Note> getRemovedNotes() {
        return getNotes(" where task = 0 and archive = 1 ");
    }

    public void remove(long id) {
        getDatabase(false).delete(TABLE_NOTES, KEY_CREATION + " = " + id, null);
    }

    public void removeNotebook(String name) {
        getDatabase(false).delete(TABLE_NOTEBOOKS, KEY_NOTEBOOK_NAME + " = " + name, null);
    }

    public boolean checkNotebook(String name) {
        List<Notebook> list = getAllNotebooks();

        for (Notebook notebook : list) {
            if(notebook.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }
}
