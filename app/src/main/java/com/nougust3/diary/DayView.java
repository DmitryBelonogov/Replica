package com.nougust3.diary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DayView extends Activity {

    private TextView dateView;
    private TextView yearView;
    private EditText editText;
    private ImageButton button;
    private ImageButton doneBtn;

    private int year = 0;
    private int month = 0;
    private int day = 0;
    private int id = 0;

    private boolean isNew = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_view);

        dateView = (TextView) findViewById(R.id.date);
        editText = (EditText) findViewById(R.id.editext);
        button = (ImageButton) findViewById(R.id.button);
        doneBtn = (ImageButton) findViewById(R.id.doneButton);
        yearView = (TextView) findViewById(R.id.year);
        dateView.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "RobotoCondensed-Light.ttf"));
        yearView.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "RobotoCondensed-Light.ttf"));

        doneBtn.setVisibility(View.GONE);

        Intent intent = getIntent();

        year = intent.getIntExtra("Year", 0);
        month = intent.getIntExtra("Month", 0);
        day = intent.getIntExtra("Day", 0);

      //  Log.i("Diary", "Year " + year + " month " + month + " day " + day);


        DBOpenHelper dbOpenHelper = new DBOpenHelper(DayView.this);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

       // Cursor cursor = db.query("notes", new String[] {"year", "month", "day", "text"}, "year = ?", year,
                //"month = ?", month, "day = ?", day, null, null);

                //"YEAR", new String[] {Integer.toString(year)},
                //"MONTH", new String[] {Integer.toString(mouth)},
                //        "DAY", Integer.toString(day), null);

        Cursor cursor = db.rawQuery("select * from notes where year = ? and month = ? and day = ?", new String[] { Integer.toString(year), Integer.toString(month), Integer.toString(day) });

        setDate(year, month, day);
        cursor.moveToFirst();
        if(cursor.getCount() != 0) {
            isNew = false;
           // id = cursor.getInt(cursor.getColumnIndex("id"));
           // Log.i("ID", "Id " + id);
            editText.setText(cursor.getString(cursor.getColumnIndex("text")));
            setEditMode(false);
        }
    else
    {
        isNew = true;
        setEditMode(true);
    }
        db.close();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditMode(true);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditMode(false);

                if(editText.getText().length() == 0) {
                    finish();
                    return;
                }

                if(!isNew) {
                    updateNote();
                    finish();
                    return;
                }

                DBOpenHelper dbOpenHelper = new DBOpenHelper(DayView.this);
                SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();

                cv.put(DBOpenHelper.YEAR, year);
                cv.put(DBOpenHelper.MONTH, month);
                cv.put(DBOpenHelper.DAY, day);
                cv.put(DBOpenHelper.TEXT, editText.getText().toString());

                db.insert(DBOpenHelper.TABLE_NAME, null, cv);
                db.close();


                Intent intent = new Intent();

                intent.putExtra("year", year);
                intent.putExtra("month", month);
                intent.putExtra("day", day);
                intent.putExtra("text", editText.getText().toString());

                Toast toast = Toast.makeText(getApplicationContext(), R.string.toast_save, Toast.LENGTH_SHORT);
                toast.show();
                //setResult(1, intent);
                //finish();
            }
        });
    }

    private void updateNote() {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(DayView.this);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("year", year);
        cv.put("month", month);
        cv.put("day", day);
        cv.put("text", editText.getText().toString());
        db.update(DBOpenHelper.TABLE_NAME, cv, "year = ? and month = ? and day = ?", new String[] {
                Integer.toString(year),
                Integer.toString(month),
                Integer.toString(day)
        });
    }

    private void setEditMode(boolean state) {
        if(state) {
            editText.setEnabled(true);
            button.setVisibility(View.GONE);
            doneBtn.setVisibility(View.VISIBLE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        else {
            editText.setEnabled(false);
            doneBtn.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    private void loadNote() {

    }

    private void setDate(int Year, int Month, int Day) {
        Log.i("Diary", Year + " " + Month + " " + Day);
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        dateView.setText(months[Month] + " " + Day);
        yearView.setText(Integer.toString(Year));
    }
}
