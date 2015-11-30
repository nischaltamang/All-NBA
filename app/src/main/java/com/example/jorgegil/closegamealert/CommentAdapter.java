package com.example.jorgegil.closegamealert;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;

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
        TextView borderView = (TextView) rowView.findViewById(R.id.borderView);

        int padding_in_dp = 10;
        final float scale = context.getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5F);

        int level = commentsList.get(position).level + 1;
        Log.d("BORDER", "LVL - " + level);


        if (level - 1 > 0) {
            int res = (level - 1) % 6;
            switch (res) {
                case 0:
                    borderView.setBackgroundColor(context.getResources().getColor(R.color.red));
                    Log.d("BORDER", "color - 0");
                    break;
                case 1:  borderView.setBackgroundColor(context.getResources().getColor(R.color.orange));
                    Log.d("BORDER", "color - 1");
                    break;
                case 2:  borderView.setBackgroundColor(context.getResources().getColor(R.color.brown));
                    Log.d("BORDER", "color - 2");
                    break;
                case 3:  borderView.setBackgroundColor(context.getResources().getColor(R.color.blue));
                    Log.d("BORDER", "color - 3");
                    break;
                case 4:  borderView.setBackgroundColor(context.getResources().getColor(R.color.green));
                    Log.d("BORDER", "color - 4");
                    break;
                case 5:  borderView.setBackgroundColor(context.getResources().getColor(R.color.yellow));
                    Log.d("BORDER", "color - 5");
                    break;
            }
        }
        rowView.setPadding(padding_in_px * (level - 1), 0, 0, 0);

        commentView.setText(commentsList.get(position).text);
        authorView.setText(commentsList.get(position).author);
        scoreView.setText(commentsList.get(position).points);
        postedOnView.setText(commentsList.get(position).postedOn);

        return rowView;
    }
}
