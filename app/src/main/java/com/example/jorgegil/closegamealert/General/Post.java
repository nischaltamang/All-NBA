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
    public int score;
    public int numOfComments;
    public String thumbnail;
    public double created;
    public boolean isSelf;

    public Post(String subreddit, String title, String author, String url, String id, int score,
                 int numOfComments, String thumbnail, double created, boolean isSelf) {
        this.subreddit = subreddit;
        this.title = title;
        this.author = author;
        this.url = url;
        this.id = id;
        this.score = score;
        this.numOfComments = numOfComments;
        this.thumbnail = thumbnail;
        this.created = created;
        this.isSelf = isSelf;
    }

    public String getDetails() {
        return author + " posted this and got " + numOfComments + " replies.";
    }

    public String getSubreddit() {
        return subreddit;
    }

    public String getTitle() {
        return title;
    }

    public String getScore() {
        return Integer.toString(score);
    }

    public String getAuthor() {
        return author;
    }
}
