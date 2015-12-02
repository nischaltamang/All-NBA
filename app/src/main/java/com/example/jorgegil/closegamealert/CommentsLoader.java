package com.example.jorgegil.closegamealert;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by jorgegil on 11/29/15.
 */
public class CommentsLoader {

    private String raw;

    CommentsLoader(String raw){
        this.raw = raw;
    }

    // Load various details about the comment
    private Comment loadComment(JSONObject data, int level){
        Comment comment = new Comment();
        try{
            comment.text = data.getString("body");
            comment.author = data.getString("author");
            comment.points = (data.getInt("ups")
                    - data.getInt("downs"))
                    + "";

            Date date = new Date((long)data.getDouble("created_utc") * 1000);

            String format = "EEE MMM dd hh:mm:ss zzz yyyy";
            Date past = new SimpleDateFormat(format, Locale.ENGLISH).parse(date.toString());
            Date now = new Date();

            long minutesAgo = (TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()));
            long hoursAgo = (TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()));
            long daysAgo = (TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()));

            if (minutesAgo == 0) {
                comment.postedOn = " just now ";
            } else if (minutesAgo < 60) {
                comment.postedOn = minutesAgo + " minutes ago";
            } else {
                if (hoursAgo < 49) {
                    comment.postedOn = hoursAgo + " hours ago";
                } else {
                    comment.postedOn = daysAgo + " days ago";
                }
            }

            comment.level=level;
        }catch(Exception e){
            Log.d("ERROR", "Unable to parse comment : " + e);
        }
        return comment;
    }

    // This is where the comment is actually loaded
    // For each comment, its replies are recursively loaded
    private void process(ArrayList<Comment> comments
            , JSONArray c, int level)
            throws Exception {
        for(int i=0;i<c.length();i++){
            if(c.getJSONObject(i).optString("kind")==null)
                continue;
            if(c.getJSONObject(i).optString("kind").equals("t1")==false)
                continue;
            JSONObject data=c.getJSONObject(i).getJSONObject("data");
            Comment comment=loadComment(data,level);
            if(comment.author!=null) {
                comments.add(comment);
                addReplies(comments,data,level+1);
            }
        }
    }

    // Add replies to the comments
    private void addReplies(ArrayList<Comment> comments,
                            JSONObject parent, int level){
        try{
            if(parent.get("replies").equals("")){
                // This means the comment has no replies
                return;
            }
            JSONArray r=parent.getJSONObject("replies")
                    .getJSONObject("data")
                    .getJSONArray("children");
            process(comments, r, level);
        }catch(Exception e){
            Log.d("ERROR","addReplies : "+e);
        }
    }

    // Load the comments as an ArrayList, so that it can be
    // easily passed to the ArrayAdapter
    ArrayList<Comment> fetchComments(){
        ArrayList<Comment> comments = new ArrayList<Comment>();
        try{

            JSONArray r = new JSONArray(raw)
                    .getJSONObject(1)
                    .getJSONObject("data")
                    .getJSONArray("children");

            // All comments at this point are at level 0
            // (i.e., they are not replies)
            process(comments, r, 0);

        }catch(Exception e){
            Log.d("ERROR","Could not connect: "+e);
        }
        return comments;
    }

}
