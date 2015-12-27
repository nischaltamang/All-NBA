package com.example.jorgegil.closegamealert.View.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import com.example.jorgegil.closegamealert.GCM.GCMClientManager;
import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.Utils.CustomAdapter;
import com.example.jorgegil.closegamealert.View.Fragments.GamesFragment;
import com.example.jorgegil.closegamealert.View.Fragments.PostsFragment;
import com.example.jorgegil.closegamealert.View.Fragments.StandingsFragment;
import com.example.jorgegil.closegamealert.View.Fragments.ThreadFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private GCMClientManager pushClientManager;
    String PROJECT_NUMBER = "532852092546";

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

        // Select Games Fragment as default
        setFragment(1);

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
        }
    }

    private void setUpDrawerContent() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                setTitle(menuItem.getTitle());
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
                }
                return true;
            }
        });
    }

    public void setFragment(int position) {
        android.support.v4.app.FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {
            case 1:
                GamesFragment gamesFragment = new GamesFragment();
                fragmentTransaction.replace(R.id.fragment, gamesFragment, "STANDINGS_FRAGMENT");
                fragmentTransaction.commit();
                break;
            case 2:
                StandingsFragment standingsFragment = new StandingsFragment();
                fragmentTransaction.replace(R.id.fragment, standingsFragment, "GAMES_FRAGMENT");
                fragmentTransaction.commit();
                break;
            case 4:
                PostsFragment postsFragment = new PostsFragment();
                fragmentTransaction.replace(R.id.fragment, postsFragment, "POSTS_FRAGMENT");
                fragmentTransaction.commit();
                break;
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
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
