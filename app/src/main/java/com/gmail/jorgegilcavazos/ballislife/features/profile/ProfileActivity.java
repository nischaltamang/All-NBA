package com.gmail.jorgegilcavazos.ballislife.features.profile;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.jorgegilcavazos.ballislife.R;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.Listing;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends MvpActivity<ProfileView, ProfilePresenter>
        implements ProfileView, SwipeRefreshLayout.OnRefreshListener,
        AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = "ProfileActivity";

    @BindView(R.id.profile_coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.profile_appbar) AppBarLayout appBarLayout;
    @BindView(R.id.profile_collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.profile_toolbar) Toolbar toolbar;
    @BindView(R.id.profile_image) ImageView profileImage;
    @BindView(R.id.profile_username) TextView usernameTV;
    @BindView(R.id.profile_post_karma_count) TextView postKarmaTV;
    @BindView(R.id.profile_comment_karma_count) TextView commentKarmaTV;
    @BindView(R.id.profile_swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.profile_recycler_view) RecyclerView recyclerView;

    private RecyclerView.LayoutManager layoutManager;
    private ContributionsAdapter contributionsAdapter;
    private Snackbar snackbar;

    @NonNull
    @Override
    public ProfilePresenter createPresenter() {
        return new ProfilePresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        appBarLayout.addOnOffsetChangedListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        contributionsAdapter = new ContributionsAdapter(this, new ArrayList<Contribution>());
        recyclerView.setAdapter(contributionsAdapter);

        setUpToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:
                presenter.loadUserDetails();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        presenter.loadUserDetails();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        presenter.onOffsetChanged((float) Math.abs(verticalOffset) / (float) maxScroll);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        swipeRefreshLayout.setRefreshing(active);
    }

    @Override
    public void showContent(Listing<Contribution> contributions) {
        contributionsAdapter.addData(contributions);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideContent() {
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void setToolbarTitle(String title) {
        collapsingToolbar.setTitle(title);
    }

    @Override
    public void setProfileImage(int resId) {
        profileImage.setImageResource(resId);
    }

    @Override
    public void setUsername(String username) {
        usernameTV.setText(username);
    }

    @Override
    public void setPostKarma(int postKarma) {
        postKarmaTV.setText(String.valueOf(postKarma));
    }

    @Override
    public void setPostKarmaVisibility(boolean visible) {
        if (visible) {
            postKarmaTV.setVisibility(View.VISIBLE);
        } else {
            postKarmaTV.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setCommentKarma(int commentKarma) {
        commentKarmaTV.setText(String.valueOf(commentKarma));
    }

    @Override
    public void setCommentKarmaVisibility(boolean visible) {
        if (visible) {
            commentKarmaTV.setVisibility(View.VISIBLE);
        } else {
            commentKarmaTV.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showSnackbar(boolean canReload) {
        snackbar = Snackbar.make(coordinatorLayout, R.string.failed_profile_data,
                Snackbar.LENGTH_INDEFINITE);
        if (canReload) {
            snackbar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.loadUserDetails();
                }
            });
        }
        snackbar.show();
    }

    @Override
    public void dismissSnackbar() {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    private void setUpToolbar(){
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

    }
}
