package com.gmail.jorgegilcavazos.ballislife.network.API;

import com.gmail.jorgegilcavazos.ballislife.features.model.NbaGame;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NbaGamesService {

    @GET("nba/{date}/games.json")
    Observable<List<NbaGame>> listGames(@Path("date") String date);

}
