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
                    e.onNext(redditClient.me());
                    e.onComplete();
                } catch (Exception ex) {
                    e.onError(ex);
                }
            }
        });

        return observable;
    }

    public Observable<Listing<Contribution>> getUserContributions() {
        String where = "overview";

        final UserContributionPaginator paginator = new UserContributionPaginator(redditClient, where,
                redditClient.getAuthenticatedUser());
        paginator.setLimit(100);
        paginator.setSorting(Sorting.NEW);

        Observable<Listing<Contribution>> observable = Observable.create(
                new ObservableOnSubscribe<Listing<Contribution>>() {
                    @Override
                    public void subscribe(ObservableEmitter<Listing<Contribution>> e) throws Exception {
                        try {
                            Log.d("Service", "starting");
                            Listing<Contribution> contributions = paginator.next(true);
                            e.onNext(contributions);
                            e.onComplete();
                        } catch (Exception ex) {
                            Log.d("Service", "exception");
                            e.onError(ex);
                        }
                    }
                }
        );

        return observable;
    }
}
