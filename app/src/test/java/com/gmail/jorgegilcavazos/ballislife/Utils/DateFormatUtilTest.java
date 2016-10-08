package com.gmail.jorgegilcavazos.ballislife.Utils;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Calendar;

/**
 * Class to test {@link DateFormatUtil}.
 */
public class DateFormatUtilTest {

    @Test
    public void testFormateRedditDate() {
        Calendar now = Calendar.getInstance();
        Calendar fiftyMinAgo = Calendar.getInstance();
        fiftyMinAgo.add(Calendar.MINUTE, -50);
        Calendar fourHoursAgo = Calendar.getInstance();
        fourHoursAgo.add(Calendar.HOUR, -4);
        Calendar twoDaysAgo = Calendar.getInstance();
        twoDaysAgo.add(Calendar.HOUR, -60);

        String nowString = DateFormatUtil.formatRedditDate(now.getTime());
        String eightyMinAgoString = DateFormatUtil.formatRedditDate(fiftyMinAgo.getTime());
        String fourHoursAgoString = DateFormatUtil.formatRedditDate(fourHoursAgo.getTime());
        String twoDaysAgoString = DateFormatUtil.formatRedditDate(twoDaysAgo.getTime());

        assertEquals(" just now ", nowString);
        assertEquals("50 minutes ago", eightyMinAgoString);
        assertEquals("4 hours ago", fourHoursAgoString);
        assertEquals("2 days ago", twoDaysAgoString);
    }

    @Test
    public void testFormatScoreBoardDate() {
        String actual = DateFormatUtil.formatScoreboardDate(1999, 12, 30);
        String expected = "1999-12-30";

        assertEquals(expected, actual);
    }

    @Test
    public void testFormatScoreBoardDate_dayAndMonthLessThanTen() {
        String actual = DateFormatUtil.formatScoreboardDate(2016, 9, 3);
        String expected = "2016-09-03";

        assertEquals(expected, actual);
    }

    @Test
    public void testFormatToolbarDate() {
        String actual = DateFormatUtil.formatToolbarDate("20100516");
        String expected = "05/16";

        assertEquals(expected, actual);
    }
}
