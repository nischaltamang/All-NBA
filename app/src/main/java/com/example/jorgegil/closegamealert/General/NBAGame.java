package com.example.jorgegil.closegamealert.General;

/**
 * Class that hold general information about a specific NBA game.
 */
public class NBAGame {
    private String homeTeam;
    private String awayTeam;
    private String homeScore;
    private String awayScore;
    private String clock;
    private int period;
    private String status;
    private String id;

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getHomeScore() {
        return homeScore;
    }

    public String getAwayScore() {
        return awayScore;
    }

    public String getClock() {
        return clock;
    }

    public int getPeriod() {
        return period;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return homeTeam + "(" + homeScore + ") " + awayTeam + "(" + awayScore + ")";
    }
}
