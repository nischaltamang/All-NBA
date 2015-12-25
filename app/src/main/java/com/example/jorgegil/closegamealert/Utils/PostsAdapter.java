package com.example.jorgegil.closegamealert.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.jorgegil.closegamealert.General.Post;
import com.example.jorgegil.closegamealert.R;

import java.util.ArrayList;

/**
 * Created by TOSHIBA on 12/25/2015.
 */
public class PostsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Post> postsList;
    private static LayoutInflater inflater = null;

    public PostsAdapter(Context context, ArrayList<Post> postsList) {
        this.context = context;
        this.postsList = postsList;
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
        View rowView = inflater.inflate(R.layout.post_layout, parent, false);

        return rowView;
    }
}
