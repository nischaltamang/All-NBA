package com.example.jorgegil.closegamealert;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
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
    String awayTeam= "";
    String gameId = "";
    TabLayout tabLayout;
    ListView listView;
    LinearLayout linlaHeaderProgress;
    TextView noThread;
    boolean foundThread = false;

    ArrayList<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activity);


        setUpToolbar();
        setUpTabLayout();


        // Get teams abbrev from MainActivity
        Intent intent = getIntent();
        homeTeam = intent.getStringExtra(MainActivity.GAME_THREAD_HOME);
        awayTeam = intent.getStringExtra(MainActivity.GAME_THREAD_AWAY);
        gameId = intent.getStringExtra(MainActivity.GAME_ID);

        setTitle(awayTeam + " @ " + homeTeam);

        Bundle bundle = new Bundle();
        bundle.putString("homeTeam", homeTeam);
        bundle.putString("awayTeam", awayTeam);
        bundle.putString("gameId", gameId);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), bundle);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:
                Log.d("Comments", "refreshed clicked");
                noThread.setText("");
                if (commentsUrl.contains("GTID"))
                    getGameThreads();
                else
                    parseComments();
                break;
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

    private void setUpTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText("Game Thread"));
        tabLayout.addTab(tabLayout.newTab().setText("Box Score"));
        tabLayout.addTab(tabLayout.newTab().setText("Post Game Thread"));
    }

    private void getGameThreads() {
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
                noThread.setText("Error loading reddit threads...");
                hideLoadingIcon();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public void parseGameThreads(String response) {

        try {

            TeamNames tn = new TeamNames();
            JSONArray r = new JSONObject(response).getJSONObject("data").getJSONArray("children");

            // Finds Game Thread for this match-up
            for (int i = 0; i < 15; i++) {
                if (!foundThread) {
                    JSONObject data = r.getJSONObject(i).getJSONObject("data");
                    if (data.getString("title").contains("GAME THREAD")
                            && data.getString("title").contains(tn.getName(homeTeam))
                            && data.getString("title").contains(tn.getName(awayTeam))) {
                        gameThreadId = data.getString("id");
                        foundThread = true;
                        break;
                    }
                }
            }

            Log.d("GAMEID", awayTeam + "@" + homeTeam + " id -> " + gameThreadId);

            if (gameThreadId.equals("No Game Thread found...")) {
                noThread.setText(gameThreadId);
                hideLoadingIcon();
            } else {
                commentsUrl = commentsUrl.replace("GTID", gameThreadId);
                parseComments();
            }

        } catch (Exception e) {
            Log.d("JSON", "Error: " + e.getMessage());
            noThread.setText("Error parsing game threads...");
            hideLoadingIcon();
        }

    }

    public void parseComments() {

        // Show loading icon and hide list view
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);

        // Requests Game Thread with found id and loads comments
        StringRequest request = new StringRequest(commentsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadComments(response);
                Log.e("Comments", "json c: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", "No game thread found");
                noThread.setText(gameThreadId);
                hideLoadingIcon();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public void loadComments(String response) {

        // Loads comments into list view adapter
        CommentsLoader commentsLoader = new CommentsLoader(response);
        commentList = commentsLoader.fetchComments();

        listView.setAdapter(new CommentAdapter(getApplicationContext(), commentList));

        // Hide reload icon and show list view
        setProgressBarIndeterminateVisibility(false);
        linlaHeaderProgress.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }


    public void addComment() {
        Comment comment = new Comment();
        comment.text = "Nuevo comentario";
        comment.author = "jorgegil96";
        comment.points = "5";
        comment.postedOn = "just now";
        comment.level = 0;

        commentList.add(0, comment);

        int lastViewedPosition = listView.getFirstVisiblePosition();

        ((CommentAdapter) listView.getAdapter()).notifyDataSetChanged();

        listView.setSelection(lastViewedPosition + 1);
        Log.d("adapter", "added new comment");
    }

    private void hideLoadingIcon() {
        setProgressBarIndeterminateVisibility(false);
        linlaHeaderProgress.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

}
