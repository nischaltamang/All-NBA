package com.gmail.jorgegilcavazos.ballislife.network.API;

import com.gmail.jorgegilcavazos.ballislife.features.model.NbaGame;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NbaGamesService {

    @GET("scoreboard.php?date=")
    Observable<List<NbaGame>> listGames(@Query("date") String date);

}
