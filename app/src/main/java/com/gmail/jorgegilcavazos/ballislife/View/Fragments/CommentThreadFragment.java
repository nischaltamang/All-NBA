package com.gmail.jorgegilcavazos.ballislife.View.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.gmail.jorgegilcavazos.ballislife.Adapter.CommentAdapter;
import com.gmail.jorgegilcavazos.ballislife.General.TeamName;
import com.gmail.jorgegilcavazos.ballislife.R;
import com.gmail.jorgegilcavazos.ballislife.Utils.AuthListener;
import com.gmail.jorgegilcavazos.ballislife.Utils.MyDebug;
import com.gmail.jorgegilcavazos.ballislife.Service.RedditAuthentication;

import net.dean.jraw.http.SubmissionRequest;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;

import java.util.ArrayList;
import java.util.List;

public class CommentThreadFragment extends Fragment {
    private static final String TAG = "CommentThreadFragment";

    public interface SubmissionListingFetchListener {
        void onSuccess(Listing<Submission> submissions);
        void onFailure(String message);
    }

    public interface FullSubmissionFetchListener {
        void onSuccess(Submission submission);
        void onFailure(String message);
    }

    public static final int LIVE_THREAD = 0;
    public static final int POST_THREAD = 1;
    public static final String HOME_TEAM_KEY = "HOME_TEAM";
    public static final String AWAY_TEAM_KEY = "AWAY_TEAM";
    public static final String THREAD_TYPE_KEY = "THREAD_TYPE";
    private static final String NBA_SUBREDDIT = "nba";
    private static final int SEARCH_LIMIT = 100;

    private static final int NO_RETRY = -1;
    private static final int RETRY_FIND_SUBMISSION = 0;
    private static final int RETRY_FETCH_COMMENTS = 1;

    private String mHomeTeam;
    private String mAwayTeam;
    private String mThreadId;
    private boolean mFoundThreadId;
    private int mThreadType;

    Context mContext;

