package com.example.jorgegil.closegamealert;

/**
 * Created by jorgegil on 11/29/15.
 */

public class Comment {
    String text;
    String author;
    String points;
    String postedOn;

    // The 'level' field indicates how deep in the hierarchy
    // this comment is. A top-level comment has a level of 0
    // where as a reply has level 1, and reply of a reply has
    // level 2 and so on...
    int level;
}