package com.gmail.jorgegilcavazos.ballislife.Utils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gmail.jorgegilcavazos.ballislife.BuildConfig;

import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RedditUtilsTest {

    @Test
    public void testParseNbaFlair(){
        String actual = RedditUtils
                .parseNbaFlair("Flair {cssClass='Celtics1', text='The Truth'}");
        String expected = "The Truth";

        assertEquals(expected, actual);
    }

    @Test
    public void testParseNbaFlair_Empty(){
        String actual = RedditUtils.parseNbaFlair(null);
        String expected = "";

        assertEquals(expected, actual);
    }

    @Test
    public void testParseNbaFlair_Invalid(){
        String actual = RedditUtils.parseNbaFlair("Flair {cssClass='Spurs'}");
        String expected = "";

        assertEquals(expected, actual);
    }

    @Test
    public void testFindNbaGameThreadId() {
        final JsonNodeFactory nodeFactory = JsonNodeFactory.instance;

        // Create and add fake Submissions with different titles.
        ArrayNode children = nodeFactory.arrayNode();
        children.add(makeNewThread(nodeFactory, "Post Game Thread: Los Angeles Clippers @ Hawks", 0));
        children.add(makeNewThread(nodeFactory, "[Post Game Thread] Los Angeles Lakers @ San Antonio Spurs", 1));
        children.add(makeNewThread(nodeFactory, "Game Thread: Los Angeles Clippers @ Hawks", 2));
        children.add(makeNewThread(nodeFactory, "Game Thread: Los Angeles Lakers @ San Antonio Spurs", 3));
        children.add(makeNewThread(nodeFactory, "Game Thread: Cleveland Cavaliers @ Houston Rockets", 4));
        children.add(makeNewThread(nodeFactory, "Game Thread: Bulls @ Warriors", 5));
        children.add(makeNewThread(nodeFactory, "Game Thread: Thunder @ Sacramento Kings", 6));
        children.add(makeNewThread(nodeFactory, "[POST GAME THREAD] Cleveland Cavaliers @ Houston Rockets", 7));
        children.add(makeNewThread(nodeFactory, "[POST-GAME THREAD] Bulls @ Warriors", 8));
        children.add(makeNewThread(nodeFactory, "Post-Game Thread: Thunder @ Sacramento Kings", 9));
        ObjectNode submissionsNode = nodeFactory.objectNode();
        submissionsNode.set("children", children);

        // Create a Listing of submissions using the fake ones created above.
        Listing<Submission> submissions = new Listing<>(submissionsNode, Submission.class);

        String id0 = RedditUtils.findNbaGameThreadId(submissions,
                RedditUtils.GameThreadType.POST_GAME_THREAD, "ATL", "LAC");
        String id1 = RedditUtils.findNbaGameThreadId(submissions,
                RedditUtils.GameThreadType.POST_GAME_THREAD, "SAS", "LAL");
        String id2 = RedditUtils.findNbaGameThreadId(submissions,
                RedditUtils.GameThreadType.LIVE_GAME_THREAD, "ATL", "LAC");
        String id3 = RedditUtils.findNbaGameThreadId(submissions,
                RedditUtils.GameThreadType.LIVE_GAME_THREAD, "SAS", "LAL");
        String id4 = RedditUtils.findNbaGameThreadId(submissions,
                RedditUtils.GameThreadType.LIVE_GAME_THREAD, "HOU", "CLE");
        String id5 = RedditUtils.findNbaGameThreadId(submissions,
                RedditUtils.GameThreadType.LIVE_GAME_THREAD, "GSW", "CHI");
        String id6 = RedditUtils.findNbaGameThreadId(submissions,
                RedditUtils.GameThreadType.LIVE_GAME_THREAD, "SAC", "OKC");
        String id7 = RedditUtils.findNbaGameThreadId(submissions,
                RedditUtils.GameThreadType.POST_GAME_THREAD, "HOU", "CLE");
        String id8 = RedditUtils.findNbaGameThreadId(submissions,
                RedditUtils.GameThreadType.POST_GAME_THREAD, "GSW", "CHI");
        String id9 = RedditUtils.findNbaGameThreadId(submissions,
                RedditUtils.GameThreadType.POST_GAME_THREAD, "SAC", "OKC");
        String id10 = RedditUtils.findNbaGameThreadId(submissions,
                RedditUtils.GameThreadType.POST_GAME_THREAD, "DAL", "NYK");

        assertEquals("0", id0);
        assertEquals("1", id1);
        assertEquals("2", id2);
        assertEquals("3", id3);
        assertEquals("4", id4);
        assertEquals("5", id5);
        assertEquals("6", id6);
        assertEquals("7", id7);
        assertEquals("8", id8);
        assertEquals("9", id9);
        assertEquals(null, id10);
    }

    @Test
    public void testTitleContainsTeam_FullName() {
        boolean isInTitle = RedditUtils.titleContainsTeam(
                "Game Thread: Los Angeles Lakers @ San Antonio Spurs", "San Antonio Spurs");
        assertTrue(isInTitle);
    }

    @Test
    public void testTitleContainsTeam_ShortName() {
        boolean isInTitle = RedditUtils.titleContainsTeam(
                "Game Thread: Los Angeles Lakers @ Spurs", "San Antonio Spurs");
        assertTrue(isInTitle);
    }

    @Test
    public void testTitleContainsTeam_IncompleteName() {
        boolean isInTitle = RedditUtils.titleContainsTeam(
                "Game Thread: Angeles Lakers @ San Antonio Spurs", "Los Angeles Lakers");
        assertTrue(isInTitle);
    }

    /**
     * Returns a new ObjectNode representing a submission object of the given values.
     * It has a "kind" and a "data" field, and the "data" field itself has a "title" and an "id"
     * field.
     */
    private ObjectNode makeNewThread(JsonNodeFactory nodeFactory, String title, int id) {
        ObjectNode threadNode = nodeFactory.objectNode();
        threadNode.put("title", title);
        threadNode.put("id", id);
        ObjectNode child = nodeFactory.objectNode();
        child.put("kind", "t3"); // t3 means of type "submission".
        child.set("data", threadNode);
        return child;
    }
}
