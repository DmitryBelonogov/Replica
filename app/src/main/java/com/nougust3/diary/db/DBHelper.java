package com.nougust3.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nougust3.diary.models.Note;
import com.nougust3.diary.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = Constants.DATABASE_NAME;
    private static final int DATABASE_VERSION = Constants.DATABASE_VERSION;
    private static final String DB_DIR = "db";

    private static final String TABLE_NOTES = "notes";

    private static final String KEY_CREATION = "creation";
    private static final String KEY_MODIFICATION = "modification";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_TASK = "task";
    private static final String KEY_ARCHIVE = "archive";

    private static final String CREATE_QUERY = "create.sql";

    private SQLiteDatabase db = null;

    private final Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("ww", "DBHelper oncreate");
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

        db.insertWithOnConflict(TABLE_NOTES, KEY_CREATION, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.setTransactionSuccessful();
        db.endTransaction();

        return note;
    }

    public Note getNote(long id) {
        String where = " WHERE " + KEY_CREATION + " = " + id;
        List<Note> notes = getNotes(where);

        if(notes.size() > 0) {
            return notes.get(0);
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
                + KEY_ARCHIVE + " FROM "
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

    public List<Note> getAllNotes() {
        List<Note> noteList = getNotes(" where task = 1 and archive = 0 ");
        List<Note> noteList2 = getNotes(" where task = 0 and archive = 0 ");
        noteList.addAll(noteList2);

        return noteList;
    }

    public List<Note> getRemovedNotes() {
        return getNotes(" where task = 0 and archive = 1 ");
    }

    public void remove(long id) {
        //String query = "delete from " + TABLE_NOTES + " where " + KEY_CREATION + " = " + id;
        //getDatabase(false).rawQuery(query, null);
        getDatabase(false).delete(TABLE_NOTES, KEY_CREATION + " = " + id, null);
    }
}
