package com.gmail.jorgegilcavazos.ballislife.features.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gmail.jorgegilcavazos.ballislife.features.data.AsyncLoaderResult;
import com.gmail.jorgegilcavazos.ballislife.features.data.NbaGame;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by jorgegil on 12/29/16.
 */

public class GamesRepository implements GamesDataSource {

    private static GamesRepository INSTANCE = null;

    private final GamesDataSource mGamesRemoteDataSource;

    private List<GamesRepositoryObserver> mObservers = new ArrayList<>();

    public static GamesRepository getInstance(GamesDataSource gamesRemoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new GamesRepository(gamesRemoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    // Prevent remote instantiation.
    private GamesRepository(@NonNull GamesDataSource gamesRemoteDataSource) {
        mGamesRemoteDataSource = checkNotNull(gamesRemoteDataSource);
    }

    public void addContentObserver(GamesRepositoryObserver observer) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    public void removeContentObserver(GamesRepositoryObserver observer) {
        if (mObservers.contains(observer)) {
            mObservers.remove(observer);
        }
    }

    private void notifyContentObserver() {
        for (GamesRepositoryObserver observer : mObservers) {
            observer.onGamesChanged();
        }
    }

    private void notifyContentObserverOfChangedDate(Long selectedDate) {
        for (GamesRepositoryObserver observer : mObservers) {
            observer.onDateChanged(selectedDate);
        }
    }

    @Nullable
    @Override
    public AsyncLoaderResult<List<NbaGame>> getGames(Long selectedDate) {
        return mGamesRemoteDataSource.getGames(selectedDate);
    }

    @Override
    public void refreshGames() {
        notifyContentObserver();
    }

    @Override
    public void changeSelectedDate(Long selectedDate) {
        notifyContentObserverOfChangedDate(selectedDate);
    }

    public interface GamesRepositoryObserver {

        void onGamesChanged();

        void onDateChanged(Long selectedDate);
    }
}
