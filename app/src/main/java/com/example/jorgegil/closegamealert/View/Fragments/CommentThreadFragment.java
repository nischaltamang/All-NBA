package com.example.jorgegil.closegamealert.View.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.jorgegil.closegamealert.Adapter.CommentAdapter;
import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.Utils.RedditAuthentication;

import net.dean.jraw.http.SubmissionRequest;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.Submission;

import java.util.ArrayList;
import java.util.List;

public class CommentThreadFragment extends Fragment {
    private static final String TAG = "CommentThreadFragment";

    public static final int LIVE_THREAD = 0;
    public static final int POST_THREAD = 1;
    public static final String HOME_TEAM_KEY = "HOME_TEAM";
    public static final String AWAY_TEAM_KEY = "AWAY_TEAM";
    public static final String THREAD_TYPE_KEY = "THREAD_TYPE";

    private String homeTeam;
    private String awayTeam;
    private String mThreadId;
    private int mThreadType;

    Context mContext;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    CommentAdapter mCommentAdapter;
    ProgressBar mProgressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            homeTeam = getArguments().getString(HOME_TEAM_KEY);
            awayTeam = getArguments().getString(AWAY_TEAM_KEY);
            mThreadType = getArguments().getInt(THREAD_TYPE_KEY);
        }
        setHasOptionsMenu(true);

        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_thread, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.comment_thread_rv);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mRecyclerView.setNestedScrollingEnabled(false);
        } else {
            ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
        }

        mThreadId = "54s74i";
        fetchComments(mThreadId);
        return view;
    }

    private void fetchComments(String submissionId) {
        new FetchSubmission(submissionId).execute();
    }

    private void loadComments(Submission submission) {
        Iterable<CommentNode> iterable = submission.getComments().walkTree();
        List<CommentNode> commentNodes = new ArrayList<>();
        for (CommentNode node : iterable) {
            commentNodes.add(node);
        }
        mCommentAdapter = new CommentAdapter(mContext, commentNodes);
        mRecyclerView.setAdapter(mCommentAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                fetchComments(mThreadId);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchSubmission extends AsyncTask<Void, Void, Submission> {
        private String mThreadId;

        public FetchSubmission(String threadId) {
            mThreadId = threadId;
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected Submission doInBackground(Void... params) {
            SubmissionRequest.Builder builder = new SubmissionRequest.Builder(mThreadId);
            switch (mThreadType) {
                case LIVE_THREAD:
                    builder.sort(CommentSort.NEW);
                    break;
                case POST_THREAD:
                    builder.sort(CommentSort.TOP);
                    break;
                default:
                    builder.sort(CommentSort.TOP);
                    break;
            }
            SubmissionRequest submissionRequest = builder.build();
            return RedditAuthentication.redditClient.getSubmission(submissionRequest);
        }

        @Override
        protected void onPostExecute(Submission submission) {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            if (submission != null) {
                loadComments(submission);
            }
        }
    }
}

