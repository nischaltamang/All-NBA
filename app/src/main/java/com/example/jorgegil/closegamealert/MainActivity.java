package com.example.jorgegil.closegamealert;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private GCMClientManager pushClientManager;
    String PROJECT_NUMBER = "532852092546";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {

            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {


                if (isNewRegistration) {

                    Toast.makeText(MainActivity.this, registrationId,
                            Toast.LENGTH_SHORT).show();

                    StringRequest sendRegId = new StringRequest("http://phpstack-4722-10615-67130.cloudwaysapps.com/gcm.php?shareRegId=1&regId=" + registrationId, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error " + error);
                        }
                    });

                    RequestQueue sendQueue = Volley.newRequestQueue(getApplicationContext());
                    sendQueue.add(sendRegId);
                }

            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
                // If there is an error registering, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off when retrying.
            }
        });

        StringRequest request = new StringRequest("http://phpstack-4722-10615-67130.cloudwaysapps.com/nbcsports.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseData(response);
                System.out.println("JSON:" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                System.out.println("JSON ERROR");
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);


    }

    public void parseData(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            int numOfEvents = jsonArray.length();

            String[] homeTeam = new String[numOfEvents];
            String[] awayTeam = new String[numOfEvents];
            String[] homeScore = new String[numOfEvents];
            String[] awayScore = new String[numOfEvents];
            String[] clock = new String[numOfEvents];
            String[] period = new String[numOfEvents];

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                homeTeam[i] = jsonObject.getString("homeTeam");
                awayTeam[i] = "" + jsonObject.getString("awayTeam");
                homeScore[i] = jsonObject.getString("homeScore");
                awayScore[i] = jsonObject.getString("awayScore");
                clock[i] = jsonObject.getString("clock");
                period[i] = jsonObject.getString("period");

                if (homeScore[i].equals("")) {
                    homeScore[i] = "0";
                }

                if (awayScore[i].equals("")) {
                    awayScore[i] = "0";
                }

            }

            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(new CustomAdapter(this, homeTeam, awayTeam, homeScore,
                    awayScore, clock, period));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
