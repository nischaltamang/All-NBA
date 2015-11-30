package com.example.jorgegil.closegamealert;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by jorgegil on 11/29/15.
 */
public class RemoteData {

    Context context;
    String result;

    public RemoteData(Context context) {
        this.context = context;
    }

    public String readContents(String url) {

        result = "";

        Log.d("PASS", url);

        StringRequest request = new StringRequest("https://www.reddit.com/r/nba/comments/3urm4v/.json", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
        }
        });

        Log.d("PASS", "BEFORE QUEUE");

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

        return null;
    }

}
