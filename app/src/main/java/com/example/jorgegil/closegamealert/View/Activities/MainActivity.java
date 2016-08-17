package com.example.jorgegil.closegamealert.View.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jorgegil.closegamealert.GCM.GCMClientManager;
import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.View.Fragments.GamesFragment;
import com.example.jorgegil.closegamealert.View.Fragments.HighlightsFragment;
import com.example.jorgegil.closegamealert.View.Fragments.PostsFragment;
import com.example.jorgegil.closegamealert.View.Fragments.StandingsFragment;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private GCMClientManager pushClientManager;
    String PROJECT_NUMBER = "532852092546";

    Toolbar toolbar;
    ActionBar actionBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    GamesFragment gamesFragment;
    StandingsFragment standingsFragment;
    HighlightsFragment highlightsFragment;

    int selectedFragment;

    public static final String MyPreferences = "MyPrefs";
    public static final String firstTime = "firstTime";
    public static final String pushCloseGameAlert = "pushCGA";
    SharedPreferences sharedPreferences;

    private static final int GAMES_FRAGMENT_ID = 1;
    private static final int STANDINGS_FRAGMENT_ID = 2;
    private static final int REDDIT_FRAGMENT_ID = 4;
    private static final int HIGHLIGHTS_FRAGMENT_ID = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setUpToolbar();
        setUpNavigationView();
        setUpDrawerContent();
        setUpPreferences();
        setFragment(GAMES_FRAGMENT_ID);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.tricornBlack));

        registerGmcClient();
    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            actionBar = getSupportActionBar();
            if (actionBar != null) {
                // Show menu icon
                actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_white);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
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
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_1:
                        setFragment(GAMES_FRAGMENT_ID);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_2:
                        setFragment(STANDINGS_FRAGMENT_ID);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_4:
                        setFragment(REDDIT_FRAGMENT_ID);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_5:
                        setFragment(HIGHLIGHTS_FRAGMENT_ID);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_9:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        setFragment(GAMES_FRAGMENT_ID);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                }
            }
        });
    }

    private void setFragment(int fragmentId) {
        selectedFragment = fragmentId;
        android.support.v4.app.FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        Bundle bundle = new Bundle();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (fragmentId) {
            case GAMES_FRAGMENT_ID:
                setTitle(R.string.games_fragment_title);
                toolbar.setSubtitle(R.string.today_string);
                gamesFragment = new GamesFragment();
                fragmentTransaction.replace(R.id.fragment, gamesFragment, "GAMES_FRAGMENT");
                fragmentTransaction.commit();
                break;
            case STANDINGS_FRAGMENT_ID:
                setTitle(R.string.standings_fragment_title);
                toolbar.setSubtitle("");
                standingsFragment = new StandingsFragment();
                fragmentTransaction.replace(R.id.fragment, standingsFragment, "STANDINGS_FRAGMENT");
                fragmentTransaction.commit();
                break;
            case REDDIT_FRAGMENT_ID:
                toolbar.setSubtitle("");
                setTitle(R.string.reddit_nba_fragment_title);
                PostsFragment postsFragment = new PostsFragment();
                bundle.putString("TYPE", "small");
                bundle.putString("URL", "http://www.reddit.com/r/nba/.json");
                bundle.putString("FILTER", "ALL");
                postsFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment, postsFragment, "POSTS_FRAGMENT");
                fragmentTransaction.commit();
                break;
            case HIGHLIGHTS_FRAGMENT_ID:
                toolbar.setSubtitle("");
                setTitle(R.string.highlights_fragment_title);
                highlightsFragment = new HighlightsFragment();
                fragmentTransaction.replace(R.id.fragment, highlightsFragment, "HL_FRAGMENT");
                fragmentTransaction.commit();
                break;
        }

    }

    private void setUpPreferences() {
        sharedPreferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        // Set default preferences if not set yet
        if (sharedPreferences.getBoolean(firstTime, true)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(firstTime, false);
            editor.putBoolean(pushCloseGameAlert, true);
            editor.commit();
        }
    }

    private void registerGmcClient() {
        final String GMC_REGISTRATION_URL = "http://phpstack-4722-10615-67130.cloudwaysapps.com/gcm.php?shareRegId=1&regId=";
        pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                if (isNewRegistration) {
                    StringRequest sendRegId = new StringRequest(GMC_REGISTRATION_URL + registrationId, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i(TAG, "Registered with GMC. " + response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            Log.e(TAG, "Volley error when registering with GMC. " + e.toString());
                        }
                    });
                    RequestQueue sendQueue = Volley.newRequestQueue(getApplicationContext());
                    sendQueue.add(sendRegId);
                }
            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
                Log.e(TAG, "Failure to register with GMC. " + ex);
                //TODO: see what's up with GMC.
                // If there is an error registering, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off when retrying.
            }
        });
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (selectedFragment) {
            case GAMES_FRAGMENT_ID:
                // Exit application.
                super.onBackPressed();
                break;
            case HIGHLIGHTS_FRAGMENT_ID:
                if (highlightsFragment.isPreviewVisible()) {
                    highlightsFragment.stopVideo();
                } else {
                    // Return to games fragment.
                    setFragment(GAMES_FRAGMENT_ID);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
                break;
            default:
                // Return to games fragment.
                setFragment(GAMES_FRAGMENT_ID);
                navigationView.getMenu().getItem(0).setChecked(true);
                break;
        }
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
