package com.example.jorgegil.closegamealert.Utils;

import com.example.jorgegil.closegamealert.Network.GetRequestListener;

import java.util.Date;

/**
 * Interface to get data of previous or upcoming NBA games.
 */
public interface GameDataService {

    /**
     * Loads a list of NBA Games.
     */
    void fetchGames(Date date, GetRequestListener listener);
}
