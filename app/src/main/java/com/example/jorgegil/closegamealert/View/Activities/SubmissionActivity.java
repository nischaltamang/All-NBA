package com.example.jorgegil.closegamealert.View.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.Utils.RedditAuthentication;
import com.example.jorgegil.closegamealert.Utils.Utilities;

import net.dean.jraw.http.SubmissionRequest;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.Submission;

public class SubmissionActivity extends AppCompatActivity {
    private static final String TAG = "SubmissionActivity";

    private static final String THREAD_ID_KEY = "THREAD_ID";
    private ViewGroup mCommentsLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String threadId = intent.getStringExtra(THREAD_ID_KEY);

        // TODO: get submission from id and set image and text.

        mCommentsLinearLayout = (ViewGroup) findViewById(R.id.submission_comments);

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

    public void getComments(Submission submission) {
        Iterable<CommentNode> iterable = submission.getComments().walkTree();
        for (CommentNode node : iterable) {
            addCommentToLayout(node);
        }
    }

    private void addCommentToLayout(CommentNode commentNode) {
        View commentLayout = LayoutInflater.from(this).inflate(R.layout.comment_layout,
                mCommentsLinearLayout, false);

        TextView authorView = (TextView) commentLayout.findViewById(R.id.authorTextView);
        TextView bodyView = (TextView) commentLayout.findViewById(R.id.bodyTextView);
        TextView timestampView = (TextView) commentLayout.findViewById(R.id.timeTextView);
        TextView pointsView = (TextView) commentLayout.findViewById(R.id.scoreTextView);

        Comment comment = commentNode.getComment();
        String author = comment.getAuthor();
        String body = comment.getBody();
        String timestamp = Utilities.formatDate(comment.getCreated());
        String points = String.valueOf(comment.getScore());

        authorView.setText(author);
        bodyView.setText(body);
        timestampView.setText(timestamp);
        pointsView.setText(getString(R.string.points, points));
        stuff(commentNode, commentLayout);

        mCommentsLinearLayout.addView(commentLayout);
    }

    private void stuff(CommentNode commentNode, View commentLayout) {
        RelativeLayout relativeLayout = (RelativeLayout) commentLayout
                .findViewById(R.id.relativeLayout);

        int padding_in_dp = 5;
        final float scale = this.getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5F);

        int depth = commentNode.getDepth(); // From 1

        // Add color if it is not a top-level comment.
        if (depth > 1) {
            int depthFromZero = depth - 2;
            int res = (depthFromZero) % 5;
            switch (res) {
                case 0:
                    relativeLayout.setBackgroundResource(R.drawable.borderblue);
                    break;
                case 1:
                    relativeLayout.setBackgroundResource(R.drawable.bordergreen);
                    break;
                case 2: //
                    relativeLayout.setBackgroundResource(R.drawable.borderbrown);
                    break;
                case 3:
                    relativeLayout.setBackgroundResource(R.drawable.borderorange);
                    break;
                case 4:
                    relativeLayout.setBackgroundResource(R.drawable.borderred);
                    break;
            }
        }

        // Add padding depending on level.

        commentLayout.setPadding(padding_in_px * (depth - 2), 0, 0, 0);
    }

}
