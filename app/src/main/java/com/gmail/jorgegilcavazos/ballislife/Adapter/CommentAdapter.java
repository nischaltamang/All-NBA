package com.gmail.jorgegilcavazos.ballislife.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gmail.jorgegilcavazos.ballislife.R;
import com.gmail.jorgegilcavazos.ballislife.Utils.DateFormatUtil;
import com.gmail.jorgegilcavazos.ballislife.Utils.RedditUtils;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.CommentNode;

import java.util.List;

/**
 * Adapter used to hold all of the comments from a thread.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    Context mContext;
    List<CommentNode> mComments;

    public CommentAdapter(Context context, List<CommentNode> comments) {
        mContext = context;
        mComments = comments;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout,
                parent, false);

        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        CommentNode commentNode = mComments.get(position);

        Comment comment = commentNode.getComment();
        String author = comment.getAuthor();
        String body = comment.getBody();
        String timestamp = DateFormatUtil.formatRedditDate(comment.getCreated());
        String score = String.valueOf(comment.getScore());
        String flair = RedditUtils.parseRedditNBAFlair(String.valueOf(comment.getAuthorFlair()));

        holder.authorTextView.setText(author);
        holder.bodyTextView.setText(body);
        holder.timestampTextView.setText(timestamp);
        holder.scoreTextView.setText(mContext.getString(R.string.points, score));
        holder.flairTextView.setText(flair);
        setBackgroundAndPadding(commentNode, holder);
    }

    @Override
    public int getItemCount() {
        return null !=  mComments ? mComments.size() : 0;
    }

    private void setBackgroundAndPadding(CommentNode commentNode, CommentViewHolder holder) {
        int padding_in_dp = 5;
        final float scale = mContext.getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5F);

        int depth = commentNode.getDepth(); // From 1

        // Add color if it is not a top-level comment.
        if (depth > 1) {
            int depthFromZero = depth - 2;
            int res = (depthFromZero) % 5;
            switch (res) {
                case 0:
                    holder.mCommentInnerRelLayout.setBackgroundResource(R.drawable.borderblue);
                    break;
                case 1:
                    holder.mCommentInnerRelLayout.setBackgroundResource(R.drawable.bordergreen);
                    break;
                case 2: //
                    holder.mCommentInnerRelLayout.setBackgroundResource(R.drawable.borderbrown);
                    break;
                case 3:
                    holder.mCommentInnerRelLayout.setBackgroundResource(R.drawable.borderorange);
                    break;
                case 4:
                    holder.mCommentInnerRelLayout.setBackgroundResource(R.drawable.borderred);
                    break;
            }
        }
        // Add padding depending on level.
        holder.mCommentOuterRelLayout.setPadding(padding_in_px * (depth - 2), 0, 0, 0);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTextView, scoreTextView, timestampTextView, bodyTextView,
                flairTextView;
        public RelativeLayout mCommentOuterRelLayout;
        public RelativeLayout mCommentInnerRelLayout;

        public CommentViewHolder(View view) {
            super(view);
            mCommentOuterRelLayout = (RelativeLayout) view;
            mCommentInnerRelLayout = (RelativeLayout)
                    view.findViewById(R.id.comment_inner_relativeLayout);
            authorTextView = (TextView) view.findViewById(R.id.comment_author);
            scoreTextView = (TextView) view.findViewById(R.id.comment_score);
            timestampTextView = (TextView) view.findViewById(R.id.comment_timestamp);
            bodyTextView = (TextView) view.findViewById(R.id.comment_body);
            flairTextView = (TextView) view.findViewById(R.id.comment_flair);
        }
    }
}
