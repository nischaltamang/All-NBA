package com.example.jorgegil.closegamealert.Utils;

import com.example.jorgegil.closegamealert.Network.GetRequestListener;

import java.util.Date;

/**
 * Interface to get data of previous or upcoming NBA games.
 */
public interface GameDataService {

    /**
     * Fetches a list of NBA Games.
     */
    void fetchGames(Date date, GetRequestListener listener);

    /**
     * Cancels all network operations started by this service.
     */
    void cancel();
}
