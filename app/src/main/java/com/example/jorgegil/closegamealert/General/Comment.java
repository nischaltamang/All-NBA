package com.example.jorgegil.closegamealert.General;

/**
 * Created by jorgegil on 11/29/15.
 */

public class Comment {
    public net.dean.jraw.models.Comment jrawComment;
    public int level;

    public Comment(net.dean.jraw.models.Comment jrawComment, int depth) {
        this.jrawComment = jrawComment;
        level = depth - 1;
    }
}