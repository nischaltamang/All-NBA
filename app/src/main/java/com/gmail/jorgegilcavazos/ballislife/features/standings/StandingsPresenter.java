package com.gmail.jorgegilcavazos.ballislife.features.standings;


import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import io.reactivex.disposables.CompositeDisposable;

public class StandingsPresenter extends MvpBasePresenter<StandingsView> {

    private CompositeDisposable disposables;

    public StandingsPresenter() {
        disposables = new CompositeDisposable();
    }

    public void loadStandings() {
        getView().setLoadingIndicator(true);
        getView().dismissSnackbar();
        getView().hideStandings();
    }

    public void dismissSnackbar() {
        getView().dismissSnackbar();
    }
}
