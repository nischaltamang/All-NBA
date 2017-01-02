package com.gmail.jorgegilcavazos.ballislife.features.games;

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
import android.widget.TextView;

import com.gmail.jorgegilcavazos.ballislife.features.data.NbaGame;
import com.gmail.jorgegilcavazos.ballislife.R;
import com.gmail.jorgegilcavazos.ballislife.util.DateFormatUtil;
import com.gmail.jorgegilcavazos.ballislife.features.gamethread.CommentsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Displays a list of {@link NbaGame}s for the selected date.
 */
public class GamesFragment extends Fragment implements GamesContract.View {
    public final static String TAG = "GamesFragment";

    public final static String GAME_THREAD_HOME = "GAME_THREAD_HOME";
    public final static String GAME_THREAD_AWAY = "GAME_THREAD_AWAY";
    public final static String GAME_ID = "GAME_ID";

    private GamesContract.Presenter mPresenter;

    @BindView(R.id.navigator_button_left) ImageButton btnPrevDay;
    @BindView(R.id.navigator_button_right) ImageButton btnNextDay;
    @BindView(R.id.navigator_text) TextView tvNavigatorDate;
    @BindView(R.id.no_games_text) TextView tvNoGames;
    @BindView(R.id.games_rv) RecyclerView rvGames;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private RecyclerView.LayoutManager lmGames;
    private GameAdapter gameAdapter;
    private Snackbar snackbar;
    private Unbinder unbinder;

    public GamesFragment() {
        // Required empty public constructor.
    }

    public static GamesFragment newInstance() {
        return new GamesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        gameAdapter = new GameAdapter(new ArrayList<NbaGame>(0), gameItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();

        // Register Broadcast manager to update scores automatically
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("game-data"));
    }

    @Override
    public void setPresenter(GamesContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.games_fragment_title);
        view = inflater.inflate(R.layout.fragment_games, container, false);
        unbinder = ButterKnife.bind(this, view);

        lmGames = new LinearLayoutManager(getContext());
        rvGames.setLayoutManager(lmGames);
        rvGames.setAdapter(gameAdapter);

        btnPrevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addOrSubstractDay(-1);
            }
        });

        btnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addOrSubstractDay(1);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadGames(true);
            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mPresenter.loadGames(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        mPresenter.dismissSnackbar();
        super.onPause();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // When new data is received, the JSON is parsed and the ListView is notified of change.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isVisible()) {
                mPresenter.updateGames(intent.getStringExtra("message"));
            }
        }
    };

    private GameItemListener gameItemListener = new GameItemListener() {
        @Override
        public void onGameClick(NbaGame clickedGame) {
            mPresenter.openGameDetails(clickedGame);
        }
    };

    @Override
    public void setLoadingIndicator(boolean active) {
        if (getView() == null) {
            return;
        }
        swipeRefreshLayout.setRefreshing(active);
    }

    @Override
    public void setDateNavigatorText(String dateText) {
        tvNavigatorDate.setText(dateText);
    }

    @Override
    public void hideGames() {
        if (getView() == null) {
            return;
        }
        rvGames.setVisibility(View.GONE);
    }

    @Override
    public void showGames(List<NbaGame> games) {
        gameAdapter.swap(games);
        rvGames.setVisibility(View.VISIBLE);
    }

    @Override
    public void showGameDetails(NbaGame game) {
        Intent intent = new Intent(getActivity(), CommentsActivity.class);
        intent.putExtra(GAME_THREAD_HOME, game.getHomeTeamAbbr());
        intent.putExtra(GAME_THREAD_AWAY, game.getAwayTeamAbbr());
        intent.putExtra(GAME_ID, game.getId());
        startActivity(intent);
    }

    @Override
    public void setNoGamesIndicator(boolean active) {
        if (active) {
            tvNoGames.setVisibility(View.VISIBLE);
        } else {
            tvNoGames.setVisibility(View.GONE);
        }
    }

    @Override
    public void showSnackbar(boolean canReload) {
        if (getView() != null) {
            snackbar = Snackbar.make(getView(), R.string.failed_game_data, Snackbar.LENGTH_INDEFINITE);
            if (canReload) {
                snackbar.setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mPresenter.loadGames(selectedDate, true);
                        mPresenter.loadGames(true);
                    }
                });
            }
        }
        snackbar.show();
    }

    @Override
    public void dismissSnackbar() {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    public interface GameItemListener {
        void onGameClick(NbaGame clickedGame);
    }
}
