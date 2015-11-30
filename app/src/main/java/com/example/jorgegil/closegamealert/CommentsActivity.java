package com.example.jorgegil.closegamealert;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * Created by jorgegil on 11/29/15.
 */
public class CommentsActivity extends AppCompatActivity {

    String url = "https://www.reddit.com/r/nba/comments/GTID/.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activity);

        Intent intent = getIntent();
        String gameThreadId = intent.getStringExtra(MainActivity.GAME_THREAD_ID);
        String homeTeam = intent.getStringExtra(MainActivity.GAME_THREAD_HOME);
        String awayTeam = intent.getStringExtra(MainActivity.GAME_THREAD_AWAY);

        setTitle("GAME THREAD: " + awayTeam + " @ " + homeTeam);

        url = url.replace("GTID", gameThreadId);

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadComments(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    public void loadComments(String response) {
        CommentsLoader commentsLoader = new CommentsLoader(response);
        ArrayList<Comment> commentList = commentsLoader.fetchComments();

        ListView listView = (ListView) findViewById(R.id.commentsListView);
        listView.setAdapter(new CommentAdapter(getApplicationContext(), commentList));
        System.out.println("COUNT: " + listView.getCount());
    }


}
