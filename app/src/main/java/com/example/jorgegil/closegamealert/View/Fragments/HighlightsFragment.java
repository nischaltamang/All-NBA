package com.example.jorgegil.closegamealert.View.Fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jorgegil.closegamealert.General.Highlight;
import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.Utils.HLAdapter;
import com.example.jorgegil.closegamealert.Utils.HLLoader;
import com.example.jorgegil.closegamealert.View.Activities.MainActivity;

import java.util.ArrayList;


public class HighlightsFragment extends Fragment {
    private static final String TAG = "HighlightsFragment";

    String url = "https://streamable.com/ajax/stream/nba?count=100&grouped=false&page=";
    int page = 1;

    Context context;
    View rootView;
    ListView hlListView;
    LinearLayout linlaHeaderProgress, videoProgressLayout;

    Button loadMore;
    ArrayList<Highlight> hlList;

    VideoView videoView;
    View background;
    boolean isPreviewVisible;



    public HighlightsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getActivity();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_highlights, container, false);
        hlListView = (ListView) rootView.findViewById(R.id.HLListView);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);
        videoProgressLayout = (LinearLayout) rootView.findViewById(R.id.videoProgressLayout);
        videoView = (VideoView) rootView.findViewById(R.id.videoView);
        background = rootView.findViewById(R.id.background);
        background.setVisibility(View.GONE);

        loadMore = new Button(context);
        loadMore.setText("Load More");
        loadMore.setTextColor(getResources().getColor(R.color.secondaryText));
        loadMore.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        hlListView.addFooterView(loadMore);

        getHL(page);

        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page++;
                loadMore.setText("Loading...");
                getHL(page);
            }
        });



        return rootView;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                linlaHeaderProgress.setVisibility(View.VISIBLE);
                stopVideo();
                page = 1;
                getHL(page);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getHL(int page) {
        if (hlList == null) {
            linlaHeaderProgress.setVisibility(View.VISIBLE);
            hlListView.setVisibility(View.GONE);
        }

        Log.d(TAG, "Loading page " + page);

        StringRequest request = new StringRequest(url + page, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (hlList == null)
                    loadHL(response);
                else
                    loadMoreHL(response);

                loadMore.setText("Load More");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadMore.setText("Load More");
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public void loadHL(String response) {
        if (context != null) {
            HLLoader hlLoader = new HLLoader(response);

            hlList = hlLoader.fetchHighlights();

            //Log.d(TAG, "list size " + hlList.get(0).title);
            hlListView.setAdapter(new HLAdapter(context, hlList));

            hlListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String vURL = hlList.get(i).videoURL;
                    playVideo(vURL);
                }
            });


            linlaHeaderProgress.setVisibility(View.GONE);
            hlListView.setVisibility(View.VISIBLE);
        }
    }

    public void loadMoreHL(String response) {
        if (context != null) {
            HLLoader hlLoader = new HLLoader(response);
            hlList.addAll(hlLoader.fetchHighlights());
            ((HLAdapter)((HeaderViewListAdapter) hlListView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
        }
    }


    public void playVideo(String vURL) {
        //TODO: handle null pointer exception
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        videoView.setVisibility(View.VISIBLE);
        videoView.setZOrderOnTop(true); //HIDE
        hlListView.setEnabled(false);
        background.setVisibility(View.VISIBLE);
        videoProgressLayout.setVisibility(View.VISIBLE);
        isPreviewVisible = true;
        try {
            Uri uri = Uri.parse(vURL);
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
        hlListView.setEnabled(true);
        background.setVisibility(View.GONE);
        videoProgressLayout.setVisibility(View.GONE);
        isPreviewVisible = false;
    }

    public boolean isPreviewVisible() {
        return isPreviewVisible;
    }

}
