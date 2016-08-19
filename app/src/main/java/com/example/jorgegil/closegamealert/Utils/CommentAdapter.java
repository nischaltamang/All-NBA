package com.example.jorgegil.closegamealert.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.jorgegil.closegamealert.General.Comment;
import com.example.jorgegil.closegamealert.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by jorgegil on 11/29/15.
 */
public class CommentAdapter extends BaseAdapter{
    private final Context context;
    private final ArrayList<Comment> commentsList;
    private static LayoutInflater inflater = null;

    public CommentAdapter(Context context, ArrayList<Comment> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return commentsList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.comments_layout, parent, false);

        TextView commentView = (TextView) rowView.findViewById(R.id.bodyTextView);
        TextView authorView = (TextView) rowView.findViewById(R.id.authorTextView);
        TextView scoreView = (TextView) rowView.findViewById(R.id.scoreTextView);
        TextView postedOnView = (TextView) rowView.findViewById(R.id.timeTextView);
        RelativeLayout relativeLayout = (RelativeLayout) rowView.findViewById(R.id.relativeLayout);

        int padding_in_dp = 5;
        final float scale = context.getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5F);

        int level = commentsList.get(position).level; // From 0


        if (level > 0) {
            int res = (level) % 5;
            switch (res) {
                case 0:
                    relativeLayout.setBackgroundResource(R.drawable.borderblue);
                    break;
                case 1:
                    relativeLayout.setBackgroundResource(R.drawable.bordergreen);
                    break;
                case 2:
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
        rowView.setPadding(padding_in_px * (level - 1), 0, 0, 0);

        authorView.setText(commentsList.get(position).jrawComment.getAuthor());
        scoreView.setText(commentsList.get(position).jrawComment.getScore() + " points");
        //postedOnView.setText(formatDate(commentsList.get(position).jrawComment.getCreatedUtc()));
        commentView.setText(commentsList.get(position).jrawComment.getBody());

        return rowView;
    }

    public String formatDate(Date date) {

        String postedOn;
        Date now = new Date();

        long minutesAgo = (TimeUnit.MILLISECONDS.toMinutes(now.getTime() - date.getTime()));
        long hoursAgo = (TimeUnit.MILLISECONDS.toHours(now.getTime() - date.getTime()));
        long daysAgo = (TimeUnit.MILLISECONDS.toDays(now.getTime() - date.getTime()));

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

        return postedOn;
    }
}
