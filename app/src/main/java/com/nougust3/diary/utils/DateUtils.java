package com.nougust3.diary.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    private static Calendar calendar = Calendar.getInstance();

    public static long getTimeInMillis() {
        if(calendar != null) {
            return Calendar.getInstance().getTimeInMillis();
        }
        else {
            Log.w("KEEP", "DateUtils.calender == null");
        }

        return 0;
    }

    public static String parseDate(long Date) {
        String time;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM HH:mm");

        try {
            calendar.setTimeInMillis(Date);
            time = sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return time;
    }

    public static String format(long Date) {
        String time;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");

        try {
            calendar.setTimeInMillis(Date);
            time = sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return time;
    }
}
