package com.gmail.jorgegilcavazos.ballislife.features.games;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.gmail.jorgegilcavazos.ballislife.features.data.AsyncLoaderResult;
import com.gmail.jorgegilcavazos.ballislife.features.data.source.GamesLoader;
import com.gmail.jorgegilcavazos.ballislife.features.data.NbaGame;
import com.gmail.jorgegilcavazos.ballislife.features.data.source.GamesRepository;
import com.gmail.jorgegilcavazos.ballislife.util.DateFormatUtil;
import com.gmail.jorgegilcavazos.ballislife.util.GameUtils;

import java.util.Calendar;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class GamesPresenter implements GamesContract.Presenter,
        LoaderManager.LoaderCallbacks<AsyncLoaderResult<List<NbaGame>>> {

    private static final int GAMES_QUERY = 1;

    private final GamesContract.View mGamesView;
    private final GamesRepository mGamesRepository;
    private final GamesLoader mGamesLoader;
    private final LoaderManager mLoaderManager;

    private List<NbaGame> mCurrentGames;
    private Calendar mSelectedDate;

    public GamesPresenter(@NonNull GamesLoader gamesLoader,
                          @NonNull LoaderManager loaderManager,
                          @NonNull GamesContract.View gamesView,
                          @NonNull GamesRepository gamesRepository) {
        mGamesLoader = checkNotNull(gamesLoader);
        mLoaderManager = checkNotNull(loaderManager);
        mGamesView = checkNotNull(gamesView);
        mGamesRepository = checkNotNull(gamesRepository);
        mSelectedDate = Calendar.getInstance();

        mGamesView.setPresenter(this);
    }

    @Override
    public void start() {
        loadDateNavigatorText();

        mLoaderManager.initLoader(GAMES_QUERY, null, this);
        loadGames(true);
    }

    @Override
    public Loader<AsyncLoaderResult<List<NbaGame>>> onCreateLoader(int id, Bundle args) {
        mGamesView.dismissSnackbar();
        mGamesView.hideGames();
        mGamesView.setNoGamesIndicator(false);
        mGamesView.setLoadingIndicator(true);

        return mGamesLoader;
    }

    @Override
    public void onLoadFinished(Loader<AsyncLoaderResult<List<NbaGame>>> loader,
                               AsyncLoaderResult<List<NbaGame>> data) {
        Log.d("Presenter", "loadFinished");
        mGamesView.setLoadingIndicator(false);

        Exception exception = data.getException();
        if (exception != null) {
            mCurrentGames = null;
        } else {
            mCurrentGames = data.getData();
        }

        if (mCurrentGames == null) {
            mGamesView.showSnackbar(true);
        } else if (mCurrentGames.size() == 0) {
            mGamesView.setNoGamesIndicator(true);
        } else {
            mGamesView.showGames(mCurrentGames);
        }
    }

    @Override
    public void onLoaderReset(Loader<AsyncLoaderResult<List<NbaGame>>> loader) {
        // TODO: what goes here?
    }

    @Override
    public void loadGames(boolean forceUpdate) {
        if (forceUpdate) {
            mGamesView.dismissSnackbar();
            mGamesView.hideGames();
            mGamesView.setNoGamesIndicator(false);
            mGamesView.setLoadingIndicator(true);

            mGamesRepository.refreshGames();
        }
    }

    @Override
    public void addOrSubstractDay(int delta) {
        mGamesView.dismissSnackbar();
        mGamesView.hideGames();
        mGamesView.setNoGamesIndicator(false);
        mGamesView.setLoadingIndicator(true);

        mSelectedDate.add(Calendar.DAY_OF_YEAR, delta);
        loadDateNavigatorText();

        mGamesRepository.changeSelectedDate(mSelectedDate.getTimeInMillis());
    }

    @Override
    public void loadDateNavigatorText() {
        String dateText = DateFormatUtil.formatNavigatorDate(mSelectedDate.getTime());
        mGamesView.setDateNavigatorText(dateText);
    }

    @Override
    public void openGameDetails(NbaGame requestedGame) {
        mGamesView.showGameDetails(requestedGame);
    }

    @Override
    public void updateGames(String gameData) {
        if (DateFormatUtil.isDateToday(mSelectedDate.getTime())) {
            mGamesView.showGames(GameUtils.getGamesListFromJson(gameData));
        }
    }

    @Override
    public void dismissSnackbar() {
        mGamesView.dismissSnackbar();
    }

}
