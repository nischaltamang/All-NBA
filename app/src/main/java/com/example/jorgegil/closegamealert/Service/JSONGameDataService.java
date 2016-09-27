package com.example.jorgegil.closegamealert.Service;

import com.example.jorgegil.closegamealert.Network.GetRequestListener;
import com.example.jorgegil.closegamealert.Network.NetworkManager;
import com.example.jorgegil.closegamealert.Service.GameDataService;

/**
 * JSON implementation of the {@link GameDataService} interface.
 */
public class JSONGameDataService implements GameDataService {
    private final static String BASE_URL = "http://phpstack-4722-10615-67130.cloudwaysapps.com/api/v1/";
    private final static String REQUEST_TAG = "FETCH_GAMES";

    @Override
    public void fetchGames(GetRequestListener listener) {
        String url = BASE_URL + "scoreboard.json";
        NetworkManager.getInstance().makeGetRequest(url, REQUEST_TAG, listener);
    }

    @Override
    public void fetchGames(String date, GetRequestListener listener) {
        String url = BASE_URL + "scoreboard.php" + "?date=" + date;
        NetworkManager.getInstance().makeGetRequest(url, REQUEST_TAG, listener);
    }

    @Override
    public void cancel() {
        NetworkManager.getInstance().cancelAllRequests(REQUEST_TAG);
    }
}
