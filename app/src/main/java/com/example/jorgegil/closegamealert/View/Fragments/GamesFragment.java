package com.example.jorgegil.closegamealert.View.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.Utils.GameAdapter;
import com.example.jorgegil.closegamealert.View.Activities.CommentsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends Fragment {
    public final static String TAG = "GamesFragment";

    public final static String GAME_THREAD_HOME =
            "com.example.jorgegil.closegamealert.GAME_THREAD_HOME";
    public final static String GAME_THREAD_AWAY =
            "com.example.jorgegil.closegamealert.GAME_THREAD_AWAY";
    public final static String GAME_ID = "com.example.jorgegil.closegamealert.GAME_ID";
    public final static String GAME_DATA_URL = "http://phpstack-4722-10615-67130.cloudwaysapps.com/GameData.txt";

    View rootView;

    List<String> homeTeam;
    List<String> awayTeam;
    List<String> homeScore;
    List<String> awayScore;
    List<String> clock;
    List<String> period;
    List<String> gameId;
    List<String> status;

    ListView listView;
    LinearLayout linlaHeaderProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Register Broadcast manager to update scores automatically
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("game-data"));

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_games, container, false);
        listView = (ListView) rootView.findViewById(R.id.games_listview);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);

        loadGameData();

        return rootView;
    }

    // When new data is received, the JSON is parsed and the listview is notified of change.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isVisible()) {
                String message = intent.getStringExtra("message");
                parseData(message, false);
            }
        }
    };

    private void loadGameData() {
        if (linlaHeaderProgress == null || listView == null ) {
            return;
        }
        // Show spinner and hide games.
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        StringRequest request = new StringRequest(GAME_DATA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseData(response, true /* reload */);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                Log.e(TAG, "Volley error when loading game data. " + error.toString());
                linlaHeaderProgress.setVisibility(View.INVISIBLE);
                //TODO: show "could not load message" and retry button.
            }
        });

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    public void parseData(String response, boolean reload) {
        homeTeam = new ArrayList<>();
        awayTeam = new ArrayList<>();
        homeScore = new ArrayList<>();
        awayScore = new ArrayList<>();
        clock = new ArrayList<>();
        period = new ArrayList<>();
        gameId = new ArrayList<>();
        status = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(response);
            int numOfEvents = jsonArray.length();

            for(int i = 0; i < numOfEvents; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                homeTeam.add(jsonObject.getString("homeTeam"));
                awayTeam.add(jsonObject.getString("awayTeam"));
                homeScore.add(jsonObject.getString("homeScore"));
                awayScore.add(jsonObject.getString("awayScore"));
                clock.add(jsonObject.getString("clock"));
                period.add(jsonObject.getString("period"));
                gameId.add(jsonObject.getString("id"));
                status.add(jsonObject.getString("status"));

                switch (status.get(i)) {
                    case "pre":
                        clock.set(i, "PRE");
                        period.set(i, "GAME");
                        break;
                    case "in":
                        if (Integer.parseInt(period.get(i)) < 5)
                            period.set(i, period.get(i) + " Qtr");
                        else
                            period.set(i, "OT" + (Integer.parseInt(period.get(i)) - 4));
                        break;
                    case "post":
                        clock.set(i, "FINAL");
                        period.set(i, "");
                        break;
                }

            }

            if (listView.getAdapter() == null) {
                if (getActivity() != null) {
                    // Set list view adapter for game events.
                    listView.setAdapter(new GameAdapter(getActivity(), homeTeam, awayTeam,
                            homeScore, awayScore, clock, period));

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(getActivity(), CommentsActivity.class);
                            intent.putExtra(GAME_THREAD_HOME, homeTeam.get(i));
                            intent.putExtra(GAME_THREAD_AWAY, awayTeam.get(i));
                            intent.putExtra(GAME_ID, gameId.get(i));
                            startActivity(intent);
                        }
                    });
                }
            } else {
                ((GameAdapter) listView.getAdapter()).notifyDataSetChanged();
            }

            if (reload) {
                // Hide reload icon and show list view
                linlaHeaderProgress.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON. " + e.toString());
            //TODO: show "could not load message" and retry button.
        }
    }

    @Override
    public void onDestroyView() {
        // Unregister since the fragment is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroyView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                loadGameData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
