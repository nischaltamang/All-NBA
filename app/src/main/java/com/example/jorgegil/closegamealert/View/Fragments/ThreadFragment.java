package com.example.jorgegil.closegamealert.View.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jorgegil.closegamealert.General.Comment;
import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.General.TeamNames;
import com.example.jorgegil.closegamealert.Utils.CommentAdapter;
import com.example.jorgegil.closegamealert.Utils.CommentsLoader;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class ThreadFragment extends Fragment {
    private final String TAG = "ThreadFragment";

    Context context;

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

    UserAgent userAgent;
    RedditClient redditClient;
    Credentials credentials;
    OAuthData oAuthData;


    public ThreadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            homeTeam = getArguments().getString("homeTeam");
            awayTeam = getArguments().getString("awayTeam");
            threadUrl = getArguments().getString("threadUrl");
            Log.d(TAG, "URL: " + threadUrl);
            threadType = getArguments().getString("threadType");
        }

        setHasOptionsMenu(true);

        context = getActivity();
        connectToReddit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thread, container, false);


        // Show loading icon and hide list view
        listView = (ListView) view.findViewById(R.id.commentsListView);
        linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);

        noThread = (TextView) view.findViewById(R.id.notFoundTextView);

        //getGameThreads();
        //getThreads();

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addComment();
            }
        });
        fab.setVisibility(View.INVISIBLE);

        if (getActivity() != null) {
            // Register Broadcast manager to update scores automatically
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("comment-data"));
        }


        return view;
    }

    // When new data is received, the JSON is parsed and the listview is notified of change
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String JSONcomment = intent.getStringExtra("comment");
            Log.d("recevier", "Got comment: " + JSONcomment);
            /*
            try {
                JSONObject jsonObject = new JSONObject(JSONcomment);

                Comment comment = new Comment();
                comment.text = jsonObject.getString("text");
                comment.author = jsonObject.getString("author");
                comment.points = jsonObject.getString("points");
                comment.level = Integer.parseInt(jsonObject.getString("level"));
                comment.postedOn = getDate(jsonObject.getString("postedOn"));
                String commentThreadId = jsonObject.getString("threadId");

                if (commentThreadId.equals(threadId)) {
                    //addComment(comment);
                }

            } catch (Exception e) {
                Log.e("JSON", "add comment e: " + e.toString());
            }
            */
        }
    };

    public void connectToReddit() {
        userAgent = UserAgent.of("mobile", "com.example.jorgegil96.allnba", "v0.1", "jorgegil96");
        redditClient = new RedditClient(userAgent);
        credentials = Credentials.userlessApp("XDtA2eYVKp1wWA", UUID.randomUUID());
        AuthenticateTask task = new AuthenticateTask();
        task.execute();
    }

    private class AuthenticateTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                oAuthData = redditClient.getOAuthHelper().easyAuth(credentials);
                redditClient.authenticate(oAuthData);
                Log.d(TAG, "authenticated!!");
            } catch (OAuthException e) {
                Log.d(TAG, "Error authenticating: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getThreads();
        }
    }

    public void getThreads() {
        GetSubmissionsTask task = new GetSubmissionsTask();
        task.execute();
    }

    private class GetSubmissionsTask extends AsyncTask<Void, Void, Listing<Submission>> {
        @Override
        protected Listing<Submission> doInBackground(Void... voids) {
            if (redditClient.isAuthenticated()) {
                SubredditPaginator paginator = new SubredditPaginator(redditClient, "nba");
                paginator.setLimit(100);
                paginator.setSorting(Sorting.HOT);
                return paginator.next(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Listing<Submission> submissions) {
            searchInSubmissions(submissions);
        }
    }

    public void searchInSubmissions(Listing<Submission> submissions) {
        TeamNames tn = new TeamNames();
        String nameH = tn.getName(homeTeam).toUpperCase();
        String nameA = tn.getName(awayTeam).toUpperCase();

        for (int i = 0; i < submissions.size(); i++) {
            Submission s = submissions.get(i);

            String title = s.getTitle().toUpperCase();
            //Log.d(TAG, title);
            String flair = s.getSubmissionFlair().getText();

            if (threadType.equals("LIVE")) {
                //Log.d(TAG, "TITLE: " + title);
                if (flair != null) {
                    if (flair.equals("Game Thread") && title.contains(nameH) && title.contains(nameA)) {
                        threadId = s.getId();
                        foundThread = true;
                        break;
                    }
                }
            } else {
                //Log.d(TAG, "TITLE: " + title);
                if (title.contains("POST GAME THREAD")
                        && title.contains(nameH.substring(nameH.lastIndexOf(' ') + 1))
                        && title.contains(nameA.substring(nameA.lastIndexOf(' ') + 1))) {
                    threadId = s.getId();
                    foundThread = true;
                    break;
                }
            }
        }

        Log.d(TAG, awayTeam + "@" + homeTeam + " id -> " + threadId);

        if (!foundThread) {
            noThread.setText(threadId);
            hideLoadingIcon();
        } else {
            noThread.setText("");
            commentsUrl = commentsUrl.replace("GTID", threadId);
            GetFullThread task = new GetFullThread();
            task.execute(threadId);
            //parseComments();
        }
    }

    private class GetFullThread extends AsyncTask<String, Void, Submission> {
        @Override
        protected Submission doInBackground(String... strings) {
            Submission submission = redditClient.getSubmission(strings[0]);
            return submission;
        }

        @Override
        protected void onPostExecute(Submission submission) {
            getComments(submission);
        }
    }

    public void getComments(Submission submission) {
        if (getActivity() != null) {
            // Loads comments into list view adapter
            CommentsLoader commentsLoader = new CommentsLoader(submission);
            commentList = commentsLoader.fetchComments();


            listView.setAdapter(new CommentAdapter(getActivity(), commentList));

            // Hide reload icon and show list view
            getActivity().setProgressBarIndeterminateVisibility(false);
            linlaHeaderProgress.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            fab.setVisibility(View.VISIBLE);
        }
    }


    /*
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
            for (int i = 0; i < r.length(); i++) {
                if (!foundThread) {
                    JSONObject data = r.getJSONObject(i).getJSONObject("data");

                    String title = data.getString("title").toUpperCase();
                    String flair = data.getString("link_flair_text");
                    String nameH = tn.getName(homeTeam).toUpperCase();
                    String nameA = tn.getName(awayTeam).toUpperCase();

                    if (threadType.equals("LIVE")) {
                        Log.d("THREAD", "TITLE: " + title);
                        if (flair.equals("Game Thread")
                                && title.contains(nameH)
                                && title.contains(nameA)) {
                            threadId = data.getString("id");
                            foundThread = true;
                            break;
                        }
                    } else {
                        Log.d("THREAD", "TITLE: " + title);
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
    */

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

        if (getActivity() != null) {
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
    }

    /*
    public void addComment(Comment comment) {

        commentList.add(0, comment);
        int lastViewedPosition = listView.getFirstVisiblePosition();
        ((CommentAdapter) listView.getAdapter()).notifyDataSetChanged();
        if (lastViewedPosition != 0) {
            listView.setSelection(lastViewedPosition + 1);
        }
        Log.d("adapter", "added new comment");

    }

    */

    private void hideLoadingIcon() {
        if(getActivity() != null) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            linlaHeaderProgress.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private String getDate(String s) {
        String postedOn = "";
        try {
            Date date = new Date(Integer.parseInt(s) * 1000);
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
            Log.e("ThreadFragment", "date exception: " + e.toString());
        }
        return postedOn;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("DESTROY", "View of ThreadFragment destroyed");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                /*
                if (commentsUrl.contains("GTID"))
                    getGameThreads();
                else
                    parseComments();
                    */
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

