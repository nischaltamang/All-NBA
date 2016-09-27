package com.example.jorgegil.closegamealert.View.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import com.example.jorgegil.closegamealert.General.NBAGame;
import com.example.jorgegil.closegamealert.Network.GetRequestListener;
import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.Adapter.GameAdapter;
import com.example.jorgegil.closegamealert.Service.GameDataService;
import com.example.jorgegil.closegamealert.Service.JSONGameDataService;
import com.example.jorgegil.closegamealert.Utils.Utilities;
import com.example.jorgegil.closegamealert.View.Activities.CommentsActivity;
import com.example.jorgegil.closegamealert.View.Activities.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

// TODO: Use View Holder pattern instead of list view with adapter.
public class GamesFragment extends Fragment {
    public final static String TAG = "GamesFragment";

    public final static String GAME_THREAD_HOME =
            "com.example.jorgegil.closegamealert.GAME_THREAD_HOME";
    public final static String GAME_THREAD_AWAY =
            "com.example.jorgegil.closegamealert.GAME_THREAD_AWAY";
    public final static String GAME_ID = "com.example.jorgegil.closegamealert.GAME_ID";

    private View rootView;
    private List<NBAGame> nbaGames;
    private ListView listView;
    private LinearLayout linlaHeaderProgress;
    private GameDataService gameDataService;
    private Snackbar snackbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_games, container, false);
        listView = (ListView) rootView.findViewById(R.id.games_listview);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.games_fragment_progress_layout);

        nbaGames = new ArrayList<>();
        loadGameData();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register Broadcast manager to update scores automatically
        Log.d(TAG, "Registering receiver");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("game-data"));
    }

    // When new data is received, the JSON is parsed and the listview is notified of change.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isVisible()) {
                Log.d(TAG, "received new data!!");
                String message = intent.getStringExtra("message");
                updateGameData(message);
            }
        }
    };

    private void loadGameData() {
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        GetRequestListener listener = new GetRequestListener() {
            @Override
            public void onResult(String result) {
                nbaGames.clear();
                nbaGames.addAll(getGamesListFromJson(result));
                setToolbarDate();
                setGameAdapter();
                linlaHeaderProgress.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Volley error when loading game data: " + error);
                linlaHeaderProgress.setVisibility(View.GONE);
                showSnackBar("Could not load game data", true /* retry */);
            }
        };

        gameDataService = new JSONGameDataService();
        gameDataService.fetchGames(listener);
    }

    private void updateGameData(String jsonString) {
        nbaGames.clear();
        nbaGames.addAll(getGamesListFromJson(jsonString));
        setGameAdapter();
    }

    private List<NBAGame> getGamesListFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, new TypeToken<List<NBAGame>>(){}.getType());
    }

    private void setGameAdapter() {
        if (listView.getAdapter() == null) {
            listView.setAdapter(new GameAdapter(getActivity(), nbaGames));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity(), CommentsActivity.class);
                    intent.putExtra(GAME_THREAD_HOME, nbaGames.get(i).getHomeTeamAbbr());
                    intent.putExtra(GAME_THREAD_AWAY, nbaGames.get(i).getAwayTeamAbbr());
                    intent.putExtra(GAME_ID, nbaGames.get(i).getId());
                    startActivity(intent);
                }
            });
        } else {
            ((GameAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
    }

    private boolean isFragmentUIActive() {
        return isAdded() && !isDetached() && !isRemoving();
    }

    private void setToolbarDate() {
        if (nbaGames != null && nbaGames.size() > 0) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.setToolbarSubtitle(
                        Utilities.formatToolbarDate(nbaGames.get(0).getDate()));
            }
        }
    }

    private void showSnackBar(String message, boolean retry) {
        snackbar = Snackbar.make(rootView, message,
                Snackbar.LENGTH_INDEFINITE);
        if (retry) {
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadGameData();
                }
            });
        }
        linlaHeaderProgress.setVisibility(View.GONE);
        snackbar.show();
    }

    private void dismissSnackbar() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    @Override
    public void onPause() {
        if (gameDataService != null) {
            gameDataService.cancel();
        }
        dismissSnackbar();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "Unregistering receiver");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onStop();
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
