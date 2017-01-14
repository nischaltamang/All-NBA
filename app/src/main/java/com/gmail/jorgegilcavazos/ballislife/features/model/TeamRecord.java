package com.gmail.jorgegilcavazos.ballislife.features.model;

public class TeamRecord {

    private int record;
    private String teamName;
    private String wins;
    private String losses;
    private String percentage;
    private String gamesBehind;

    public TeamRecord(int record,
                      String teamName,
                      String wins,
                      String losses,
                      String percentage,
                      String gamesBehind) {
        this.record = record;
        this.teamName = teamName;
        this.wins = wins;
        this.losses = losses;
        this.percentage = percentage;
        this.gamesBehind = gamesBehind;
    }

    public int getRecord() {
        return record;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getWins() {
        return wins;
    }

    public String getLosses() {
        return losses;
    }

    public String getPercentage() {
        return percentage;
    }

    public String getGamesBehind() {
        return gamesBehind;
    }

    public String toString() {
        return record + " " + teamName + " " + wins + "-" + losses;
    }

}