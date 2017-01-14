package com.gmail.jorgegilcavazos.ballislife.features.submission;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.gmail.jorgegilcavazos.ballislife.util.Constants;
import com.gmail.jorgegilcavazos.ballislife.features.model.Streamable;
import com.gmail.jorgegilcavazos.ballislife.R;
import com.gmail.jorgegilcavazos.ballislife.features.shared.CommentAdapter;
import com.gmail.jorgegilcavazos.ballislife.util.MyDebug;
import com.gmail.jorgegilcavazos.ballislife.network.RedditAuthentication;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import net.dean.jraw.http.SubmissionRequest;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.Submission;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SubmissionActivity extends AppCompatActivity {
    private static final String TAG = "SubmissionActivity";

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private TextView mAuthorTextView;
    private TextView mTimestampTextView;
    private TextView mScoreTextView;
    private TextView mCommentCntTextView;
    private ImageView mSubmissionImageView;
    private VideoView mVideoView;
    private FloatingActionButton mFab;

    private String threadId;
    private String threadDomain;
    private String threadUrl;

    private FetchFullThreadTask fetchFullThreadTask;
    private FetchStreamableTask fetchStreamableTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);
        setUpToolbar();

        setTitle(getString(R.string.rnba));

        Bundle extras = getIntent().getExtras();
        threadId = extras.getString(Constants.THREAD_ID);
        threadDomain = extras.getString(Constants.THREAD_DOMAIN);
        threadUrl = extras.getString(Constants.THREAD_URL);

        mTitleTextView = (TextView) findViewById(R.id.submission_title);
        mDescriptionTextView = (TextView) findViewById(R.id.submission_description);
        mAuthorTextView = (TextView) findViewById(R.id.submission_author);
        mTimestampTextView = (TextView) findViewById(R.id.submission_timestamp);
        mScoreTextView = (TextView) findViewById(R.id.submission_score);
        mCommentCntTextView = (TextView) findViewById(R.id.submission_num_comments);
        mSubmissionImageView = (ImageView) findViewById(R.id.submission_image);
        mVideoView = (VideoView) findViewById(R.id.submission_video);
        mVideoView.setVisibility(View.INVISIBLE);
        loadSubmissionDetails(extras);

        mRecyclerView = (RecyclerView) findViewById(R.id.submission_rv);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mRecyclerView.setNestedScrollingEnabled(false);
        } else {
            ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
        }

        mSubmissionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(threadUrl, threadDomain);
            }
        });

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fetchFullThreadTask = new FetchFullThreadTask(threadId);
        fetchFullThreadTask.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (fetchFullThreadTask != null) {
            fetchFullThreadTask.cancel(true);
        }
        if (fetchStreamableTask != null) {
            fetchStreamableTask.cancel(true);
        }
        super.onDestroy();
    }

    private void setUpToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void loadSubmissionDetails(Bundle extras) {
        mTitleTextView.setText(extras.getString(Constants.THREAD_TITLE));
        mAuthorTextView.setText(extras.getString(Constants.THREAD_AUTHOR));
        mTimestampTextView.setText(extras.getString(Constants.THREAD_TIMESTAMP));
        mScoreTextView.setText(extras.getString(Constants.THREAD_SCORE));
        mCommentCntTextView.setText(extras.getString(Constants.THREAD_NUM_COMMENTS));

        if (extras.getBoolean(Constants.THREAD_SELF)) {
            mSubmissionImageView.setVisibility(View.GONE);
        } else {
            String imageUrl = extras.getString(Constants.THREAD_IMAGE);
            if (imageUrl != null) {
                Picasso.with(this).load(imageUrl).fit().centerCrop().into(mSubmissionImageView);
            } else {
                mSubmissionImageView.setVisibility(View.GONE);
            }
        }

        String description = extras.getString(Constants.THREAD_DESCRIPTION);
        if (description.equals("")) {
            mDescriptionTextView.setVisibility(View.GONE);
        } else {
            mDescriptionTextView.setText(description);
        }
    }

    private void getComments(Submission submission) {
        Iterable<CommentNode> iterable = submission.getComments().walkTree();
        List<CommentNode> commentNodes = new ArrayList<>();
        for (CommentNode node : iterable) {
            commentNodes.add(node);
        }
        mAdapter = new CommentAdapter(this, commentNodes);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void openLink(String url, String domain) {
        if (domain.equals(Constants.STREAMABLE_DOMAIN)) {
            playStreamable(url);
        }
        playStreamable(url);
    }

    private void playStreamable(String url) {
        String streamableId = url.substring(url.lastIndexOf('/') + 1);
        fetchStreamableTask = new FetchStreamableTask("dng6");
        fetchStreamableTask.execute();
    }

    private void playVideo(String url, int width, int height) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mFab.hide();


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayH = displayMetrics.heightPixels;
        int displayW = displayMetrics.widthPixels;

        mVideoView.setVisibility(View.VISIBLE);
        //mVideoView.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
        //mVideoView.setZOrderOnTop(true); //HIDE
        //background.setVisibility(GamesView.VISIBLE);
        //videoProgressLayout.setVisibility(GamesView.VISIBLE);
        //isPreviewVisible = true;
        Uri uri = Uri.parse(url);

        try {

            mVideoView.setMediaController(new android.widget.MediaController(this));
            mVideoView.setVideoURI(uri);
            mVideoView.requestFocus();
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //videoProgressLayout.setVisibility(GamesView.GONE);
                    //mVideoView.setZOrderOnTop(false); //SHOW
                    mSubmissionImageView.setVisibility(View.GONE);
                }
            });
            mVideoView.start();
        } catch (Exception e) {
            // TODO: Handle exception
            //stopVideo();
            //Toast.makeText(context, "Error loading video", Toast.LENGTH_SHORT).show();
        }

    }

    private class FetchFullThreadTask extends AsyncTask<Void, Void, Submission> {
        private String mThreadId;

        public FetchFullThreadTask(String threadId) {
            mThreadId = threadId;
        }

        @Override
        protected Submission doInBackground(Void... params) {
            SubmissionRequest.Builder b = new SubmissionRequest.Builder(mThreadId);
            b.sort(CommentSort.HOT);
            SubmissionRequest sr = b.build();

            return RedditAuthentication.getInstance().getRedditClient().getSubmission(sr);
        }

        @Override
        protected void onPostExecute(Submission submission) {
            getComments(submission);
        }
    }

    private class FetchStreamableTask extends AsyncTask<Void, Void, Streamable> {
        String mStreamableId;

        public FetchStreamableTask(String streamableId) {
            mStreamableId = streamableId;
        }

        @Override
        protected Streamable doInBackground(Void... params) {
            String streamableUrl = Constants.STREAMABLE_API_URL + mStreamableId;
            try {
                URL url = new URL(streamableUrl);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.setRequestMethod("GET");
                request.connect();

                JsonParser jsonParser = new JsonParser();
                JsonElement root = jsonParser.parse(new InputStreamReader((InputStream)
                        request.getContent()));
                JsonObject jsonObject = root.getAsJsonObject();
                Streamable streamable = new Streamable(jsonObject);
                return  streamable;
            } catch (MalformedURLException e) {
                if (MyDebug.LOG) {
                    Log.e(TAG, "Failed to convert string to URL. ", e);
                }
            } catch (IOException e) {
                if (MyDebug.LOG) {
                    Log.e(TAG, "Failed to open URL connection. ", e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Streamable streamable) {
            String videoUrl = null;
            int videoWidth = -1;
            int videoHeight = -1;

            if (streamable.getMobileVideoUrl() != null) {
                videoUrl = streamable.getMobileVideoUrl();
                videoWidth = streamable.getMobileVideoWidth();
                videoHeight = streamable.getMobileVideoHeight();
            }
            if (videoUrl == null && streamable.getDesktopVideoUrl() != null) {
                videoUrl = streamable.getDesktopVideoUrl();
                videoWidth = streamable.getDesktopVideoWidth();
                videoHeight = streamable.getDesktopVideoHeight();
            }

            if (videoUrl != null && videoWidth != -1 && videoHeight != -1) {
                playVideo(videoUrl, videoWidth, videoHeight);
            } else {
                if (MyDebug.LOG) {
                    Log.e(TAG, "Could not get video from Streamable.");
                }
            }
        }
    }

}
