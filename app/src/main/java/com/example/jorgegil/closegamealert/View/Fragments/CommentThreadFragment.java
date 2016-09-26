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

import com.example.jorgegil.closegamealert.General.Comment;
import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.General.TeamName;

import net.dean.jraw.http.SubmissionRequest;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

import java.util.ArrayList;

public class CommentThreadFragment extends Fragment {
    private static final String TAG = "CommentThreadFragment";

    public static final int LIVE_THREAD = 0;
    public static final int POST_THREAD = 1;
    public static final String HOME_TEAM_KEY = "HOME_TEAM";
    public static final String AWAY_TEAM_KEY = "AWAY_TEAM";
    public static final String THREAD_TYPE_KEY = "THREAD_TYPE";

    private String homeTeam;
    private String awayTeam;
    private String threadType;

    Context context;

    String threadId;


    boolean foundThread = false;

    ListView listView;
    LinearLayout linlaHeaderProgress;
    TextView noThreadTV;

    FloatingActionButton fab;
    ArrayList<Comment> commentList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            homeTeam = getArguments().getString(HOME_TEAM_KEY);
            awayTeam = getArguments().getString(AWAY_TEAM_KEY);
            threadType = getArguments().getString(THREAD_TYPE_KEY);
        }
        setHasOptionsMenu(true);

        context = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thread, container, false);

        // Show loading icon and hide list view
        listView = (ListView) view.findViewById(R.id.commentsListView);
        linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        showLoadingIcon();

        noThreadTV = (TextView) view.findViewById(R.id.notFoundTextView);
        noThreadTV.setText(getResources().getString(R.string.noThreadFound));
        noThreadTV.setVisibility(View.INVISIBLE);

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

    public void searchInSubmissions(Listing<Submission> submissions) {
        String nameH = TeamName.valueOf(homeTeam.toUpperCase()).getTeamName().toUpperCase();
        String nameA = TeamName.valueOf(awayTeam.toUpperCase()).getTeamName().toUpperCase();

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
            noThreadTV.setVisibility(View.VISIBLE);
            hideLoadingIcon();
        } else {
            noThreadTV.setVisibility(View.INVISIBLE);
            GetFullThread task = new GetFullThread();
            task.execute(threadId);
        }
    }

    private class GetFullThread extends AsyncTask<String, Void, Submission> {
        @Override
        protected void onPreExecute() {
            showLoadingIcon();
        }

        @Override
        protected Submission doInBackground(String... strings) {
            SubmissionRequest.Builder b = new SubmissionRequest.Builder(strings[0]);
            if (threadType.equals("LIVE")) {
                b.sort(CommentSort.NEW);
            } else {
                b.sort(CommentSort.TOP);
            }
            SubmissionRequest sr = b.build();

            //return redditClient.getSubmission(sr);
            return null;
        }

        @Override
        protected void onPostExecute(Submission submission) {
            getComments(submission);
        }
    }

    public void getComments(Submission submission) {
        if (getActivity() != null) {

            Iterable<CommentNode> iterable = submission.getComments().walkTree();
            commentList = new ArrayList<>();
            for (CommentNode node : iterable) {
                commentList.add(new Comment(node.getComment(), node.getDepth()));
            }


            // Hide reload icon and show list view
            hideLoadingIcon();

            fab.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoadingIcon() {
        if(getActivity() != null) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            linlaHeaderProgress.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void showLoadingIcon() {
        if(getActivity() != null) {
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("DESTROY", "View of CommentThreadFragment destroyed");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (foundThread) {
                    GetFullThread task = new GetFullThread();
                    task.execute(threadId);
                } else {
                    //getThreads();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

