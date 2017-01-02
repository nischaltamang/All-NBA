package com.gmail.jorgegilcavazos.ballislife.network;

import com.gmail.jorgegilcavazos.ballislife.features.data.NbaGame;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by jorgegil on 12/29/16.
 */

public interface NbaGamesService {
    @GET("scoreboard.php?date=")
    Call<List<NbaGame>> listGames(@Query("date") String date);
}
