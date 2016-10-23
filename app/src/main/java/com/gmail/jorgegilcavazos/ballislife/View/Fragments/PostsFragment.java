package com.gmail.jorgegilcavazos.ballislife.View.Fragments;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import com.gmail.jorgegilcavazos.ballislife.General.Constants;
import com.gmail.jorgegilcavazos.ballislife.R;
import com.gmail.jorgegilcavazos.ballislife.Adapter.PostsAdapter;
import com.gmail.jorgegilcavazos.ballislife.Utils.AuthListener;
import com.gmail.jorgegilcavazos.ballislife.Service.RedditAuthentication;
import com.gmail.jorgegilcavazos.ballislife.Utils.DateFormatUtil;
import com.gmail.jorgegilcavazos.ballislife.View.Activities.MainActivity;
import com.gmail.jorgegilcavazos.ballislife.View.Activities.SubmissionActivity;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;

public class PostsFragment extends Fragment {
    private static final String TAG = "PostsFragment";

    private Context context;
    private View rootView;
    private String type;
    private ListView postsListView;
    private LinearLayout spinner, videoProgressLayout;
    private Snackbar snackbar;

    private VideoView videoView;
    private View background;
    boolean isPreviewVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getActivity();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString("TYPE");
        }
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_posts, container, false);
        postsListView = (ListView) rootView.findViewById(R.id.postsListView);
        spinner = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);
        videoProgressLayout = (LinearLayout) rootView.findViewById(R.id.videoProgressLayout);
        videoView = (VideoView) rootView.findViewById(R.id.videoView);
        background = rootView.findViewById(R.id.background);
        background.setVisibility(View.GONE);

        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels;
        switch (type) {
            case "small":
                dpAsPixels = (int) (5 * scale + 0.5f);
                postsListView.setDividerHeight(dpAsPixels);
                break;
            case "large":
                dpAsPixels = (int) (10 * scale + 0.5f);
                postsListView.setDividerHeight(dpAsPixels);
                break;
        }


        fetchPosts();
        return rootView;
    }

    private void fetchPosts() {
        spinner.setVisibility(View.VISIBLE);
        postsListView.setVisibility(View.GONE);

        AuthListener listener = new AuthListener() {
            @Override
            public void onSuccess() {
                fetchPosts();
            }

            @Override
            public void onFailure() {
                spinner.setVisibility(View.GONE);
                showSnackBar("Could not fetch posts", true /* retry */);
            }
        };

        new GetSubmissionListing(getActivity(),
                RedditAuthentication.getInstance().getRedditClient(), "nba", 20,
                Sorting.HOT, listener).execute();
    }

    private class GetSubmissionListing extends AsyncTask<Void, Void, Listing<Submission>> {
        private Context mContext;
        private RedditClient mRedditClient;
        private String mSubreddit;
        private int mLimit;
        private Sorting mSorting;
        private AuthListener mListener;

        public GetSubmissionListing(Context context, RedditClient redditClient, String subreddit,
                                    int limit, Sorting sorting, AuthListener listener) {
            mContext = context;
            mRedditClient = redditClient;
            mSubreddit = subreddit;
            mLimit = limit;
            mSorting = sorting;
            mListener = listener;
        }

        @Override
        protected Listing<Submission> doInBackground(Void... params) {
            if (RedditAuthentication.getInstance().getRedditClient().isAuthenticated()) {
                SubredditPaginator paginator = new SubredditPaginator(mRedditClient, mSubreddit);
                paginator.setLimit(mLimit);
                paginator.setSorting(mSorting);
                return paginator.next(false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Listing<Submission> submissions) {
            if (submissions == null
                    || !RedditAuthentication.getInstance().getRedditClient().isAuthenticated()) {
                Toast.makeText(mContext, "Not authenticated", Toast.LENGTH_SHORT).show();
                RedditAuthentication.getInstance().authenticate(mContext, mListener);
            } else {
                loadPosts(submissions);
            }
        }
    }


    private void loadPosts(final Listing<Submission> posts) {
        final String STREAMABLE_URL = "https://streamable.com/";
        final String STREAMABLE_VIDEO_URL = "http://cdn.streamable.com/video/mp4/";
        final String YOUTUBE_DOMAIN = "youtube.com";
        final String SELF_POST_NBA_DOMAIN = "self.nba";
        if (context != null) {

            postsListView.setAdapter(new PostsAdapter(context, posts, type));
            postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Submission post = posts.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.THREAD_ID, post.getId());
                    bundle.putString(Constants.THREAD_TITLE, post.getTitle());
                    bundle.putString(Constants.THREAD_DESCRIPTION, post.getSelftext());
                    bundle.putString(Constants.THREAD_AUTHOR, post.getAuthor());
                    bundle.putString(Constants.THREAD_TIMESTAMP,
                            DateFormatUtil.formatRedditDate(post.getCreated()));
                    bundle.putString(Constants.THREAD_SCORE, String.valueOf(post.getScore()));
                    bundle.putString(Constants.THREAD_DOMAIN, post.getDomain());
                    bundle.putString(Constants.THREAD_URL, post.getUrl());
                    if (post.getThumbnails() != null) {
                        bundle.putString(Constants.THREAD_IMAGE,
                                post.getThumbnails().getSource().getUrl());
                    } else {
                        bundle.putString(Constants.THREAD_IMAGE, null);
                    }
                    bundle.putBoolean(Constants.THREAD_SELF, post.isSelfPost());

                    Intent submissionIntent = new Intent(getActivity(), SubmissionActivity.class);
                    submissionIntent.putExtras(bundle);
                    startActivity(submissionIntent);

                    String url = post.getUrl();
                    String domain = post.getDomain();
                    switch (domain) {
                        case Constants.STREAMABLE_DOMAIN:
                            url = url.replace(STREAMABLE_URL, STREAMABLE_VIDEO_URL);
                            url = url + ".mp4";
                            playVideo(url);
                            break;
                        case YOUTUBE_DOMAIN:
                            //TODO: Set up youtube player https://developers.google.com/youtube/android/player/
                            Toast.makeText(context, "Youtube video still not ready", Toast.LENGTH_SHORT).show();
                            break;
                        case SELF_POST_NBA_DOMAIN:
                            Toast.makeText(context, "Text post", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(context, "Not a video", Toast.LENGTH_SHORT).show();

                    }
                }
            });

            spinner.setVisibility(View.GONE);
            postsListView.setVisibility(View.VISIBLE);
        }
    }

    private void playVideo(String url) {
        //TODO: handle null pointer exception
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        videoView.setVisibility(View.VISIBLE);
        videoView.setZOrderOnTop(true); //HIDE
        postsListView.setEnabled(false);
        background.setVisibility(View.VISIBLE);
        videoProgressLayout.setVisibility(View.VISIBLE);
        isPreviewVisible = true;
        try {
            Uri uri = Uri.parse(url);
            videoView.setMediaController(new android.widget.MediaController(context));
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoProgressLayout.setVisibility(View.GONE);
                    videoView.setZOrderOnTop(false); //SHOW
                }
            });
            videoView.start();
        } catch (Exception e) {
            // TODO: Handle exception
            stopVideo();
            Toast.makeText(context, "Error loading video", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopVideo() {
        //TODO: Handle null pointer exception
        ((MainActivity) getActivity()).getSupportActionBar().show();

        videoView.stopPlayback();
        videoView.setZOrderOnTop(true); //HIDE
        videoView.setVisibility(View.GONE);
        postsListView.setEnabled(true);
        background.setVisibility(View.GONE);
        videoProgressLayout.setVisibility(View.GONE);
        isPreviewVisible = false;
    }

    private void showSnackBar(String message, boolean retry) {
        snackbar = Snackbar.make(rootView, message,
                Snackbar.LENGTH_INDEFINITE);
        if (retry) {
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchPosts();
                }
            });
        }
        snackbar.show();
    }

    private void dismissSnackbar() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                stopVideo();
                spinner.setVisibility(View.VISIBLE);
                fetchPosts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isPreviewVisible() {
        return isPreviewVisible;
    }

    @Override
    public void onPause() {
        dismissSnackbar();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
