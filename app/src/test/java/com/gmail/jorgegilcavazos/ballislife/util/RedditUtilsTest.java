package com.gmail.jorgegilcavazos.ballislife.util;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gmail.jorgegilcavazos.ballislife.BuildConfig;
import com.gmail.jorgegilcavazos.ballislife.features.model.GameThreadSummary;

import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

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
    public void testFindGameThreadId() {
        // Create fake threads with different titles.
        List<GameThreadSummary> gameThreadList = new ArrayList<>();
        gameThreadList.add(makeFakeGameThreadSummary("id0", "Post Game Thread: Los Angeles Clippers @ Hawks", 0));
        gameThreadList.add(makeFakeGameThreadSummary("id1", "[Post Game Thread] Los Angeles Lakers @ San Antonio Spurs", 0));
        gameThreadList.add(makeFakeGameThreadSummary("id2", "Game Thread: Los Angeles Clippers @ Hawks", 0));
        gameThreadList.add(makeFakeGameThreadSummary("id3", "Game Thread: Los Angeles Lakers @ San Antonio Spurs", 0));
        gameThreadList.add(makeFakeGameThreadSummary("id4", "Game Thread: Cleveland Cavaliers @ Houston Rockets", 0));
        gameThreadList.add(makeFakeGameThreadSummary("id5", "Game Thread: Bulls @ Warriors", 0));
        gameThreadList.add(makeFakeGameThreadSummary("id6", "Game Thread: Thunder @ Sacramento Kings", 0));
        gameThreadList.add(makeFakeGameThreadSummary("id7", "[POST GAME THREAD] Cleveland Cavaliers @ Houston Rockets", 0));
        gameThreadList.add(makeFakeGameThreadSummary("id8", "[POST-GAME THREAD] Bulls @ Warriors", 0));
        gameThreadList.add(makeFakeGameThreadSummary("id9", "Post-Game Thread: Thunder @ Sacramento Kings", 0));


        String id0 = RedditUtils.findGameThreadId(gameThreadList,
                RedditUtils.POST_GT_TYPE, "ATL", "LAC");
        String id1 = RedditUtils.findGameThreadId(gameThreadList,
                RedditUtils.POST_GT_TYPE, "SAS", "LAL");
        String id2 = RedditUtils.findGameThreadId(gameThreadList,
                RedditUtils.LIVE_GT_TYPE, "ATL", "LAC");
        String id3 = RedditUtils.findGameThreadId(gameThreadList,
                RedditUtils.LIVE_GT_TYPE, "SAS", "LAL");
        String id4 = RedditUtils.findGameThreadId(gameThreadList,
                RedditUtils.LIVE_GT_TYPE, "HOU", "CLE");
        String id5 = RedditUtils.findGameThreadId(gameThreadList,
                RedditUtils.LIVE_GT_TYPE, "GSW", "CHI");
        String id6 = RedditUtils.findGameThreadId(gameThreadList,
                RedditUtils.LIVE_GT_TYPE, "SAC", "OKC");
        String id7 = RedditUtils.findGameThreadId(gameThreadList,
                RedditUtils.POST_GT_TYPE, "HOU", "CLE");
        String id8 = RedditUtils.findGameThreadId(gameThreadList,
                RedditUtils.POST_GT_TYPE, "GSW", "CHI");
        String id9 = RedditUtils.findGameThreadId(gameThreadList,
                RedditUtils.POST_GT_TYPE, "SAC", "OKC");
        String id10 = RedditUtils.findGameThreadId(gameThreadList,
                RedditUtils.POST_GT_TYPE, "DAL", "NYK");

        assertEquals("id0", id0);
        assertEquals("id1", id1);
        assertEquals("id2", id2);
        assertEquals("id3", id3);
        assertEquals("id4", id4);
        assertEquals("id5", id5);
        assertEquals("id6", id6);
        assertEquals("id7", id7);
        assertEquals("id8", id8);
        assertEquals("id9", id9);
        assertEquals("", id10);
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

        /*
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
        */
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

    private GameThreadSummary makeFakeGameThreadSummary(String id, String title, long createdUtc) {
        GameThreadSummary gameThreadSummary = new GameThreadSummary();
        gameThreadSummary.setId(id);
        gameThreadSummary.setTitle(title);
        gameThreadSummary.setCreated_utc(createdUtc);
        return gameThreadSummary;
    }
}
