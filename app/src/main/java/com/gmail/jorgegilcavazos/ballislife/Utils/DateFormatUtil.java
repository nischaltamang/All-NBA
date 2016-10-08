package com.gmail.jorgegilcavazos.ballislife.Utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods used in various points of the application.
 */
public final class DateFormatUtil {

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
     * Returns a "month/day" formatted date given a "yearmonthday" string.
     */
    public static String formatToolbarDate(String date) {
        return date.substring(4, 6) + "/" + date.substring(6, 8);
    }
}
