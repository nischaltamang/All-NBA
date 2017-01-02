package com.gmail.jorgegilcavazos.ballislife.features.data.source;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.gmail.jorgegilcavazos.ballislife.features.data.AsyncLoaderResult;
import com.gmail.jorgegilcavazos.ballislife.features.data.NbaGame;

import java.util.Calendar;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by jorgegil on 12/29/16.
 */

public class GamesLoader extends AsyncTaskLoader<AsyncLoaderResult<List<NbaGame>>>
        implements GamesRepository.GamesRepositoryObserver {

    private GamesRepository mRepository;
    private Long mSelectedDate;

    public GamesLoader(Context context, @NonNull GamesRepository repository) {
        super(context);
        checkNotNull(repository);
        mRepository = repository;
        mSelectedDate = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    protected void onStartLoading() {
        mRepository.addContentObserver(this);

        forceLoad();
    }

    @Override
    public AsyncLoaderResult<List<NbaGame>> loadInBackground() {
        Log.d("Loader", "loading");
        return mRepository.getGames(mSelectedDate);
    }

    @Override
    public void deliverResult(AsyncLoaderResult<List<NbaGame>> data) {
        if (isReset()) {
            return;
        }

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        mRepository.removeContentObserver(this);
    }

    @Override
    public void onGamesChanged() {
        if (isStarted()) {
            forceLoad();
        }
    }

    @Override
    public void onDateChanged(Long selectedDate) {
        if (isStarted()) {
            mSelectedDate = selectedDate;
            forceLoad();
        }
    }
}
