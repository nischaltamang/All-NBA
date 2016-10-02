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

import com.example.jorgegil.closegamealert.Adapter.PagerAdapter;
import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.View.Fragments.BoxScoreFragment;
import com.example.jorgegil.closegamealert.View.Fragments.CommentThreadFragment;
import com.example.jorgegil.closegamealert.View.Fragments.GamesFragment;

public class CommentsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    private static final String TAG = "CommentsActivity";

    private String homeTeam;
    private String awayTeam;
    private String gameId;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activity);

        Intent intent = getIntent();
        homeTeam = intent.getStringExtra(GamesFragment.GAME_THREAD_HOME);
        awayTeam = intent.getStringExtra(GamesFragment.GAME_THREAD_AWAY);
        gameId = intent.getStringExtra(GamesFragment.GAME_ID);

        setTitle(awayTeam + " @ " + homeTeam);

        Bundle bundle = new Bundle();
        bundle.putString(CommentThreadFragment.HOME_TEAM_KEY, homeTeam);
        bundle.putString(CommentThreadFragment.AWAY_TEAM_KEY, awayTeam);
        bundle.putString(BoxScoreFragment.GAME_ID_KEY, gameId);

        setUpToolbar();

        // Initialize tab layout and add three tabs.
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText("Game Thread"));
        tabLayout.addTab(tabLayout.newTab().setText("Box Score"));
        tabLayout.addTab(tabLayout.newTab().setText("Post Game Thread"));

        viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount(), bundle);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setOnTabSelectedListener(this);
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
        toolbar = (Toolbar) findViewById(R.id.comments_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

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
}
