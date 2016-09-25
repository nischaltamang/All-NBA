package com.example.jorgegil.closegamealert.Utils;

import com.example.jorgegil.closegamealert.Network.GetRequestListener;

/**
 * Interface to get data of previous or upcoming NBA games.
 */
public interface GameDataService {

    /**
     * Fetches a list of NBA Games from the most relevant game day.
     */
    void fetchGames(GetRequestListener listener);

    /**
     * Fetches a list of NBA Games given a date.
     */
    void fetchGames(String date, GetRequestListener listener);

    /**
     * Cancels all network operations started by this service.
     */
    void cancel();
}
