package com.example.jorgegil.closegamealert;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import com.example.jorgegil.closegamealert.View.Fragments.StandingsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public final static String GAME_THREAD_HOME = "com.example.jorgegil.closegamealert.GAME_THREAD_HOME";
    public final static String GAME_THREAD_AWAY = "com.example.jorgegil.closegamealert.GAME_THREAD_AWAY";
    public final static String GAME_ID = "com.example.jorgegil.closegamealert.GAME_ID";

    private GCMClientManager pushClientManager;
    String PROJECT_NUMBER = "532852092546";

    ArrayList<String> homeTeam;
    ArrayList<String> awayTeam;
    ArrayList<String> homeScore;
    ArrayList<String> awayScore;
    ArrayList<String> clock;
    ArrayList<String> period;
    ArrayList<String> gameId;
    ArrayList<String> status;

    ListView listView;
    LinearLayout linlaHeaderProgress;
    Toolbar toolbar;
    ActionBar ab;

    DrawerLayout drawerLayout;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Show loading icon
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        setUpNavigationView();
        setUpDrawerContent();




        // Register client to GCM
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

        // Game data
        listView = (ListView) findViewById(R.id.listView);
        homeTeam = new ArrayList<>();
        awayTeam = new ArrayList<>();
        homeScore = new ArrayList<>();
        awayScore = new ArrayList<>();
        clock = new ArrayList<>();
        period = new ArrayList<>();
        gameId = new ArrayList<>();
        status = new ArrayList<>();

        loadGameData();

        // Register Broadcast manager to update scores automatically
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("game-data"));

    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // Show menu icon
            ab = getSupportActionBar();
            ab.setHomeAsUpIndicator(R.mipmap.ic_menu_white);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpNavigationView(){
        if (toolbar != null) {
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.navigation);
        }
    }

    private void setUpDrawerContent() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                setTitle(menuItem.getTitle());
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_5:
                        menuItem.setChecked(true);
                        setFragment(4);
                        listView.setVisibility(View.INVISIBLE);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                }
                return true;
            }
        });
    }

    public void setFragment(int position) {
        android.support.v4.app.FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 4:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                StandingsFragment standingsFragment = new StandingsFragment();
                fragmentTransaction.replace(R.id.fragment, standingsFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    // When new data is received, the JSON is parsed and the listview is notified of change
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.d("recevier", "Got message: " + message);

            parseData(message, false);
        }
    };

    // Called on full refreshing, adapter is reloaded (not refreshed)
    public void loadGameData() {

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);

        StringRequest request = new StringRequest("http://phpstack-4722-10615-67130.cloudwaysapps.com/GameData.txt", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseData(response, true);
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

    public void parseData(String response, boolean reload) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            int numOfEvents = jsonArray.length();

            homeTeam.clear();
            awayTeam.clear();
            homeScore.clear();
            awayScore.clear();
            clock.clear();
            period.clear();
            gameId.clear();
            status.clear();

            for(int i = 0; i < numOfEvents; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                homeTeam.add(jsonObject.getString("homeTeam"));
                awayTeam.add(jsonObject.getString("awayTeam"));
                homeScore.add(jsonObject.getString("homeScore"));
                awayScore.add(jsonObject.getString("awayScore"));
                clock.add(jsonObject.getString("clock"));
                period.add(jsonObject.getString("period"));
                gameId.add(jsonObject.getString("id"));
                status.add(jsonObject.getString("status"));

                switch (status.get(i)) {
                    case "pre":
                        clock.set(i, "PRE");
                        period.set(i, "GAME");
                        break;
                    case "in":
                        if (Integer.parseInt(period.get(i)) < 5)
                            period.set(i, period.get(i) + " Qtr");
                        else
                            period.set(i, "OT" + (Integer.parseInt(period.get(i)) - 4));
                        break;
                    case "post":
                        clock.set(i, "FINAL");
                        period.set(i, "");
                        break;
                }

            }

            if (reload) { // when full reload is requested
                // Set list view adapter for game events
                listView.setAdapter(new CustomAdapter(this, homeTeam, awayTeam, homeScore,
                        awayScore, clock, period));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                        intent.putExtra(GAME_THREAD_HOME, homeTeam.get(i));
                        intent.putExtra(GAME_THREAD_AWAY, awayTeam.get(i));
                        intent.putExtra(GAME_ID, gameId.get(i));
                        startActivity(intent);
                    }
                });

                // Hide reload icon and show list view
                setProgressBarIndeterminateVisibility(false);
                linlaHeaderProgress.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

            } else { // when refresh is requested
                ((CustomAdapter) listView.getAdapter()).notifyDataSetChanged();
                Log.d("adapter", "notified of change");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_refresh:
                loadGameData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

}
