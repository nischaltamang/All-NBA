package com.example.jorgegil.closegamealert;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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


public class ThreadFragment extends Fragment {

    String gameThreadsUrl = "https://www.reddit.com/r/nba/search.json?sort=new&restrict_sr=on&q=flair%3AGame%2BThread";
    String commentsUrl = "https://www.reddit.com/r/nba/comments/GTID/.json";
    String gameThreadId = "No Game Thread found...";
    String homeTeam = "";
    String awayTeam="";
    ListView listView;
    LinearLayout linlaHeaderProgress;
    TextView noThread;
    boolean foundThread = false;

    ArrayList<Comment> commentList;

    public ThreadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setProgressBarIndeterminateVisibility(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thread, container, false);


        // Show loading icon and hide list view
        listView = (ListView) view.findViewById(R.id.commentsListView);
        linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);

        homeTeam = getArguments().getString("homeTeam");
        awayTeam = getArguments().getString("awayTeam");

        noThread = (TextView) view.findViewById(R.id.notFoundTextView);

        getGameThreads();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });


        return view;
    }

    public void getGameThreads() {
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
        RequestQueue queue = Volley.newRequestQueue(getActivity());
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
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    public void loadComments(String response) {

        // Loads comments into list view adapter
        CommentsLoader commentsLoader = new CommentsLoader(response);
        commentList = commentsLoader.fetchComments();

        listView.setAdapter(new CommentAdapter(getActivity(), commentList));

        // Hide reload icon and show list view
        getActivity().setProgressBarIndeterminateVisibility(false);
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
        getActivity().setProgressBarIndeterminateVisibility(false);
        linlaHeaderProgress.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }
}
