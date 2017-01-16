package com.gmail.jorgegilcavazos.ballislife.features.profile;

import android.util.Log;

import com.gmail.jorgegilcavazos.ballislife.network.API.RedditService;
import com.gmail.jorgegilcavazos.ballislife.network.RedditAuthentication;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.LoggedInAccount;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class ProfilePresenter extends MvpBasePresenter<ProfileView> {

    private CompositeDisposable disposables;
    private RedditClient redditClient;

    public ProfilePresenter() {
        disposables = new CompositeDisposable();
        redditClient = RedditAuthentication.getInstance().getRedditClient();
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
            try {
                getView().setToolbarTitle(redditClient.getAuthenticatedUser());
                getView().setUsername(redditClient.getAuthenticatedUser());
            } catch (IllegalStateException e) {
                getView().showSnackbar(true);
            }
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

        disposables.clear();
        RedditService redditService = new RedditService(redditClient);

        Observable<LoggedInAccount> account = redditService.getLoggedInAccount();
        disposables.add(
                account.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<LoggedInAccount>() {
                    @Override
                    public void onNext(LoggedInAccount me) {
                        if (isViewAttached()) {
                            getView().setCommentKarma(me.getCommentKarma());
                            getView().setCommentKarmaVisibility(true);
                            getView().setPostKarma(me.getLinkKarma());
                            getView().setPostKarmaVisibility(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isViewAttached()) {
                            getView().showSnackbar(true);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );

        Observable<Listing<Contribution>> contributions = redditService.getUserContributions();
        disposables.add(
                contributions.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Listing<Contribution>>() {
                    @Override
                    public void onNext(Listing<Contribution> contributions) {
                        Log.d("Presenter", "onnext");
                        if (isViewAttached()) {
                            getView().setLoadingIndicator(false);
                            getView().showContent(contributions);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Presenter", "onerror");
                        if (isViewAttached()) {
                            getView().setLoadingIndicator(false);
                            getView().showSnackbar(true);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );


    }

    public void onOffsetChanged(float percentage) {
        final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;

        if (isViewAttached()) {
            if (percentage <= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
                getView().setToolbarTitle("");
            } else {
                try {
                    getView().setToolbarTitle(redditClient.getAuthenticatedUser());
                } catch (IllegalStateException e) {
                    getView().showSnackbar(true);
                }
            }
        }
    }


}
