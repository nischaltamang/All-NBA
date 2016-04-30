package com.example.jorgegil.closegamealert.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jorgegil.closegamealert.General.Highlight;
import com.example.jorgegil.closegamealert.R;
import com.squareup.picasso.Picasso;

import net.dean.jraw.models.Listing;

import java.util.ArrayList;

/**
 * Created by jorgegil on 4/30/16.
 */
public class HLAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Highlight> highlights;
    private static LayoutInflater inflater;

    public HLAdapter(Context context, ArrayList<Highlight> highlights) {
        this.context = context;
        this.highlights = highlights;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return highlights.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.row_highlight, parent, false);
        ImageView thumbnailView = (ImageView) rowView.findViewById(R.id.thumbnailView);
        TextView titleView = (TextView) rowView.findViewById(R.id.titleView);

        Picasso.with(context).load(highlights.get(i).thumbnailURL).into(thumbnailView);
        titleView.setText(highlights.get(i).title);

        return rowView;
    }
}