    View rootView;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    CommentAdapter mCommentAdapter;
    ProgressBar mProgressBar;
    Snackbar mSnackbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHomeTeam = getArguments().getString(HOME_TEAM_KEY);
            mAwayTeam = getArguments().getString(AWAY_TEAM_KEY);
            mThreadType = getArguments().getInt(THREAD_TYPE_KEY);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_comment_thread, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.comment_thread_rv);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mRecyclerView.setNestedScrollingEnabled(false);
        } else {
            ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

        if (mFoundThreadId) {
            fetchComments();
        } else {
            findGameSubmission();
        }
    }

    @Override
    public void onPause() {
        dismissSnackbar();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                dismissSnackbar();
                if (mFoundThreadId) {
                    fetchComments();
                } else {
                    findGameSubmission();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts a {@link FetchSubmissionListing} task that retrieves a list of submissions in the
     * /r/nba subreddit. If it is successfully retrieved, it tries to find the one that belongs
     * to this game and shows its comment tree.
     */
    private void findGameSubmission() {
        AuthListener authListener = new AuthListener() {
            @Override
            public void onSuccess() {
                findGameSubmission();
            }

            @Override
            public void onFailure() {
                showSnackBar("Failed to connect to Reddit", true, RETRY_FIND_SUBMISSION);
            }
        };
        SubmissionListingFetchListener fetchListener =  new SubmissionListingFetchListener() {
            @Override
            public void onSuccess(Listing<Submission> submissions) {
                findGameInSubmissions(submissions);
            }

            @Override
            public void onFailure(String message) {
                showSnackBar(message, true, RETRY_FIND_SUBMISSION);
            }
        };
        new FetchSubmissionListing(mContext, NBA_SUBREDDIT, SEARCH_LIMIT, Sorting.NEW,
                fetchListener, authListener).execute();
    }

    // TODO: Move to AsyncTask.
    /**
     * Given a list of Reddit submissions, finds the one that belongs to this game and type.
     */
    private void findGameInSubmissions(Listing<Submission> submissions) {
        String fullHomeTeam = null;
        String awayFullTeam = null;

        for (TeamName teamName : TeamName.values()) {
            if (teamName.toString().equals(mHomeTeam)) {
                fullHomeTeam = teamName.getTeamName();
            }
            if (teamName.toString().equals(mAwayTeam)) {
                awayFullTeam = teamName.getTeamName();
            }
        }

        if (fullHomeTeam == null || awayFullTeam == null) {
            return;
        }

        for (Submission submission : submissions) {
            String title = submission.getTitle();
            // Usually in format "GAME THREAD: Cleveland Cavaliers @ San Antonio Spurs".
            if (mThreadType == LIVE_THREAD) {
                if (title.contains("GAME THREAD") && title.contains(fullHomeTeam)
                        && title.contains(awayFullTeam)) {
                    mThreadId = submission.getId();
                    mFoundThreadId = true;
                    break;
                }
            }
            // Usually in format "POST GAME THREAD: San Antonio Spurs defeat Lakers".
            if (mThreadType == POST_THREAD) {
                String capsTitle = title.toUpperCase();
                if (capsTitle.contains("POST GAME THREAD") && titleContainsTeam(title, fullHomeTeam)
                        && titleContainsTeam(title, awayFullTeam)) {
                    mThreadId = submission.getId();
                    mFoundThreadId = true;
                    break;
                }
            }
        }

        if (mFoundThreadId) {
            fetchComments();
        } else {
            showSnackBar("No comment thread found", true, RETRY_FIND_SUBMISSION);
        }
    }

    /**
     * Checks that the title contains the full team name or at least the name only, e.g "Spurs".
     */
    private boolean titleContainsTeam(String title, String fullTeamName) {
        String capsTitle = title.toUpperCase();
        String capsTeam = fullTeamName.toUpperCase(); // Ex. "SAN ANTONIO SPURS".
        String capsName = capsTeam.substring(capsTeam.lastIndexOf(" ") + 1); // Ex. "SPURS".
        return capsTitle.contains(capsName) || capsTitle.contains(capsName);
    }

    /**
     * Starts a {@link FetchFullSubmission} task that retrieves the Submission of the given
     * submissionId. A "full" submissions is one that also contains its comment tree.
     */
    private void fetchComments() {
        AuthListener authListener = new AuthListener() {
            @Override
            public void onSuccess() {
                fetchComments();
            }

            @Override
            public void onFailure() {
                showSnackBar("Failed to connect to Reddit", true, RETRY_FETCH_COMMENTS);
            }
        };

        FullSubmissionFetchListener fetchListener = new FullSubmissionFetchListener() {
            @Override
            public void onSuccess(Submission submission) {
                loadComments(submission);
            }

            @Override
            public void onFailure(String message) {
                showSnackBar(message, true, RETRY_FIND_SUBMISSION);
            }
        };
        new FetchFullSubmission(mContext, mThreadId, fetchListener, authListener).execute();
    }

    /**
     * Loads a tree of comments into the RecyclerView, given a Reddit Submission.
     */
    private void loadComments(Submission submission) {
        Iterable<CommentNode> iterable = submission.getComments().walkTree();
        List<CommentNode> commentNodes = new ArrayList<>();
        for (CommentNode node : iterable) {
            commentNodes.add(node);
        }
        mCommentAdapter = new CommentAdapter(mContext, commentNodes);
        mRecyclerView.setAdapter(mCommentAdapter);
    }

    private void showSnackBar(String message, boolean retry, final int retryCode) {
        mSnackbar = Snackbar.make(rootView, message,
                Snackbar.LENGTH_INDEFINITE);
        if (retry) {
            mSnackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (retryCode) {
                        case RETRY_FIND_SUBMISSION:
                            findGameSubmission();
                            break;
                        case RETRY_FETCH_COMMENTS:
                            fetchComments();
                            break;
                    }
                }
            });
        }
        mSnackbar.show();
        mProgressBar.setVisibility(View.GONE);
    }

    private void dismissSnackbar() {
        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }
    }

    /**
     * Retrieves a Listing of Reddit Submissions, given a subreddit, a limit of submissions and a
     * sorting option.
     */
    private class FetchSubmissionListing extends AsyncTask<Void, Void, Listing<Submission>> {
        private Context mContext;
        private String mSubreddit;
        private int mLimit;
        private Sorting mSorting;
        private SubmissionListingFetchListener mFetchListener;
        private AuthListener mAuthListener;

        public FetchSubmissionListing(Context context, String subreddit, int limit, Sorting sorting,
                                      SubmissionListingFetchListener fetchListener,
                                      AuthListener authListener) {
            mContext = context;
            mSubreddit = subreddit;
            mLimit = limit;
            mSorting = sorting;
            mFetchListener = fetchListener;
            mAuthListener = authListener;
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected Listing<Submission> doInBackground(Void... params) {
            RedditAuthentication redditAuthentication = RedditAuthentication.getInstance();
            if (redditAuthentication.getRedditClient().isAuthenticated()) {
                SubredditPaginator paginator = new SubredditPaginator(
                        redditAuthentication.getRedditClient(), mSubreddit);
                paginator.setLimit(mLimit);
                paginator.setSorting(mSorting);
                try {
                    return paginator.next(false /* forceNetwork */);
                } catch (Exception e) {
                    if (MyDebug.LOG) {
                        Log.d(TAG, "Reddit auth error on FetchSubmissionListing.");
                    }
                }
            } else {
                mFetchListener.onFailure("Failed to connect to Reddit");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Listing<Submission> submissions) {
            if (submissions != null) {
                mFetchListener.onSuccess(submissions);
            } else {
                if (!RedditAuthentication.getInstance().getRedditClient().isAuthenticated()) {
                    // Attempt to authenticate once.
                    RedditAuthentication.getInstance().authenticate(mContext, mAuthListener);
                }
                mFetchListener.onFailure("Failed to connect to Reddit");
            }
        }
    }

    /**
     * Retrieves a "full" Reddit submission given a Reddit submisisonId. A "full" submission is one
     * that also contains its comment tree.
     * The sorting of the thread is determined by mThreadType (Live game or post game).
     */
    private class FetchFullSubmission extends AsyncTask<Void, Void, Submission> {
        private Context mContext;
        private String mThreadId;
        private FullSubmissionFetchListener mFetchListener;
        private AuthListener mAuthListener;

        public FetchFullSubmission(Context context, String threadId,
                                   FullSubmissionFetchListener fetchListener,
                                   AuthListener authListener) {
            mContext = context;
            mThreadId = threadId;
            mFetchListener = fetchListener;
            mAuthListener = authListener;
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected Submission doInBackground(Void... params) {
            if (mThreadId == null) {
                return null;
            }
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
            try {
                return RedditAuthentication.getInstance()
                        .getRedditClient().getSubmission(submissionRequest);
            } catch (Exception e) {
                if (MyDebug.LOG) {
                    Log.d(TAG, "Could not load submission in FetchFullSubmission.");
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Submission submission) {
            mProgressBar.setVisibility(View.GONE);
            if (submission != null) {
                mFetchListener.onSuccess(submission);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                if (!RedditAuthentication.getInstance().getRedditClient().isAuthenticated()) {
                    // Attempt to re-authenticate once.
                    RedditAuthentication.getInstance().authenticate(mContext, mAuthListener);
                }
                mFetchListener.onFailure("Failed to connect to Reddit");
            }
        }
    }
}

