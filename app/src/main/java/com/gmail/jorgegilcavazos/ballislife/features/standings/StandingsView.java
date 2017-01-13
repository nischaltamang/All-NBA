package com.gmail.jorgegilcavazos.ballislife.features.standings;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface StandingsView extends MvpView {

    void setLoadingIndicator(boolean active);

    void showStandings();

    void hideStandings();

    void showSnackbar(boolean canReload);

    void dismissSnackbar();
}
