package com.gmail.jorgegilcavazos.ballislife.Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods used in various points of the application.
 */
public final class DateFormatUtil {
    private static final String TAG = "DateFormatUtil";

    /**
     * Receives a Date object and returns a human-readable string, e.g. "5 minutes ago".
     */
    public static String formatRedditDate(Date date) {
        String postedOn;
        Date now = new Date();
        long minutesAgo = (TimeUnit.MILLISECONDS.toMinutes(now.getTime() - date.getTime()));
        long hoursAgo = (TimeUnit.MILLISECONDS.toHours(now.getTime() - date.getTime()));
        long daysAgo = (TimeUnit.MILLISECONDS.toDays(now.getTime() - date.getTime()));

        if (minutesAgo == 0) {
            postedOn = " just now ";
        } else if (minutesAgo < 60) {
            postedOn = minutesAgo + " minutes ago";
        } else {
            if (hoursAgo < 49) {
                postedOn = hoursAgo + " hours ago";
            } else {
                postedOn = daysAgo + " days ago";
            }
        }

        return postedOn;
    }

    /**
     * Returns a dash-separated date given a year, month and day, e.g. "2016-03-20".
     */
    public static String formatScoreboardDate(int year, int month, int day) {
        String monthString;
        if (month < 10) {
            monthString = "0" + month;
        } else {
            monthString = String.valueOf(month);
        }

        String dayString;
        if (day < 10) {
            dayString = "0" + day;
        } else {
            dayString = String.valueOf(day);
        }

        return year + "-" + monthString + "-" + dayString;
    }

    /**
     * Returns a "month/day" formatted date given a "yyyymmdd" string, unless the given date is
     * today, in which case the string "Today" is returned.
     */
    public static String formatToolbarDate(String dateString) {
        try {
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.US);
            Date date = format.parse(dateString);
            if (areDatesSameDay(now, date)) {
                return "Today";
            }
        } catch (ParseException e) {
            if (MyDebug.LOG) {
                Log.e(TAG, "Error parsing date: " + e.toString());
            }
        }
        return dateString.substring(4, 6) + "/" + dateString.substring(6, 8);
    }

    public static boolean areDatesSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }
}
