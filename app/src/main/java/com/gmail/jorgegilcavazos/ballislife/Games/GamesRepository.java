package com.gmail.jorgegilcavazos.ballislife.Games;

import com.gmail.jorgegilcavazos.ballislife.General.NBAGame;

import java.util.Date;
import java.util.List;

public interface GamesRepository {

    interface LoadGamesCallback {
        void onGamesLoaded(List<NBAGame> games);

        void onFailure(String error);
    }

    void getGames(Date date, LoadGamesCallback callback);
}
