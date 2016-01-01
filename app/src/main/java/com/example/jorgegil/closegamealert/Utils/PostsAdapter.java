package com.example.jorgegil.closegamealert.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jorgegil.closegamealert.General.Post;
import com.example.jorgegil.closegamealert.R;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by TOSHIBA on 12/25/2015.
 */
public class PostsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Post> postsList;
    private String type;
    private static LayoutInflater inflater = null;

    public PostsAdapter(Context context, ArrayList<Post> postsList, String type) {
        this.context = context;
        this.postsList = postsList;
        this.type = type;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return postsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        switch (type) {
            case "small":
                rowView = inflater.inflate(R.layout.post_layout, parent, false);
                break;
            case "large":
                rowView = inflater.inflate(R.layout.post_layout_large, parent, false);
                break;
            default:
                rowView = inflater.inflate(R.layout.post_layout, parent, false);
        }

        TextView scoreView = (TextView) rowView.findViewById(R.id.scoreView);
        TextView authorView = (TextView) rowView.findViewById(R.id.authorView);
        TextView createdView = (TextView) rowView.findViewById(R.id.createdView);
        TextView titleView = (TextView) rowView.findViewById(R.id.titleView);
        TextView numOfCommentsView = (TextView) rowView.findViewById(R.id.numCommentsView);
        TextView linkView = (TextView) rowView.findViewById(R.id.linkView);
        TextView subredditView = (TextView) rowView.findViewById(R.id.subredditView);

        ImageView thumbnail = (ImageView) rowView.findViewById(R.id.thumbnail);

        scoreView.setText(String.valueOf(postsList.get(position).score));
        authorView.setText(postsList.get(position).author);
        createdView.setText(String.valueOf(postsList.get(position).created));
        titleView.setText(postsList.get(position).title);
        //numOfCommentsView.setText(String.valueOf(postsList.get(position).numOfComments) + " Comments");

        String url = postsList.get(position).thumbnail;
        if (postsList.get(position).isSelf || url.equals("")) {
            linkView.setText("• self");
            thumbnail.setVisibility(View.GONE);
        } else {
            linkView.setText("• link");
            Picasso.with(context).load(url).into(thumbnail);
        }
        subredditView.setText("• " + postsList.get(position).subreddit);




        return rowView;
    }

}
