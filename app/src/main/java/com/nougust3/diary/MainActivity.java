package com.nougust3.diary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.ImageFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.p_v.flexiblecalendar.FlexibleCalendarView;
import com.p_v.flexiblecalendar.entity.Event;
import com.p_v.flexiblecalendar.view.BaseCellView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity {

    private FlexibleCalendarView calendar;
    private ImageButton addButton;
    private Context context;

    private int month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        calendar = (FlexibleCalendarView) findViewById(R.id.calendar);
        addButton = (ImageButton) findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                Intent intent = new Intent(MainActivity.this, DayView.class);

                intent.putExtra("Year", c.get(Calendar.YEAR));
                intent.putExtra("Month", c.get(Calendar.MONTH));
                intent.putExtra("Day", c.get(Calendar.DAY_OF_MONTH));

                //startActivityForResult(intent, 1);
                startActivity(intent);
            }
        });

        createCalendar();
    }

    private void createCalendar() {
        calendar.setDisableAutoDateSelection(true);
        calendar.setMonthViewVerticalSpacing(15);

        calendar.setOnDateClickListener(new FlexibleCalendarView.OnDateClickListener() {
            @Override
            public void onDateClick(int year, int month, int day) {
                Intent intent = new Intent(MainActivity.this, DayView.class);

                intent.putExtra("Year", year);
                intent.putExtra("Month", month);
                intent.putExtra("Day", day);

                startActivityForResult(intent, 1);
            }
        });

        final Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Light.ttf");
        final Typeface typeface2 = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Regular.ttf");

        calendar.setCalendarView(new FlexibleCalendarView.CalendarView() {
            @Override
            public BaseCellView getCellView(int position, View convertView, ViewGroup parent, int cellType) {
                BaseCellView cellView = (BaseCellView) convertView;

                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                    cellView = (BaseCellView) inflater.inflate(R.layout.square_cell_layout, null);
                    cellView.setTextColor(ContextCompat.getColor(context, R.color.colorCalendarText));
                    cellView.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
                    cellView.setTypeface(typeface1);
                    cellView.setTextSize(20);
                }
                if(cellType == BaseCellView.TODAY) {
                    cellView.setTextColor(ContextCompat.getColor(context, R.color.weekDays));
                    cellView.setTextSize(20);
                    cellView.setTypeface(typeface2);
                }

                return cellView;
            }

            @Override
            public BaseCellView getWeekdayCellView(int position, View convertView, ViewGroup parent) {
                BaseCellView cellView = (BaseCellView) convertView;

                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                    cellView = (BaseCellView) inflater.inflate(R.layout.weekday_cell, null);
                    cellView.setTextColor(ContextCompat.getColor(context, R.color.weekDays));
                    cellView.setTextSize(16);
                }

                return cellView;
            }

            @Override
            public String getDayOfWeekDisplayValue(int dayOfWeek, String defaultValue) {
                return null;
            }
        });

        updateCalendar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null) {
            return;
        }

        addNote(data.getIntExtra("year", 0),
                data.getIntExtra("mouth", 0),
                data.getIntExtra("day", 0),
                data.getStringExtra("text"));

        updateCalendar();
    }

    private void addNote(int year, int mouth, int day, String text) {
        return;
    }

    private void updateCalendar() {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(MainActivity.this);
        final SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        final Cursor cursor = db.rawQuery("select * from notes where month = ?", new String[] {Integer.toString(calendar.getCurrentMonth())});


            calendar.setEventDataProvider(new FlexibleCalendarView.EventDataProvider() {
                @Override
                public List<? extends Event> getEventsForTheDay(int year, int month, int day) {
                    List<CustomEvent> notesList = new ArrayList<CustomEvent>();

                    if (cursor.moveToFirst()) {
                        do {
                            if (cursor.getInt(cursor.getColumnIndex("year")) == year &&
                                    cursor.getInt(cursor.getColumnIndex("month")) == month &&
                                    cursor.getInt(cursor.getColumnIndex("day")) == day) {
                                notesList.add(new CustomEvent(R.color.colorEvent));
                            }
                        } while (cursor.moveToNext());
                    }

                    return notesList;
                }
            });

       // cursor.close();
       //    db.close();

    }

    public static class EventW implements Event{
        public EventW(){

        }

        @Override
        public int getColor() {
            return 0;
        }
    }


}


