package com.gmail.jorgegilcavazos.ballislife.features.data.source.remote;

import android.support.annotation.Nullable;

import com.gmail.jorgegilcavazos.ballislife.features.data.AsyncLoaderResult;
import com.gmail.jorgegilcavazos.ballislife.features.data.NbaGame;
import com.gmail.jorgegilcavazos.ballislife.features.data.source.GamesDataSource;
import com.gmail.jorgegilcavazos.ballislife.network.NbaGamesService;
import com.gmail.jorgegilcavazos.ballislife.util.DateFormatUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jorgegil on 12/29/16.
 */

public class GamesRemoteDataSource implements GamesDataSource {

    private static GamesRemoteDataSource INSTANCE;

    public static GamesRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GamesRemoteDataSource();
        }
        return INSTANCE;
    }

    private GamesRemoteDataSource() {
        // Empty private constructor to avoid direct instantiation.
    }

    @Nullable
    @Override
    public AsyncLoaderResult<List<NbaGame>> getGames(Long selectedDate) {

        // Convert millis to String "yyyy/MM/dd".
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selectedDate);
        String dashedDate = DateFormatUtil.getDashedDateString(cal.getTime());

        AsyncLoaderResult asyncLoaderResult = new AsyncLoaderResult();

        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("http://phpstack-4722-10615-67130.cloudwaysapps.com/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NbaGamesService gamesService = retrofit.create(NbaGamesService.class);
        Call<List<NbaGame>> gamesCall = gamesService.listGames(dashedDate);

        Response<List<NbaGame>> response;
        try {
            response = gamesCall.execute();
            if (response.isSuccessful()) {
                asyncLoaderResult.setData(response.body());
            }

        } catch (IOException e) {
           asyncLoaderResult.setException(e);
        }

        return asyncLoaderResult;
    }

    @Override
    public void refreshGames() {
        // Not necessary because the {@link GamesRepository} handles the refreshing of data.
    }

    @Override
    public void changeSelectedDate(Long selectedDate) {
        // Not necessary because the {@link GamesRepository} handles changing the selected date.
    }

}
