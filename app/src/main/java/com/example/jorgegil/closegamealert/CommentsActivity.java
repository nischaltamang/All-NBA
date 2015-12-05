package com.example.jorgegil.closegamealert;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jorgegil on 11/29/15.
 */
public class CommentsActivity extends AppCompatActivity {

    String gameThreadsUrl = "https://www.reddit.com/r/nba/search.json?sort=new&restrict_sr=on&q=flair%3AGame%2BThread";
    String commentsUrl = "https://www.reddit.com/r/nba/comments/GTID/.json";
    String gameThreadId = "No Game Thread found...";
    String homeTeam = "";
    String awayTeam="";
    ListView listView;
    LinearLayout linlaHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Show loading icon
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activity);

        listView = (ListView) findViewById(R.id.commentsListView);
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);


        setUpToolbar();

        // Get teams abbrev from MainActivity
        Intent intent = getIntent();
        homeTeam = intent.getStringExtra(MainActivity.GAME_THREAD_HOME);
        awayTeam = intent.getStringExtra(MainActivity.GAME_THREAD_AWAY);
        setTitle("GAME THREAD: " + awayTeam + " @ " + homeTeam);

        // Request new reddit game threads
        StringRequest request = new StringRequest(gameThreadsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseGameThreads(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                Log.d("Volley", "gameThreadsUrl error: " + error);
                TextView noThread = (TextView) findViewById(R.id.notFoundTextView);
                noThread.setText("Error loading reddit threads...");
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    public void parseGameThreads(String response) {

        try {

            TeamNames tn = new TeamNames();
            JSONArray r = new JSONObject(response).getJSONObject("data").getJSONArray("children");

            // Finds Game Thread for this match-up
            for (int i = 0; i < 15; i++) {
                if (gameThreadId.equals("No Game Thread found...")) {
                    JSONObject data = r.getJSONObject(i).getJSONObject("data");
                    if (data.getString("title").contains("GAME THREAD")
                            && data.getString("title").contains(tn.getName(homeTeam))
                            && data.getString("title").contains(tn.getName(awayTeam))) {
                        gameThreadId = data.getString("id");
                        break;
                    }
                }
            }

            Log.d("GAMEID", awayTeam + "@" + homeTeam + " id -> " + gameThreadId);

            if (gameThreadId.equals("No Game Thread found...")) {
                TextView noThread = (TextView) findViewById(R.id.notFoundTextView);
                noThread.setText(gameThreadId);
            } else {
                commentsUrl = commentsUrl.replace("GTID", gameThreadId);

                // Requests Game Thread with found id and loads comments
                StringRequest request = new StringRequest(commentsUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadComments(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "No game thread found");
                        TextView noThread = (TextView) findViewById(R.id.notFoundTextView);
                        noThread.setText(gameThreadId);
                    }
                });
                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(request);
            }

        } catch (Exception e) {
            Log.d("JSON", "Error: " + e.getMessage());
        }

    }

    public void loadComments(String response) {

        // Loads comments into list view adapter
        CommentsLoader commentsLoader = new CommentsLoader(response);
        ArrayList<Comment> commentList = commentsLoader.fetchComments();

        listView.setAdapter(new CommentAdapter(getApplicationContext(), commentList));

        // Hide reload icon and show list view
        setProgressBarIndeterminateVisibility(false);
        linlaHeaderProgress.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

}
