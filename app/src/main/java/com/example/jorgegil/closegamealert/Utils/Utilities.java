package com.example.jorgegil.closegamealert.Utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by jorgegil on 8/20/16.
 */
public final class Utilities {

    public static String formatDate(Date date) {
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
}
