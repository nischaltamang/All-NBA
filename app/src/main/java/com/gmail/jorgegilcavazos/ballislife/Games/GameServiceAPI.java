package com.gmail.jorgegilcavazos.ballislife.Games;

import com.gmail.jorgegilcavazos.ballislife.General.NBAGame;

import java.util.Date;
import java.util.List;

public interface GameServiceApi {

    interface GameServiceCallback<T> {
        void onLoaded(T games);

        void onFailure(String error);
    }

    void getAllGames(Date date, GameServiceCallback<List<NBAGame>> callback);
}
