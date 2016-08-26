package com.example.jorgegil.closegamealert.View.Activities;

import android.content.Intent;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jorgegil.closegamealert.General.Constants;
import com.example.jorgegil.closegamealert.General.Streamable;
import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.Utils.CommentAdapter;
import com.example.jorgegil.closegamealert.Utils.RedditAuthentication;
import com.example.jorgegil.closegamealert.Utils.Utilities;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import net.dean.jraw.http.SubmissionRequest;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Thumbnails;

import org.json.JSONObject;

import java.io.BufferedReader;
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
    private TextView mAuthorTextView;
    private TextView mTimestampTextView;
    private TextView mScoreTextView;
    private ImageView mSubmissionImageView;

    private String threadId;
    private String threadDomain;
    private String threadUrl;

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
        mAuthorTextView = (TextView) findViewById(R.id.submission_author);
        mTimestampTextView = (TextView) findViewById(R.id.submission_timestamp);
        mScoreTextView = (TextView) findViewById(R.id.submission_score);
        mSubmissionImageView = (ImageView) findViewById(R.id.submission_image);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        new GetFullThread().execute(threadId);
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
    }

    private void playStreamable(String url) {
        String streamableId = url.substring(url.lastIndexOf('/') + 1);

        new FetchStreamable(streamableId).execute();
    }

    private void playVideo(String url, int width, int height) {

    }

    private class GetFullThread extends AsyncTask<String, Void, Submission> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Submission doInBackground(String... threadId) {
            SubmissionRequest.Builder b = new SubmissionRequest.Builder(threadId[0]);
            b.sort(CommentSort.HOT);
            SubmissionRequest sr = b.build();

            return RedditAuthentication.sRedditClient.getSubmission(sr);
        }

        @Override
        protected void onPostExecute(Submission submission) {
            getComments(submission);
        }
    }

    private class FetchStreamable extends AsyncTask<Void, Void, Streamable> {
        String mStreamableId;

        public FetchStreamable(String streamableId) {
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
                Log.e(TAG, "Failed to convert string to URL. ", e);
            } catch (IOException e) {
                Log.e(TAG, "Failed to open URL connection. ", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Streamable streamable) {
            String videoUrl = streamable.getMobileVideoUrl();
            int videoWidth = streamable.getMobileVideoWidth();
            int videoHeight = streamable.getMobileVideoHeight();
            playVideo(videoUrl, videoWidth, videoHeight);
        }
    }

}
