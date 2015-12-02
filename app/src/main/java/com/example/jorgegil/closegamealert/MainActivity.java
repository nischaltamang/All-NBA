package com.example.jorgegil.closegamealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public final static String GAME_THREAD_ID = "com.example.jorgegil.closegamealert.GAME_THREAD_ID";
    public final static String GAME_THREAD_HOME = "com.example.jorgegil.closegamealert.GAME_THREAD_HOME";
    public final static String GAME_THREAD_AWAY = "com.example.jorgegil.closegamealert.GAME_THREAD_AWAY";

    private GCMClientManager pushClientManager;
    String PROJECT_NUMBER = "532852092546";
    String url = "https://www.reddit.com/r/nba/search.json?sort=new&restrict_sr=on&q=flair%3AGame%2BThread";

    //String[] gameThreadId;
    ArrayList<String> gameThreadId;
    //String[] homeTeam;
    ArrayList<String> homeTeam;
    //String[] awayTeam;
    ArrayList<String> awayTeam;
    //String[] homeScore;
    ArrayList<String> homeScore;
    //String[] awayScore;
    ArrayList<String> awayScore;
    //String[] clock;
    ArrayList<String> clock;
    //String[] period;
    ArrayList<String> period;

    ListView listView;
    LinearLayout linlaHeaderProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setUpToolbar();

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

        listView = (ListView) findViewById(R.id.listView);
        gameThreadId = new ArrayList<>();
        homeTeam = new ArrayList<>();
        awayTeam = new ArrayList<>();
        homeScore = new ArrayList<>();
        awayScore = new ArrayList<>();
        clock = new ArrayList<>();
        period = new ArrayList<>();

        loadGameData();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("game-data"));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.d("recevier", "Got message: " + message);

            parseData(message);
        }
    };

    public void loadGameData() {
        gameThreadId.clear();

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);

        StringRequest request = new StringRequest("http://phpstack-4722-10615-67130.cloudwaysapps.com/GameData.txt", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseData(response);
                System.out.println("JSON:" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public void parseData(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            int numOfEvents = jsonArray.length();

            homeTeam.clear();
            awayTeam.clear();
            homeScore.clear();
            awayScore.clear();
            clock.clear();
            period.clear();

            for(int i = 0; i < numOfEvents; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                homeTeam.add(jsonObject.getString("homeTeam"));
                awayTeam.add(jsonObject.getString("awayTeam"));
                homeScore.add(jsonObject.getString("homeScore"));
                awayScore.add(jsonObject.getString("awayScore"));
                clock.add(jsonObject.getString("clock"));
                period.add(jsonObject.getString("period"));

                if (homeScore.get(i).equals("")) {
                    homeScore.set(i, "0");
                }

                if (awayScore.get(i).equals("")) {
                    awayScore.set(i, "0");
                }

            }

            Log.d("gameThreadId", "size: " + gameThreadId.size());
            if (gameThreadId.size() == 0) {
                getGameThreads(numOfEvents);
            } else {
                ((CustomAdapter) listView.getAdapter()).notifyDataSetChanged();
                Log.d("adapter", "notified of change");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            for (int i = 0; i < numOfEvents; i++){
                gameThreadId.add("No Game Thread found...");
            }

            TeamNames tn = new TeamNames();
            JSONArray r = new JSONObject(response).getJSONObject("data").getJSONArray("children");

            for (int i = 0; i < numOfEvents; i++) {
                if (!gameThreadId.get(i).equals("No Game Thread found...")) {
                    JSONObject data = r.getJSONObject(i).getJSONObject("data");
                    for (int j = 0; j < numOfEvents; j++) {
                        if (data.getString("title").contains("GAME THREAD")
                                && data.getString("title").contains(tn.getName(homeTeam.get(j)))
                                && data.getString("title").contains(tn.getName(awayTeam.get(j)))) {
                            gameThreadId.set(j, data.getString("id"));
                        }
                    }
                }
            }

            for (int i = 0; i < numOfEvents; i++){
                Log.d("GAMEID", i + " -> " + gameThreadId.get(i));
            }


            listView.setAdapter(new CustomAdapter(this, homeTeam, awayTeam, homeScore,
                    awayScore, clock, period));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                    intent.putExtra(GAME_THREAD_ID, gameThreadId.get(i));
                    intent.putExtra(GAME_THREAD_HOME, homeTeam.get(i));
                    intent.putExtra(GAME_THREAD_AWAY, awayTeam.get(i));
                    startActivity(intent);
                }
            });

            setProgressBarIndeterminateVisibility(false);
            linlaHeaderProgress.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            Log.d("gameThreadId", "sizeafter: " + gameThreadId.size());


        } catch (Exception e) {
            Log.d("AUTOGT", "Error: " + e.getMessage());
        }

    }

    private void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_menu_white);
        ab.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                loadGameData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //loadGameData();
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

}
