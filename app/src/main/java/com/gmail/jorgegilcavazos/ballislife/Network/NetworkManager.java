package com.gmail.jorgegilcavazos.ballislife.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Singleton to make network call using the Volley Library.
 */
public class NetworkManager {
    private static final String TAG = "NetworkManager";
    private static NetworkManager instance = null;

    public RequestQueue requestQueue;

    private NetworkManager(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
                    "is not initialized, call getInstance(...) first.");
        }
        return instance;
    }

    public void makeGetRequest(String url, String requestTag, final GetRequestListener listener) {
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            listener.onResult(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onFailure(error.toString());
                    }
                }
        );
        request.setTag(requestTag);
        requestQueue.add(request);
    }

    public void cancelAllRequests(String requestTag) {
        requestQueue.cancelAll(requestTag);
    }
}
