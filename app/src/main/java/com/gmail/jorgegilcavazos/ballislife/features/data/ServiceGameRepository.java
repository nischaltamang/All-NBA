package com.gmail.jorgegilcavazos.ballislife.features.data;

import java.util.Date;
import java.util.List;

/**
 * Implementation of the {@link GamesRepository} to load games from a data source.
 */
public class ServiceGameRepository implements GamesRepository {

    private final GameServiceApi gameServiceApi;

    public ServiceGameRepository(GameServiceApi gameServiceApi) {
        this.gameServiceApi = gameServiceApi;
    }

    @Override
    public void getGames(Date date, final LoadGamesCallback callback) {
        // Load games from the API.
        gameServiceApi.getAllGames(date, new GameServiceApi.GameServiceCallback<List<NbaGame>>() {
            @Override
            public void onLoaded(List<NbaGame> games) {
                callback.onGamesLoaded(games);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }
}
