package com.example.jorgegil.closegamealert;

/**
 * Created by jorgegil on 11/29/15.
 */
public class Post {

    String subreddit;
    String title;
    String author;
    String url;
    String domain;
    String id;
    String permalink;
    int points;
    int numComments;


    String getDetails() {
        return author + " posted this and got " + numComments + " replies.";
    }

    String getSubreddit() {
        return subreddit;
    }

    String getTitle() {
        return title;
    }

    String getScore() {
        return Integer.toString(points);
    }
}
