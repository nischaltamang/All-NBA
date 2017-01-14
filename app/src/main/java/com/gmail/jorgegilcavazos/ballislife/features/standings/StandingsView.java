package com.gmail.jorgegilcavazos.ballislife.features.standings;

import com.gmail.jorgegilcavazos.ballislife.features.model.TeamRecord;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

public interface StandingsView extends MvpView {

    void setLoadingIndicator(boolean active);

    void showStandings(List<TeamRecord> eastStandings, List<TeamRecord> westStandings);

    void hideStandings();

    void showSnackbar(boolean canReload);

    void dismissSnackbar();
}
