package com.gmail.jorgegilcavazos.ballislife.features.model;

public class GameThreadSummary {

    private String id;
    private String title;
    private long created_utc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreated_utc() {
        return created_utc;
    }

    public void setCreated_utc(long created_utc) {
        this.created_utc = created_utc;
    }

    public String toString() {
        return "Id: " + id + " , " + title + " - " + created_utc;
    }
}
