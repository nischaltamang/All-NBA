package com.gmail.jorgegilcavazos.ballislife.features.profile;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import io.reactivex.disposables.CompositeDisposable;

public class ProfilePresenter extends MvpBasePresenter<ProfileView> {

    private CompositeDisposable disposables;

    public ProfilePresenter() {
        disposables = new CompositeDisposable();
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (!retainInstance) {
            disposables.clear();
        }
    }

    public void init() {
        if (isViewAttached()) {
            getView().setToolbarTitle("Obi-Wan_Ginobili");
            getView().setUsername("Obi-Wan_Ginobili");
        }
        loadUserDetails();
    }

    public void loadUserDetails() {
        if (isViewAttached()) {
            getView().setLoadingIndicator(true);
            getView().dismissSnackbar();
            getView().hideContent();
            getView().setPostKarmaVisibility(false);
            getView().setCommentKarmaVisibility(false);
        }

        // Load Karma
        // Load posts
    }

    public void onOffsetChanged(float percentage) {
        final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;

        if (isViewAttached()) {
            if (percentage <= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
                getView().setToolbarTitle("");
            } else {
                getView().setToolbarTitle("Obi-Wan_Ginobili");
            }
        }
    }


}
