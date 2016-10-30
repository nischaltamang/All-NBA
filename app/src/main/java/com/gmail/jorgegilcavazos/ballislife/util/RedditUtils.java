package com.gmail.jorgegilcavazos.ballislife.util;

import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

public final class RedditUtils {
    private final static String TAG = "RedditUtils";

    public enum GameThreadType {
        LIVE_GAME_THREAD, POST_GAME_THREAD
    }

    /**
     * Parses a given /r/NBA flair into a readable friendly string.
     * @param flair usually formatted as "Flair {cssClass='Celtics1', text='The Truth'}"
     * @return friendly string, e.g. "The Truth", or empty string if flair was null or not valid.
     */
    public static String parseNbaFlair(String flair) {
        final int EXPECTED_SECTIONS = 5;
        if (flair == null) {
            return "";
        }

        String[] sections = flair.split("'");
        if (sections.length == EXPECTED_SECTIONS) {
            return sections[sections.length - 2];
        }
        return "";
    }

    /**
     * Searches through a list of reddit Submissions for the one that corresponds to the desired
     * game and thread type. The desired game is the one where the two given teams are playing.
     * @param submissions - Listing of reddit threads
     * @param threadType - CommentThreadFragment.LIVE_THREAD or CommentThreadFragment.POST_THREAD
     * @param homeTeamAbbrev
     * @param awayTeamAbbrev
     * @return submissionId or null if it was not found
     */
    public static String findNbaGameThreadId(Listing<Submission> submissions,
                                             GameThreadType threadType,
                                             String homeTeamAbbrev,
                                             String awayTeamAbbrev) {
        String homeTeamFullName = null;
        String awayTeamFullName = null;

        for (TeamName teamName : TeamName.values()) {
            if (teamName.toString().equals(homeTeamAbbrev)) {
                homeTeamFullName = teamName.getTeamName();
            }
            if (teamName.toString().equals(awayTeamAbbrev)) {
                awayTeamFullName = teamName.getTeamName();
            }
        }

        if (homeTeamFullName == null || awayTeamFullName == null) {
            return null;
        }

        for (Submission submission : submissions) {
            String capsTitle = submission.getTitle().toUpperCase();

            // Usually formatted as "GAME THREAD: Cleveland Cavaliers @ San Antonio Spurs".
            if (threadType == GameThreadType.LIVE_GAME_THREAD) {
                if (capsTitle.contains("GAME THREAD") && !capsTitle.contains("POST")
                        && titleContainsTeam(capsTitle, homeTeamFullName)
                        && titleContainsTeam(capsTitle, awayTeamFullName)) {

                    return submission.getId();
                }
            }
            // Usually formatted as "POST GAME THREAD: San Antonio Spurs defeat Lakers".
            if (threadType == GameThreadType.POST_GAME_THREAD) {
                if ((capsTitle.contains("POST GAME THREAD")
                        || capsTitle.contains("POST-GAME THREAD"))
                        && titleContainsTeam(capsTitle, homeTeamFullName)
                        && titleContainsTeam(capsTitle, awayTeamFullName)) {
                    return submission.getId();
                }
            }
        }

        return null;
    }

    /**
     * Checks that the title contains at least the team name, e.g "Spurs".
     */
    public static boolean titleContainsTeam(String title, String fullTeamName) {
        String capsTitle = title.toUpperCase();
        String capsTeam = fullTeamName.toUpperCase(); // Ex. "SAN ANTONIO SPURS".
        String capsName = capsTeam.substring(capsTeam.lastIndexOf(" ") + 1); // Ex. "SPURS".
        return capsTitle.contains(capsName);
    }
}
