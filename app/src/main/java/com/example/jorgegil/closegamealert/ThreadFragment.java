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

    String threadUrl;
    String commentsUrl = "https://www.reddit.com/r/nba/comments/GTID/.json";
    String threadId = "No Thread found...";
    String homeTeam;
    String awayTeam;
    String threadType;
    ListView listView;
    LinearLayout linlaHeaderProgress;
    TextView noThread;
    boolean foundThread = false;

    FloatingActionButton fab;
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
        threadUrl = getArguments().getString("threadUrl");
        threadType = getArguments().getString("threadType");

        noThread = (TextView) view.findViewById(R.id.notFoundTextView);

        getGameThreads();

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });
        fab.setVisibility(View.INVISIBLE);


        return view;
    }

    public void getGameThreads() {
        // Request new reddit game threads
        StringRequest request = new StringRequest(threadUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseGameThreads(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                Log.d("Volley", "threadUrl error: " + error);
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

            // Finds Thread for this match-up
            for (int i = 0; i < 15; i++) {
                if (!foundThread) {
                    JSONObject data = r.getJSONObject(i).getJSONObject("data");

                    String title = data.getString("title").toUpperCase();
                    String nameH = tn.getName(homeTeam).toUpperCase();
                    String nameA = tn.getName(awayTeam).toUpperCase();

                    if (threadType.equals("LIVE")) {
                        if (title.contains("GAME THREAD")
                                && title.contains(nameH)
                                && title.contains(nameA)) {
                            threadId = data.getString("id");
                            foundThread = true;
                            break;
                        }
                    } else {
                        if (title.contains("POST") && title.contains("GAME") && title.contains("THREAD")
                                && title.contains(nameH.substring(nameH.lastIndexOf(' ') + 1))
                                && title.contains(nameA.substring(nameA.lastIndexOf(' ') + 1))) {
                            threadId = data.getString("id");
                            foundThread = true;
                            break;
                        }
                    }
                }
            }

            Log.d("GAMEID", awayTeam + "@" + homeTeam + " id -> " + threadId);

            if (!foundThread) {
                noThread.setText(threadId);
                hideLoadingIcon();
            } else {
                commentsUrl = commentsUrl.replace("GTID", threadId);
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", "No thread found");
                noThread.setText(threadId);
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

        fab.setVisibility(View.VISIBLE);
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
