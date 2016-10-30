package com.gmail.jorgegilcavazos.ballislife.Games;

import com.gmail.jorgegilcavazos.ballislife.General.NBAGame;

import java.util.Date;
import java.util.List;

public class ServiceGameRepository implements GamesRepository {

    private final GameServiceApi mGameServiceApi;

    public ServiceGameRepository(GameServiceApi gameServiceApi) {
        mGameServiceApi = gameServiceApi;
    }

    @Override
    public void getGames(Date date, final LoadGamesCallback callback) {
        mGameServiceApi.getAllGames(date, new GameServiceApi.GameServiceCallback<List<NBAGame>>() {
            @Override
            public void onLoaded(List<NBAGame> games) {
                callback.onGamesLoaded(games);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }
}
