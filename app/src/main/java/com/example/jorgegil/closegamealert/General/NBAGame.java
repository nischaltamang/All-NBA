package com.example.jorgegil.closegamealert.General;

/**
 * Class that hold general information about a specific NBA game.
 */
public class NBAGame {
    public static final String PRE_GAME = "1";
    public static final String IN_GAME = "2";
    public static final String POST_GAME = "3";

    private String id;
    private String date;
    private String time;
    private String arena;
    private String city;
    private String periodValue;
    private String periodStatus;
    private String gameStatus;
    private String gameClock;
    private String totalPeriods;
    private String periodName;

    private String homeTeamId;
    private String homeTeamKey;
    private String homeTeamCity;
    private String homeTeamAbbr;
    private String homeTeamNickname;
    private String homeTeamScore;

    private String awayTeamId;
    private String awayTeamKey;
    private String awayTeamCity;
    private String awayTeamAbbr;
    private String awayTeamNickname;
    private String awayTeamScore;


    public String toString() {
        return homeTeamAbbr + "(" + homeTeamScore + ") " + awayTeamAbbr + "(" + awayTeamScore + ")";
    }


    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getArena() {
        return arena;
    }

    public String getCity() {
        return city;
    }

    public String getPeriodValue() {
        return periodValue;
    }

    public String getPeriodStatus() {
        return periodStatus;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public String getGameClock() {
        return gameClock;
    }

    public String getTotalPeriods() {
        return totalPeriods;
    }

    public String getPeriodName() {
        return periodName;
    }

    public String getHomeTeamId() {
        return homeTeamId;
    }

    public String getHomeTeamKey() {
        return homeTeamKey;
    }

    public String getHomeTeamCity() {
        return homeTeamCity;
    }

    public String getHomeTeamAbbr() {
        return homeTeamAbbr;
    }

    public String getHomeTeamNickname() {
        return homeTeamNickname;
    }

    public String getHomeTeamScore() {
        return homeTeamScore;
    }

    public String getAwayTeamId() {
        return awayTeamId;
    }

    public String getAwayTeamKey() {
        return awayTeamKey;
    }

    public String getAwayTeamCity() {
        return awayTeamCity;
    }

    public String getAwayTeamAbbr() {
        return awayTeamAbbr;
    }

    public String getAwayTeamNickname() {
        return awayTeamNickname;
    }

    public String getAwayTeamScore() {
        return awayTeamScore;
    }
}
