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
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

// TODO: Use View Holder pattern instead of list view with adapter.
public class GamesFragment extends Fragment {
    public final static String TAG = "GamesFragment";

    public final static String GAME_THREAD_HOME =
            "com.example.jorgegil.closegamealert.GAME_THREAD_HOME";
    public final static String GAME_THREAD_AWAY =
            "com.example.jorgegil.closegamealert.GAME_THREAD_AWAY";
    public final static String GAME_ID = "com.example.jorgegil.closegamealert.GAME_ID";

    @BindView(R.id.navigator_text) TextView tvNavigatorDate;
    @BindView(R.id.no_games_text) TextView tvNoGames;
    @BindView(R.id.games_rv) RecyclerView rvGames;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private View view;
    private RecyclerView.LayoutManager lmGames;
    private GameAdapter gameAdapter;
    private GameDataService gameDataService;
    private Snackbar snackbar;
    private List<NBAGame> nbaGamesList;
    private Calendar selectedDate;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_games, container, false);
        unbinder = ButterKnife.bind(this, view);

        lmGames = new LinearLayoutManager(context);
        rvGames.setLayoutManager(lmGames);

        getActivity().setTitle(R.string.games_fragment_title);

        ImageButton datePrevBtn = (ImageButton) view.findViewById(R.id.navigator_button_left);
        datePrevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate == null) {
                    selectedDate = Calendar.getInstance();
                }
                selectedDate.add(Calendar.DAY_OF_YEAR, -1);
                setNavigatorDate(selectedDate.getTime());
                loadGameData(selectedDate.getTime());
            }
        });
        ImageButton dateNextBtn = (ImageButton) view.findViewById(R.id.navigator_button_right);
        dateNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate == null) {
                    selectedDate = Calendar.getInstance();
                }
                selectedDate.add(Calendar.DAY_OF_YEAR, 1);
                setNavigatorDate(selectedDate.getTime());
                loadGameData(selectedDate.getTime());
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadGameData(selectedDate.getTime());
                setNavigatorDate(selectedDate.getTime());
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        selectedDate = Calendar.getInstance();
        setNavigatorDate(selectedDate.getTime());
        loadGameData(selectedDate.getTime());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register Broadcast manager to update scores automatically
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("game-data"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // When new data is received, the JSON is parsed and the listview is notified of change.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isVisible() && DateFormatUtil.isDateToday(selectedDate.getTime())) {
                String message = intent.getStringExtra("message");
                updateGameData(message);
            }
        }
    };

    private void loadGameData(Date date) {
        swipeRefreshLayout.setRefreshing(true);
        rvGames.setVisibility(View.GONE);
        tvNoGames.setVisibility(View.GONE);

        GetRequestListener listener = new GetRequestListener() {
            @Override
            public void onResult(String result) {
                nbaGamesList = new ArrayList<>();
                nbaGamesList.addAll(getGamesListFromJson(result));
                setGameAdapter();
                rvGames.setAdapter(gameAdapter);
                rvGames.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);

                if (nbaGamesList.size() == 0) {
                    tvNoGames.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String error) {
                if (MyDebug.LOG) {
                    Log.d(TAG, "Volley error when loading game data: " + error);
                }
                swipeRefreshLayout.setRefreshing(false);
                showSnackBar("Could not load game data", true /* retry */);
            }
        };

        gameDataService = new JSONGameDataService();
        gameDataService.fetchGames(date, listener);
    }

    private void updateGameData(String jsonString) {
        if (gameAdapter != null) {
            gameAdapter.swap(getGamesListFromJson(jsonString));
        }
    }

    private List<NBAGame> getGamesListFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, new TypeToken<List<NBAGame>>(){}.getType());
    }

    private void setGameAdapter() {
        gameAdapter = new GameAdapter(context, nbaGamesList, new GameAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), CommentsActivity.class);
                intent.putExtra(GAME_THREAD_HOME, nbaGamesList.get(position).getHomeTeamAbbr());
                intent.putExtra(GAME_THREAD_AWAY, nbaGamesList.get(position).getAwayTeamAbbr());
                intent.putExtra(GAME_ID, nbaGamesList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void setNavigatorDate(Date date) {
        tvNavigatorDate.setText(DateFormatUtil.formatNavigatorDate(date));
    }

    private void showSnackBar(String message, boolean retry) {
        snackbar = Snackbar.make(view, message,
                Snackbar.LENGTH_INDEFINITE);
        if (retry) {
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadGameData(selectedDate.getTime());
                    setNavigatorDate(selectedDate.getTime());
                }
            });
        }
        swipeRefreshLayout.setRefreshing(false);
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
                loadGameData(selectedDate.getTime());
                setNavigatorDate(selectedDate.getTime());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
