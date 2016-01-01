package com.example.jorgegil.closegamealert.Utils;

import android.util.Log;
import android.widget.Toast;

import com.example.jorgegil.closegamealert.General.Post;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TOSHIBA on 12/25/2015.
 */
public class PostsLoader {
    private String raw;

    public PostsLoader(String raw) {
        this.raw = raw;
    }

    public ArrayList<Post> fetchPosts() {
        ArrayList<Post> posts = new ArrayList<>();
        try {
            String title, author, subreddit, id, thumbnail, url, domain, ext_thumbnail;
            int score, numOfComments;
            double created;
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
                created = data.getDouble("created");
                isSelf = data.getBoolean("is_self");
                numOfComments = data.getInt("num_comments");

                domain = data.getString("domain");
                Object media = data.get("media");
                if (media != JSONObject.NULL) {
                    ext_thumbnail = data.getJSONObject("media").getJSONObject("oembed").getString("thumbnail_url");
                    Log.d("LOADER", "ext_thumbnail: " + ext_thumbnail);
                }

                Post post = new Post(subreddit, title, author, url, id, score, numOfComments,
                         thumbnail, ext_thumbnail, created, isSelf, domain);

                posts.add(post);
            }
        } catch (Exception e) {
            Log.d("POSTS", "Error parsing JSON: " + e.toString());
        }

        return posts;
    }
}
