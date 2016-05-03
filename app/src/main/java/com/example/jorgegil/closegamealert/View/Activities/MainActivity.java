package com.example.jorgegil.closegamealert.View.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    private GCMClientManager pushClientManager;
    String PROJECT_NUMBER = "532852092546";

    Toolbar toolbar;
    ActionBar ab;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    GamesFragment gamesFragment;
    StandingsFragment standingsFragment;
    HighlightsFragment hlfragment;

    int selectedFragment;
    PostsFragment postsFragment1;

    public static final String MyPreferences = "MyPrefs";
    public static final String firstTime = "firstTime";
    public static final String pushCloseGameAlert = "pushCGA";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Show loading icon
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setUpToolbar();
        setUpNavigationView();
        setUpDrawerContent();
        setUpPreferences();


        // Select Games Fragment as default
        setTitle("Games");
        setFragment(1);
        toolbar.setBackgroundColor(getResources().getColor(R.color.tricornBlack));
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.tricornBlackDark));
        }

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
            navigationView.getMenu().getItem(8).setCheckable(false);
        }
    }

    private void setUpDrawerContent() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_1:
                        setFragment(1);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_2:
                        setFragment(2);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_4:
                        setFragment(4);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_5:
                        setFragment(5);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_9:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);

                }
                return true;
            }
        });
    }

    public void setFragment(int position) {
        selectedFragment = position;
        android.support.v4.app.FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        Bundle bundle = new Bundle();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {
            case 1:
                setTitle("Games");
                gamesFragment = new GamesFragment();
                fragmentTransaction.replace(R.id.fragment, gamesFragment, "GAMES_FRAGMENT");
                fragmentTransaction.commit();
                break;
            case 2:
                setTitle("Standings");
                standingsFragment = new StandingsFragment();
                fragmentTransaction.replace(R.id.fragment, standingsFragment, "STANDINGS_FRAGMENT");
                fragmentTransaction.commit();
                break;
            case 4:
                setTitle("NBA");
                PostsFragment postsFragment = new PostsFragment();
                bundle.putString("TYPE", "small");
                bundle.putString("URL", "http://www.reddit.com/r/nba/.json");
                bundle.putString("FILTER", "ALL");
                postsFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment, postsFragment, "POSTS_FRAGMENT");
                fragmentTransaction.commit();
                break;
            case 5:
                setTitle("Highlights");
                hlfragment = new HighlightsFragment();
                fragmentTransaction.replace(R.id.fragment, hlfragment, "HL_FRAGMENT");
                fragmentTransaction.commit();
                break;
        }

    }

    public void setUpPreferences() {
        sharedPreferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        // Set default preferences if not set yet
        if (sharedPreferences.getBoolean(firstTime, true)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(firstTime, false);
            editor.putBoolean(pushCloseGameAlert, true);
            editor.commit();
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (selectedFragment) {
            case 1:
                super.onBackPressed();
                break;
            case 5:
                if (hlfragment.isPreviewVisible()) {
                    hlfragment.stopVideo();
                } else {
                    setFragment(1);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
                break;
            default:
                setFragment(1);
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
