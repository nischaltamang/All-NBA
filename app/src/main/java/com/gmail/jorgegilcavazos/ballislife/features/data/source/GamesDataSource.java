package com.gmail.jorgegilcavazos.ballislife.features.data.source;

import android.support.annotation.Nullable;

import com.gmail.jorgegilcavazos.ballislife.features.data.AsyncLoaderResult;
import com.gmail.jorgegilcavazos.ballislife.features.data.NbaGame;

import java.util.List;

/**
 * Created by jorgegil on 12/29/16.
 */

public interface GamesDataSource {

    @Nullable
    AsyncLoaderResult<List<NbaGame>> getGames(Long selectedDate);

    void refreshGames();

    void changeSelectedDate(Long selectedDate);
}
