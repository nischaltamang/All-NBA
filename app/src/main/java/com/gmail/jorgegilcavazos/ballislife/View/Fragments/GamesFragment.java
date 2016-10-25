package com.gmail.jorgegilcavazos.ballislife.View.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.jorgegilcavazos.ballislife.General.NBAGame;
import com.gmail.jorgegilcavazos.ballislife.Network.GetRequestListener;
import com.gmail.jorgegilcavazos.ballislife.R;
import com.gmail.jorgegilcavazos.ballislife.Adapter.GameAdapter;
import com.gmail.jorgegilcavazos.ballislife.Service.GameDataService;
import com.gmail.jorgegilcavazos.ballislife.Service.JSONGameDataService;
import com.gmail.jorgegilcavazos.ballislife.Utils.DateFormatUtil;
import com.gmail.jorgegilcavazos.ballislife.Utils.MyDebug;
import com.gmail.jorgegilcavazos.ballislife.View.Activities.CommentsActivity;
import com.gmail.jorgegilcavazos.ballislife.View.Activities.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// TODO: Use View Holder pattern instead of list view with adapter.
public class GamesFragment extends Fragment {
    public final static String TAG = "GamesFragment";

    public final static String GAME_THREAD_HOME =
            "com.example.jorgegil.closegamealert.GAME_THREAD_HOME";
    public final static String GAME_THREAD_AWAY =
            "com.example.jorgegil.closegamealert.GAME_THREAD_AWAY";
    public final static String GAME_ID = "com.example.jorgegil.closegamealert.GAME_ID";

    private Context mContext;
    private View rootView;
    private TextView mNavigatorDate;
    private TextView mNoGamesText;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private GameAdapter mGameAdapter;
    private LinearLayout linlaHeaderProgress;
    private GameDataService gameDataService;
    private Snackbar snackbar;
    private List<NBAGame> mNbaGames;
    private Calendar mSelectedDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_games, container, false);
        mNavigatorDate = (TextView) rootView.findViewById(R.id.navigator_text);
        mNoGamesText = (TextView) rootView.findViewById(R.id.no_games_text);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.games_rv);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        linlaHeaderProgress = (LinearLayout) rootView.findViewById(
                R.id.games_fragment_progress_layout);

        getActivity().setTitle(R.string.games_fragment_title);

        ImageButton datePrevBtn = (ImageButton) rootView.findViewById(R.id.navigator_button_left);
        datePrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedDate == null) {
                    mSelectedDate = Calendar.getInstance();
                }
                mSelectedDate.add(Calendar.DAY_OF_YEAR, -1);
                setNavigatorDate(mSelectedDate.getTime());
                loadGameData(mSelectedDate.getTime());
            }
        });
        ImageButton dateNextBtn = (ImageButton) rootView.findViewById(R.id.navigator_button_right);
        dateNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedDate == null) {
                    mSelectedDate = Calendar.getInstance();
                }
                mSelectedDate.add(Calendar.DAY_OF_YEAR, 1);
                setNavigatorDate(mSelectedDate.getTime());
                loadGameData(mSelectedDate.getTime());
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        mSelectedDate = Calendar.getInstance();
        setNavigatorDate(mSelectedDate.getTime());
        loadGameData(mSelectedDate.getTime());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register Broadcast manager to update scores automatically
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("game-data"));
    }

    // When new data is received, the JSON is parsed and the listview is notified of change.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isVisible() && DateFormatUtil.isDateToday(mSelectedDate.getTime())) {
                String message = intent.getStringExtra("message");
                updateGameData(message);
            }
        }
    };

    private void loadGameData(Date date) {
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mNoGamesText.setVisibility(View.GONE);

        GetRequestListener listener = new GetRequestListener() {
            @Override
            public void onResult(String result) {
                mNbaGames = new ArrayList<>();
                mNbaGames.addAll(getGamesListFromJson(result));
                setGameAdapter();
                mRecyclerView.setAdapter(mGameAdapter);
                linlaHeaderProgress.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);

                if (mNbaGames.size() == 0) {
                    mNoGamesText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String error) {
                if (MyDebug.LOG) {
                    Log.d(TAG, "Volley error when loading game data: " + error);
                }
                linlaHeaderProgress.setVisibility(View.GONE);
                showSnackBar("Could not load game data", true /* retry */);
            }
        };

        gameDataService = new JSONGameDataService();
        gameDataService.fetchGames(date, listener);
    }

    private void updateGameData(String jsonString) {
        if (mGameAdapter != null) {
            mGameAdapter.swap(getGamesListFromJson(jsonString));
        }
    }

    private List<NBAGame> getGamesListFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, new TypeToken<List<NBAGame>>(){}.getType());
    }

    private void setGameAdapter() {
        mGameAdapter = new GameAdapter(mContext, mNbaGames, new GameAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), CommentsActivity.class);
                intent.putExtra(GAME_THREAD_HOME, mNbaGames.get(position).getHomeTeamAbbr());
                intent.putExtra(GAME_THREAD_AWAY, mNbaGames.get(position).getAwayTeamAbbr());
                intent.putExtra(GAME_ID, mNbaGames.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void setNavigatorDate(Date date) {
        mNavigatorDate.setText(DateFormatUtil.formatNavigatorDate(date));
    }

    private void showSnackBar(String message, boolean retry) {
        snackbar = Snackbar.make(rootView, message,
                Snackbar.LENGTH_INDEFINITE);
        if (retry) {
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadGameData(mSelectedDate.getTime());
                    setNavigatorDate(mSelectedDate.getTime());
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
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                loadGameData(mSelectedDate.getTime());
                setNavigatorDate(mSelectedDate.getTime());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
