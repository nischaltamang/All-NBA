package com.example.jorgegil.closegamealert;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by jorgegil on 11/29/15.
 */
public class RemoteData {

    static Context context;
    static String result;

    public RemoteData(Context context) {
        this.context = context;
    }

    public static String readContents(String url) {

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                result = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                result = error.getMessage();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);

        return result;
    }

}
