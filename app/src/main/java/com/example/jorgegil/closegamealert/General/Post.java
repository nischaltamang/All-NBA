package com.example.jorgegil.closegamealert.General;

/**
 * Created by jorgegil on 11/29/15.
 */
public class Post {

    public String subreddit;
    public String title;
    public String author;
    public String url;
    public String domain;
    public String id;
    public String permalink;
    public int points;
    public int numComments;


    public String getDetails() {
        return author + " posted this and got " + numComments + " replies.";
    }

    public String getSubreddit() {
        return subreddit;
    }

    public String getTitle() {
        return title;
    }

    public String getScore() {
        return Integer.toString(points);
    }
}
