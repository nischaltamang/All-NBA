package com.gmail.jorgegilcavazos.ballislife.features.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmail.jorgegilcavazos.ballislife.features.games.GamesPresenter;
import com.gmail.jorgegilcavazos.ballislife.network.GCMClientManager;
import com.gmail.jorgegilcavazos.ballislife.features.login.LoginActivity;
import com.gmail.jorgegilcavazos.ballislife.features.settings.SettingsActivity;
import com.gmail.jorgegilcavazos.ballislife.network.NetworkManager;
import com.gmail.jorgegilcavazos.ballislife.R;
import com.gmail.jorgegilcavazos.ballislife.util.ActivityUtils;
import com.gmail.jorgegilcavazos.ballislife.util.AuthListener;
import com.gmail.jorgegilcavazos.ballislife.util.MyDebug;
import com.gmail.jorgegilcavazos.ballislife.network.RedditAuthentication;
import com.gmail.jorgegilcavazos.ballislife.features.games.GamesFragment;
import com.gmail.jorgegilcavazos.ballislife.features.posts.PostsFragment;
import com.gmail.jorgegilcavazos.ballislife.features.standings.StandingsFragment;

import net.dean.jraw.RedditClient;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String MY_PREFERENCES = "MyPrefs";
    public static final String FIRST_TIME = "firstTime";
    public static final String PUSH_CLOSE_GAME_ALERT = "pushCGA";
    private static final String PROJECT_NUMBER = "532852092546";

    private static final String SELECTED_FRAGMENT_KEY = "selected_fragment";

    private static final String TAG_GAMES_FRAGMENT = "GAMES_FRAGMENT";
    private static final String TAG_STANDINGS_FRAGMENT = "STANDINGS_FRAGMENT";
    private static final String TAG_POSTS_FRAGMENT = "POSTS_FRAGMENT";
    private static final String TAG_HIGHLIGHTS_FRAGMENT = "HIGHLIGHTS_FRAGMENT";

    private static final int GAMES_FRAGMENT_ID = 1;
    private static final int STANDINGS_FRAGMENT_ID = 2;
    private static final int POSTS_FRAGMENT_ID = 4;
    private static final int HIGHLIGHTS_FRAGMENT_ID = 5;

    private GCMClientManager pushClientManager;

    Toolbar toolbar;
    ActionBar actionBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    int selectedFragment;
    private SharedPreferences myPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceListener;

    private GamesPresenter mGamesPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setUpToolbar();
        setUpNavigationView();
        setUpDrawerContent();
        setUpPreferences();
        loadNavigationHeaderContent();

        registerGmcClient();

        AuthListener listener = new AuthListener() {
            @Override
            public void onSuccess() {
                RedditAuthentication.getInstance().saveRefreshTokenInPrefs(getApplicationContext());
                loadRedditUsername();
            }

            @Override
            public void onFailure() {

            }
        };

        if (!RedditAuthentication.getInstance().getRedditClient().isAuthenticated()) {
            RedditAuthentication.getInstance().authenticate(this, listener);
        }

        NetworkManager.getInstance(this);

        // Set default to GamesFragment.
        selectedFragment = GAMES_FRAGMENT_ID;
        if (savedInstanceState != null) {
            selectedFragment = savedInstanceState.getInt(SELECTED_FRAGMENT_KEY);
        }

        switch (selectedFragment) {
            case GAMES_FRAGMENT_ID:
                setGamesFragment();
                break;
            case STANDINGS_FRAGMENT_ID:
                setStandingsFragment();
                break;
            case POSTS_FRAGMENT_ID:
                setPostsFragment();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        Log.d(TAG, "saving " + selectedFragment);
        outState.putInt(SELECTED_FRAGMENT_KEY, selectedFragment);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRedditUsername();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setUpToolbar() {
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

    private void setUpNavigationView() {
        if (toolbar != null) {
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.navigation);
            if (navigationView != null) {
                NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);
                if (navMenuView != null) {
                    navMenuView.setVerticalScrollBarEnabled(false);
                }
            }
        }
    }

    private void setUpDrawerContent() {
        navigationView.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_1:
                        setGamesFragment();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_2:
                        setStandingsFragment();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_4:
                        setPostsFragment();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_5:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_7:
                        // Start LoginActivity if no user is already logged in.
                        if (!RedditAuthentication.getInstance().isUserLoggedIn()) {
                            Intent loginIntent = new Intent(getApplicationContext(),
                                    LoginActivity.class);
                            startActivity(loginIntent);
                        } else {
                            //TODO: open user profile here.
                            Toast.makeText(getApplicationContext(),
                                    "You are already logged in", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.navigation_item_9:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent settingsIntent = new Intent(getApplicationContext(),
                                SettingsActivity.class);
                        startActivity(settingsIntent);
                        return true;
                    default:
                        setGamesFragment();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                }
            }
        });
    }

    public void setGamesFragment() {
        GamesFragment gamesFragment = null;
        if (selectedFragment == GAMES_FRAGMENT_ID) {
            gamesFragment = (GamesFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment);
        }

        if (gamesFragment == null) {
            gamesFragment = GamesFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    gamesFragment, R.id.fragment);
        }

        selectedFragment = GAMES_FRAGMENT_ID;
    }

    public void setStandingsFragment() {
        StandingsFragment standingsFragment = null;
        if (selectedFragment == STANDINGS_FRAGMENT_ID) {
            standingsFragment = (StandingsFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment);
        }

        if (standingsFragment == null) {
            standingsFragment = StandingsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    standingsFragment, R.id.fragment);
        }

        selectedFragment = STANDINGS_FRAGMENT_ID;
    }

    public void setPostsFragment() {
        PostsFragment postsFragment = null;
        if (selectedFragment == POSTS_FRAGMENT_ID) {
            postsFragment = (PostsFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment);
        }

        if (postsFragment == null) {
            postsFragment = PostsFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("TYPE", "small");
            postsFragment.setArguments(bundle);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    postsFragment, R.id.fragment);
        }

        selectedFragment = POSTS_FRAGMENT_ID;
    }

    /**
     * Sets the logo and username from shared preferences.
     */
    private void loadNavigationHeaderContent() {
        loadRedditUsername();
        loadTeamLogo();
    }

    private void loadRedditUsername() {
        if (navigationView == null) {
            return;
        }

        View headerView = navigationView.getHeaderView(0);
        TextView redditUsername = (TextView) headerView.findViewById(R.id.redditUsername);
        RedditClient redditClient = RedditAuthentication.getInstance().getRedditClient();
        if (redditClient.isAuthenticated() && redditClient.hasActiveUserContext()) {
            redditUsername.setText(redditClient.getAuthenticatedUser());
        } else {
            redditUsername.setText(R.string.not_logged);
        }
    }

    private void loadTeamLogo() {
        View headerView = navigationView.getHeaderView(0);
        ImageView favTeamLogo = (ImageView) headerView.findViewById(R.id.favTeamLogo);
        favTeamLogo.setImageResource(getFavTeamLogoResource());
    }

    /**
     * Looks for a favorite team saved in shared preferences and returns the resource id for its
     * logo. If there is no favorite team, the app icon is returned.
     */
    private int getFavTeamLogoResource() {
        String favoriteTeam = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("teams_list", null);

        int resourceId;
        if (favoriteTeam != null && !favoriteTeam.equals("noteam")) {
            resourceId = getResources().getIdentifier(favoriteTeam, "drawable", getPackageName());
        } else {
            resourceId = R.mipmap.ic_launcher;
        }
        return resourceId;
    }

    private void setUpPreferences() {
        myPreferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        // Set default preferences if not set yet
        if (myPreferences.getBoolean(FIRST_TIME, true)) {
            SharedPreferences.Editor editor = myPreferences.edit();
            editor.putBoolean(FIRST_TIME, false);
            editor.putBoolean(PUSH_CLOSE_GAME_ALERT, true);
            editor.apply();
        }

        // Listen for changes to update team logo.
        mPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch (key) {
                    case "teams_list":
                        loadTeamLogo();
                        break;
                }

            }
        };
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(mPreferenceListener);
    }

    private void registerGmcClient() {
        final String GCM_REGISTRATION_URL = "http://phpstack-4722-10615-67130.cloudwaysapps.com/gcm.php?shareRegId=1&regId=";
        pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                if (isNewRegistration) {
                    StringRequest sendRegId = new StringRequest(
                            GCM_REGISTRATION_URL + registrationId, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (MyDebug.LOG) {
                                Log.i(TAG, "Registered with GCM. " + response);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {
                            if (MyDebug.LOG) {
                                Log.e(TAG, "Volley error when registering with GCM. " + e.toString());
                            }
                        }
                    });
                    RequestQueue sendQueue = Volley.newRequestQueue(getApplicationContext());
                    sendQueue.add(sendRegId);
                }
            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
                if (MyDebug.LOG) {
                    Log.e(TAG, "Failure to register with GCM. " + ex);
                }
                //TODO: see what's up with GCM.
                // If there is an error registering, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off when retrying.
            }
        });
    }

    public void setToolbarSubtitle(String subtitle) {
        if (toolbar != null) {
            toolbar.setSubtitle(subtitle);
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
            case GAMES_FRAGMENT_ID:
                // Exit application.
                super.onBackPressed();
                break;
            default:
                // Return to games fragment.
                setGamesFragment();
                navigationView.getMenu().getItem(0).setChecked(true);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        RedditAuthentication.getInstance().cancelReAuthTaskIfRunning();
        RedditAuthentication.getInstance().cancelUserlessAuthTaskIfRunning();
        super.onDestroy();
    }
}
