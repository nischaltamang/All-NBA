package com.example.jorgegil.closegamealert;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
    public final static String GAME_THREAD_ID = "com.example.jorgegil.closegamealert.GAME_THREAD_ID";
    public final static String GAME_THREAD_HOME = "com.example.jorgegil.closegamealert.GAME_THREAD_HOME";
    public final static String GAME_THREAD_AWAY = "com.example.jorgegil.closegamealert.GAME_THREAD_AWAY";

    private GCMClientManager pushClientManager;
    String PROJECT_NUMBER = "532852092546";
    String url = "https://www.reddit.com/r/nba/search.json?sort=new&restrict_sr=on&q=flair%3AGame%2BThread";

    String[] gameThreadId;
    String[] gameThreadTitle;

    String[] homeTeam;
    String[] awayTeam;
    String[] homeScore;
    String[] awayScore;
    String[] clock;
    String[] period;



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

    public void getGameThreads(final int numOfEvents) {
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseGameThreads(response, numOfEvents);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                Log.d("AUTOGT", "getGTErr: " + error);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    public void parseGameThreads(String response, int numOfEvents) {

        try {
            gameThreadId = new String[numOfEvents];
            for (int i = 0; i < numOfEvents; i++){
                gameThreadId[i] = "No Game Thread found...";
            }

            TeamNames tn = new TeamNames();
            JSONArray r = new JSONObject(response).getJSONObject("data").getJSONArray("children");

            for (int i = 0; i < numOfEvents; i++) {
                JSONObject data = r.getJSONObject(i).getJSONObject("data");
                for (int j = 0; j < numOfEvents; j++) {
                    if (data.getString("title").contains("GAME THREAD")
                            && data.getString("title").contains(tn.getName(homeTeam[j]))
                            && data.getString("title").contains(tn.getName(awayTeam[j]))) {
                        gameThreadId[j] = data.getString("id");
                    }
                }
            }

            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(new CustomAdapter(this, homeTeam, awayTeam, homeScore,
                    awayScore, clock, period));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                    Log.d("POS", "I=> " + i);
                    intent.putExtra(GAME_THREAD_ID, gameThreadId[i]);
                    intent.putExtra(GAME_THREAD_HOME, homeTeam[i]);
                    intent.putExtra(GAME_THREAD_AWAY, awayTeam[i]);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            Log.d("AUTOGT", "Error: " + e.getMessage());
        }

    }

    public void parseData(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            int numOfEvents = jsonArray.length();

            homeTeam = new String[numOfEvents];
            awayTeam = new String[numOfEvents];
            homeScore = new String[numOfEvents];
            awayScore = new String[numOfEvents];
            clock = new String[numOfEvents];
            period = new String[numOfEvents];

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

            getGameThreads(numOfEvents);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
