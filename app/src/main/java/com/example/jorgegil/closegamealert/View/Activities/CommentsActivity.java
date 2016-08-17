package com.example.jorgegil.closegamealert.View.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jorgegil.closegamealert.General.Comment;
import com.example.jorgegil.closegamealert.Utils.PagerAdapter;
import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.View.Fragments.GamesFragment;

import java.util.ArrayList;

/**
 * Created by jorgegil on 11/29/15.
 */
public class CommentsActivity extends AppCompatActivity {

    String gameThreadsUrl = "https://www.reddit.com/r/nba/search.json?sort=new&restrict_sr=on&q=flair%3AGame%2BThread";
    String commentsUrl = "https://www.reddit.com/r/nba/comments/GTID/.json";
    String gameThreadId = "No Game Thread found...";
    String homeTeam = "";
    String awayTeam= "";
    String gameId = "";
    TabLayout tabLayout;
    ListView listView;
    LinearLayout linlaHeaderProgress;
    TextView noThread;
    boolean foundThread = false;

    ArrayList<Comment> commentList;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activity);


        // Get teams abbrev from MainActivity
        Intent intent = getIntent();
        homeTeam = intent.getStringExtra(GamesFragment.GAME_THREAD_HOME);
        awayTeam = intent.getStringExtra(GamesFragment.GAME_THREAD_AWAY);
        gameId = intent.getStringExtra(GamesFragment.GAME_ID);

        setTitle(awayTeam + " @ " + homeTeam);

        Bundle bundle = new Bundle();
        bundle.putString("homeTeam", homeTeam);
        bundle.putString("awayTeam", awayTeam);
        bundle.putString("gameId", gameId);

        setUpToolbar();
        setUpTabLayout();

        //Set Colors
        /*
        toolbar.setBackgroundColor(getResources().getColor(R.color.indigo));
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.indigoDark));
        }
        */

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), bundle);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    private void setUpTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //tabLayout.setBackgroundColor(getResources().getColor(R.color.indigo));
        tabLayout.addTab(tabLayout.newTab().setText("Game Thread"));
        tabLayout.addTab(tabLayout.newTab().setText("Box Score"));
        tabLayout.addTab(tabLayout.newTab().setText("Post Game Thread"));
    }


}
