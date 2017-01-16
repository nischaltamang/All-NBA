package com.gmail.jorgegilcavazos.ballislife.network.API;

import android.util.Log;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.UserContributionPaginator;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class RedditService {

    private RedditClient redditClient;

    public RedditService(RedditClient redditClient) {
        this.redditClient = redditClient;
    }

    public Observable<LoggedInAccount> getLoggedInAccount() {
        Observable<LoggedInAccount> observable = Observable.create(
                new ObservableOnSubscribe<LoggedInAccount>() {
            @Override
            public void subscribe(ObservableEmitter<LoggedInAccount> e) throws Exception {
                try {
                    if (!e.isDisposed()) {
                        e.onNext(redditClient.me());
                        e.onComplete();
                    }
                } catch (Exception ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                    }
                }
            }
        });

        return observable;
    }

    public Observable<Listing<Contribution>> getUserContributions() {
        String where = "overview";

        final UserContributionPaginator paginator = new UserContributionPaginator(redditClient, where,
                redditClient.getAuthenticatedUser());
        paginator.setLimit(50);
        paginator.setSorting(Sorting.NEW);

        Observable<Listing<Contribution>> observable = Observable.create(
                new ObservableOnSubscribe<Listing<Contribution>>() {
                    @Override
                    public void subscribe(ObservableEmitter<Listing<Contribution>> e) throws Exception {
                        try {
                            Listing<Contribution> contributions = paginator.next(true);
                            if (!e.isDisposed()) {
                                e.onNext(contributions);
                                e.onComplete();
                            }
                        } catch (Exception ex) {
                            if (!e.isDisposed()) {
                                e.onError(ex);
                            }
                        }
                    }
                }
        );

        return observable;
    }
}
