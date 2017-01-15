package com.gmail.jorgegilcavazos.ballislife.features.profile;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gmail.jorgegilcavazos.ballislife.R;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.util.List;

public class ProfileActivity extends MvpActivity<ProfileView, ProfilePresenter>
        implements ProfileView, SwipeRefreshLayout.OnRefreshListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @NonNull
    @Override
    public ProfilePresenter createPresenter() {
        return null;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showContent(List<String> contentList) {

    }

    @Override
    public void hideContent() {

    }

    @Override
    public void showSnackbar(boolean canReload) {

    }

    @Override
    public void dismissSnackbar() {

    }
}
