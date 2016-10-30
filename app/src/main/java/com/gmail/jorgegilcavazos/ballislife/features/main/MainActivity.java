package com.gmail.jorgegilcavazos.ballislife.features.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmail.jorgegilcavazos.ballislife.network.GCMClientManager;
import com.gmail.jorgegilcavazos.ballislife.features.login.LoginActivity;
import com.gmail.jorgegilcavazos.ballislife.features.settings.SettingsActivity;
import com.gmail.jorgegilcavazos.ballislife.network.NetworkManager;
import com.gmail.jorgegilcavazos.ballislife.R;
import com.gmail.jorgegilcavazos.ballislife.util.AuthListener;
import com.gmail.jorgegilcavazos.ballislife.util.MyDebug;
import com.gmail.jorgegilcavazos.ballislife.network.RedditAuthentication;
import com.gmail.jorgegilcavazos.ballislife.features.games.GamesFragment;
import com.gmail.jorgegilcavazos.ballislife.features.highlights.HighlightsFragment;
import com.gmail.jorgegilcavazos.ballislife.features.posts.PostsFragment;
import com.gmail.jorgegilcavazos.ballislife.features.standings.StandingsFragment;

import net.dean.jraw.RedditClient;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String MY_PREFERENCES = "MyPrefs";
    public static final String FIRST_TIME = "firstTime";
    public static final String PUSH_CLOSE_GAME_ALERT = "pushCGA";
    public static final String REDDIT_USERNAME = "redditUsername";
    private static final String PROJECT_NUMBER = "532852092546";

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
    GamesFragment gamesFragment;
    StandingsFragment standingsFragment;
    HighlightsFragment highlightsFragment;
    PostsFragment postsFragment;

    int selectedFragment;
    private SharedPreferences myPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        LocalBroadcastManager.getInstance(this).registerReceiver(mUserLoginReceiver,
                new IntentFilter("reddit-user-login"));

        if (!RedditAuthentication.getInstance().getRedditClient().isAuthenticated()) {
            RedditAuthentication.getInstance().authenticate(this, listener);
        }

        NetworkManager.getInstance(this);

        if (savedInstanceState == null) {
            // The Activity is not being restored so we need to add a Fragment.
            setFragment(GAMES_FRAGMENT_ID);
        }
    }

    private BroadcastReceiver mUserLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received user log in intent");
            RedditAuthentication.getInstance().saveRefreshTokenInPrefs(context);
            loadRedditUsername();
        }
    };

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
                        setFragment(GAMES_FRAGMENT_ID);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_2:
                        setFragment(STANDINGS_FRAGMENT_ID);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_4:
                        setFragment(POSTS_FRAGMENT_ID);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_5:
                        setFragment(HIGHLIGHTS_FRAGMENT_ID);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.navigation_item_7:
                        Intent loginIntent = new Intent(getApplicationContext(),
                                LoginActivity.class);
                        startActivity(loginIntent);
                        return true;
                    case R.id.navigation_item_9:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        // TODO: check if already logged in, if not, fire intent.
                        Intent settingsIntent = new Intent(getApplicationContext(),
                                SettingsActivity.class);
                        startActivity(settingsIntent);
                        return true;
                    default:
                        setFragment(GAMES_FRAGMENT_ID);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true;
                }
            }
        });
    }

    /**
     * Sets the logo and username from shared preferences.
     */
    private void loadNavigationHeaderContent() {
        loadRedditUsername();
        loadTeamLogo();
    }

    private void loadRedditUsername() {
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

    private void setFragment(int fragmentId) {
        selectedFragment = fragmentId;
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        Bundle bundle = new Bundle();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (fragmentId) {
            case GAMES_FRAGMENT_ID:
                gamesFragment = GamesFragment.newInstance();
                fragmentTransaction.replace(R.id.fragment, gamesFragment, TAG_GAMES_FRAGMENT);
                break;
            case STANDINGS_FRAGMENT_ID:
                toolbar.setSubtitle("");
                standingsFragment = new StandingsFragment();
                fragmentTransaction.replace(R.id.fragment, standingsFragment,
                        TAG_STANDINGS_FRAGMENT);
                break;
            case POSTS_FRAGMENT_ID:
                toolbar.setSubtitle("");
                postsFragment = new PostsFragment();
                bundle.putString("TYPE", "small");
                postsFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment, postsFragment, TAG_POSTS_FRAGMENT);
                break;
            case HIGHLIGHTS_FRAGMENT_ID:
                toolbar.setSubtitle("");
                highlightsFragment = new HighlightsFragment();
                fragmentTransaction.replace(R.id.fragment, highlightsFragment,
                        TAG_HIGHLIGHTS_FRAGMENT);
                break;
        }
        fragmentTransaction.commit();
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
            case POSTS_FRAGMENT_ID:
                if (postsFragment.isPreviewVisible()) {
                    postsFragment.stopVideo();
                } else {
                    setFragment(GAMES_FRAGMENT_ID);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
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
    public void onResume() {  // After a pause OR at startup
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
