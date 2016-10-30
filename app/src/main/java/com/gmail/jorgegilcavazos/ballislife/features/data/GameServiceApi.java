package com.gmail.jorgegilcavazos.ballislife.features.data;

import java.util.Date;
import java.util.List;

/**
 * Defines an interface to the games service API. All games data requests should be piped through
 * this interface.
 */
public interface GameServiceApi {

    interface GameServiceCallback<T> {
        void onLoaded(T games);

        void onFailure(String error);
    }

    void getAllGames(Date date, GameServiceCallback<List<NbaGame>> callback);
}
