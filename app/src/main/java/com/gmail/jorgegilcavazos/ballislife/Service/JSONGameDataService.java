package com.gmail.jorgegilcavazos.ballislife.Service;

import com.gmail.jorgegilcavazos.ballislife.Network.GetRequestListener;
import com.gmail.jorgegilcavazos.ballislife.Network.NetworkManager;
import com.gmail.jorgegilcavazos.ballislife.Utils.DateFormatUtil;

import java.util.Date;

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
    public void fetchGames(Date date, GetRequestListener listener) {
        String url = BASE_URL + "scoreboard.php" + "?date="
                + DateFormatUtil.getDashedDateString(date);
        NetworkManager.getInstance().makeGetRequest(url, REQUEST_TAG, listener);
    }

    @Override
    public void cancel() {
        NetworkManager.getInstance().cancelAllRequests(REQUEST_TAG);
    }
}
