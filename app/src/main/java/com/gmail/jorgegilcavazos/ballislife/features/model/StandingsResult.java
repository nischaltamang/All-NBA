package com.gmail.jorgegilcavazos.ballislife.features.model;

import java.util.List;

public class StandingsResult {

    private List<TeamRecord> eastStandings;
    private List<TeamRecord> westStandings;
    private Exception exception;

    public List<TeamRecord> getEastStandings() {
        return eastStandings;
    }

    public void setEastStandings(List<TeamRecord> eastStandings) {
        this.eastStandings = eastStandings;
    }

    public List<TeamRecord> getWestStandings() {
        return westStandings;
    }

    public void setWestStandings(List<TeamRecord> westStandings) {
        this.westStandings = westStandings;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}