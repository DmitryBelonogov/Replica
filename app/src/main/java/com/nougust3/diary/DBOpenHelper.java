package com.nougust3.diary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final int DB_Version = 1;
    private static final String DB_NAME = "db";

    public static final String TABLE_NAME = "notes";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String TEXT = "text";

    private static final String CREATE_TABLE = "create table " + TABLE_NAME +
            " ( _id integer primary key autoincrement, " +
            YEAR + " YEAR INTEGER, " +
            MONTH + " MONTH INTEGER, " +
            DAY + " DAY INTEGER, " +
            TEXT + " TEXT TEXT)";

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
