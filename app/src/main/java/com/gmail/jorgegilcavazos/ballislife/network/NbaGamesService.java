package com.gmail.jorgegilcavazos.ballislife.network;

import com.gmail.jorgegilcavazos.ballislife.features.data.NbaGame;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NbaGamesService {

    @GET("scoreboard.php?date=")
    Observable<List<NbaGame>> listGames(@Query("date") String date);

}
