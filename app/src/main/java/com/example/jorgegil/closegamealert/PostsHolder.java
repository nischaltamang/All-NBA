package com.example.jorgegil.closegamealert;

import android.content.Context;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorgegil on 11/29/15.
 */
public class PostsHolder {

    Context context;
    private final String URL_TEMPLATE =
            "http://www.reddit.com/r/SUBREDDIT_NAME/" + ".json" + "?after=AFTER";


    String subreddit;
    String url;
    String after;

    PostsHolder(String subreddit, Context context) {
        this.context = context;
        this.subreddit = subreddit;
        after = "";
        generateURL();
    }

    private void generateURL() {
        url = URL_TEMPLATE.replace("SUBREDDIT_NAME", subreddit);
        url = url.replace("AFTER", after);
    }

    List<Post> fetchPosts() {
        RemoteData remoteData = new RemoteData(context);
        String raw = remoteData.readContents(url);
        List<Post> list = new ArrayList<Post>();

        try {
            JSONObject data = new JSONObject(raw).getJSONObject("data");
            JSONArray children = data.getJSONArray("children");
            after = data.getString("after");

            for (int i = 0; i < children.length(); i++) {
                JSONObject cur = children.getJSONObject(i).getJSONObject("data");

                Post p = new Post();
                p.title = cur.optString("title");
                p.url=cur.optString("url");
                p.numComments=cur.optInt("num_comments");
                p.points=cur.optInt("score");
                p.author=cur.optString("author");
                p.subreddit=cur.optString("subreddit");
                p.permalink=cur.optString("permalink");
                p.domain=cur.optString("domain");
                p.id=cur.optString("id");

                if (p.title != null) {
                    list.add(p);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    List<Post> fetchMorePosts(){
        generateURL();
        return fetchPosts();
    }
}
