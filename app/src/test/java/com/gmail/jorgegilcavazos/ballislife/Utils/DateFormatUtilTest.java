package com.gmail.jorgegilcavazos.ballislife.Utils;

import com.gmail.jorgegilcavazos.ballislife.BuildConfig;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Class to test {@link DateFormatUtil}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DateFormatUtilTest {

    @Test
    public void testFormatRedditDate() {
        Calendar now = Calendar.getInstance();
        Calendar fiftyMinAgo = Calendar.getInstance();
        fiftyMinAgo.add(Calendar.MINUTE, -50);
        Calendar fourHoursAgo = Calendar.getInstance();
        fourHoursAgo.add(Calendar.HOUR, -4);
        Calendar twoDaysAgo = Calendar.getInstance();
        twoDaysAgo.add(Calendar.HOUR, -60);

        String nowString = DateFormatUtil.formatRedditDate(now.getTime());
        String fiftyMinAgoString = DateFormatUtil.formatRedditDate(fiftyMinAgo.getTime());
        String fourHoursAgoString = DateFormatUtil.formatRedditDate(fourHoursAgo.getTime());
        String twoDaysAgoString = DateFormatUtil.formatRedditDate(twoDaysAgo.getTime());

        assertEquals(" just now ", nowString);
        assertEquals("50 m", fiftyMinAgoString);
        assertEquals("4 hr", fourHoursAgoString);
        assertEquals("2 days", twoDaysAgoString);
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

    @Test
    public void testFormatToolbarDate_Today() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.US);

        String actual = DateFormatUtil.formatToolbarDate(format.format(now.getTime()));
        String expected = "Today";

        assertEquals(expected, actual);
    }

    @Test
    public void testFormatNavigatorDate() {
        String date1 = DateFormatUtil.formatNavigatorDate(Calendar.getInstance().getTime());
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        String date2 = DateFormatUtil.formatNavigatorDate(yesterday.getTime());
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        String date3 = DateFormatUtil.formatNavigatorDate(tomorrow.getTime());
        Calendar someDay = Calendar.getInstance();
        someDay.set(2016, 9, 14);
        String date4 = DateFormatUtil.formatNavigatorDate(someDay.getTime());

        assertEquals("Today", date1);
        assertEquals("Yesterday", date2);
        assertEquals("Tomorrow", date3);
        assertEquals("Friday, October 14", date4);
    }
}
