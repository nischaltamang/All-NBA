package com.gmail.jorgegilcavazos.ballislife.Utils;

import com.gmail.jorgegilcavazos.ballislife.BuildConfig;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RedditUtilsTest {

    @Test
    public void testParseRedditNBAFlair(){
        String actual = RedditUtils
                .parseRedditNBAFlair("Flair {cssClass='Celtics1', text='The Truth'}");
        String expected = "The Truth";

        assertEquals(expected, actual);
    }

    @Test
    public void testParseRedditNBAFlair_Empty(){
        String actual = RedditUtils.parseRedditNBAFlair(null);
        String expected = "";

        assertEquals(expected, actual);
    }

    @Test
    public void testParseRedditNBAFlair_Invalid(){
        String actual = RedditUtils.parseRedditNBAFlair("Flair {cssClass='Spurs'}");
        String expected = "";

        assertEquals(expected, actual);
    }
}
