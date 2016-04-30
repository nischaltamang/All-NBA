package com.example.jorgegil.closegamealert.Utils;

import android.util.Log;
import android.widget.Toast;

import com.example.jorgegil.closegamealert.General.Post;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by TOSHIBA on 12/25/2015.
 */
public class PostsLoader {
    private String raw;
    private String filter;

    public PostsLoader(String raw, String filter) {
        this.raw = raw;
        this.filter = filter;
    }


    public ArrayList<Post> fetchPosts() {
        ArrayList<Post> posts = new ArrayList<>();
        try {
            String title, author, subreddit, id, thumbnail, url, domain, ext_thumbnail, link_flair;
            int score, numOfComments;
            String created;
            boolean isSelf;
            JSONArray arr = new JSONObject(raw).getJSONObject("data").getJSONArray("children");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject data = arr.getJSONObject(i).getJSONObject("data");
                title = data.getString("title");
                author = data.getString("author");
                subreddit = data.getString("subreddit");
                id = data.getString("id");
                thumbnail = data.getString("thumbnail");
                ext_thumbnail = "";
                score = data.getInt("score");
                url = data.getString("url");
                created = getDate((long) data.getDouble("created_utc") * 1000);
                isSelf = data.getBoolean("is_self");
                numOfComments = data.getInt("num_comments");
                if (data.get("link_flair_text") != JSONObject.NULL) {
                    link_flair = data.getString("link_flair_text");
                }else {
                    link_flair = "";
                }

                domain = data.getString("domain");
                Object media = data.get("media");
                if (media != JSONObject.NULL) {
                    ext_thumbnail = data.getJSONObject("media").getJSONObject("oembed").getString("thumbnail_url");
                }

                Post post;
                if (filter.equals("Highlights")) {
                    if (link_flair.equals(filter)) {
                        post = new Post(subreddit, title, author, url, id, score, numOfComments,
                                thumbnail, ext_thumbnail, created, isSelf, domain, link_flair);
                        posts.add(post);
                    }
                } else {
                    post = new Post(subreddit, title, author, url, id, score, numOfComments,
                            thumbnail, ext_thumbnail, created, isSelf, domain, link_flair);
                    posts.add(post);
                }


            }
        } catch (Exception e) {
            Log.d("POSTS", "Error parsing JSON: " + e.toString());
        }

        return posts;
    }

    private String getDate(long l) {
        String postedOn = "";
        try {
            Date date = new Date(l);
            String format = "EEE MMM dd hh:mm:ss zzz yyyy";
            Date past = new SimpleDateFormat(format, Locale.ENGLISH).parse(date.toString());
            Date now = new Date();

            long minutesAgo = (TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()));
            long hoursAgo = (TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()));
            long daysAgo = (TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()));

            if (minutesAgo == 0) {
                postedOn = " just now ";
            } else if (minutesAgo < 60) {
                postedOn = minutesAgo + " minutes ago";
            } else {
                if (hoursAgo < 49) {
                    postedOn = hoursAgo + " hours ago";
                } else {
                    postedOn = daysAgo + " days ago";
                }
            }
        } catch (Exception e) {
            Log.e("PostLoader", "date exception: " + e.toString());
        }
        return postedOn;
    }
}
