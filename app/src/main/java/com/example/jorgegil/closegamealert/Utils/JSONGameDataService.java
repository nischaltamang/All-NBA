package com.example.jorgegil.closegamealert.Utils;

import com.example.jorgegil.closegamealert.Network.GetRequestListener;
import com.example.jorgegil.closegamealert.Network.NetworkManager;

import java.util.Date;

/**
 * JSON implementation of the {@link GameDataService} interface.
 */
public class JSONGameDataService implements GameDataService {
    public final static String GAME_DATA_URL = "http://phpstack-4722-10615-67130.cloudwaysapps.com/GameData.txt";

    @Override
    public void fetchGames(Date date, GetRequestListener listener) {
        NetworkManager.getInstance().makeGetRequest(GAME_DATA_URL, listener);
    }
}
