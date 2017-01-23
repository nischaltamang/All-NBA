package com.gmail.jorgegilcavazos.ballislife.features.gamethread;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import io.reactivex.disposables.CompositeDisposable;

public class GameThreadPresenter extends MvpBasePresenter<GameThreadView> {

    private CompositeDisposable disposables;

    public GameThreadPresenter() {
        disposables = new CompositeDisposable();
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (!retainInstance) {
            disposables.clear();
        }
    }

    public void loadComments() {

    }

    public void dismissSnackbar() {
        if (isViewAttached()) {
            getView().dismissSnackbar();
        }
    }
}
