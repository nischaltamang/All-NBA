package com.gmail.jorgegilcavazos.ballislife.util;

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
            postedOn = minutesAgo + "m";
        } else {
            if (hoursAgo < 49) {
                postedOn = hoursAgo + "hr";
            } else {
                postedOn = daysAgo + " days";
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
            if (isDateToday(date)) {
                return "Today";
            }
        } catch (ParseException e) {
            if (MyDebug.LOG) {
                Log.e(TAG, "Error parsing date: " + e.toString());
            }
        }
        return dateString.substring(4, 6) + "/" + dateString.substring(6, 8);
    }

    /**
     * Returns a prettier date for the game date navigator, e.g. "Tuesday, October 25".
     */
    public static String formatNavigatorDate(Date date) {
        if (isDateToday(date)) {
            return "Today";
        } else if(isDateYesterday(date)) {
            return "Yesterday";
        } else if(isDateTomorrow(date)) {
            return "Tomorrow";
        } else {
            SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d", Locale.US);
            return format.format(date);
        }

    }

    public static boolean isDateToday(Date date) {
        Calendar calNow = Calendar.getInstance();
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);
        return areDatesEqual(calNow, calDate);
    }

    public static boolean isDateYesterday(Date date) {
        Calendar calYesterday = Calendar.getInstance();
        calYesterday.add(Calendar.DAY_OF_YEAR, -1);
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);
        return areDatesEqual(calYesterday, calDate);
    }

    public static boolean isDateTomorrow(Date date) {
        Calendar calTomorrow = Calendar.getInstance();
        calTomorrow.add(Calendar.DAY_OF_YEAR, 1);
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);
        return areDatesEqual(calTomorrow, calDate);
    }

    public static boolean areDatesEqual(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }

    public static Date getDateFromString(String dateString, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.US);
            return format.parse(dateString);
        } catch (ParseException e) {
            if (MyDebug.LOG) {
                Log.e(TAG, "Error parsing date: " + e.toString());
            }
        }
        return null;
    }

    public static String getDashedDateString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return format.format(date);
    }
}
