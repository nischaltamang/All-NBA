package com.gmail.jorgegilcavazos.ballislife.network;

/**
 * Listens to results from a GET request.
 */
public interface GetRequestListener {
    void onResult(String result);

    void onFailure(String error);
}
