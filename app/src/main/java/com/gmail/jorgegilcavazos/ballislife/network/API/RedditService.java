package com.gmail.jorgegilcavazos.ballislife.network.API;

import android.util.Log;

import com.gmail.jorgegilcavazos.ballislife.util.RedditUtils;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.SubmissionRequest;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.UserContributionPaginator;

import java.util.ArrayList;
import java.util.List;

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

    public Observable<List<CommentNode>> getComments(final String threadId, final String type) {
        Observable<List<CommentNode>> observable = Observable.create(
                new ObservableOnSubscribe<List<CommentNode>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<CommentNode>> e) throws Exception {
                        SubmissionRequest.Builder builder = new SubmissionRequest.Builder(threadId);
                        switch (type) {
                            case RedditUtils.LIVE_GT_TYPE:
                                builder.sort(CommentSort.NEW);
                                break;
                            case RedditUtils.POST_GT_TYPE:
                                builder.sort(CommentSort.TOP);
                                break;
                            default:
                                builder.sort(CommentSort.TOP);
                                break;
                        }

                        SubmissionRequest submissionRequest = builder.build();
                        Submission submission = null;
                        try {
                            submission = redditClient.getSubmission(submissionRequest);

                            Iterable<CommentNode> iterable = submission.getComments().walkTree();
                            List<CommentNode> commentNodes = new ArrayList<>();
                            for (CommentNode node : iterable) {
                                commentNodes.add(node);
                            }

                            if (!e.isDisposed()) {
                                e.onNext(commentNodes);
                                e.onComplete();
                            }
                        } catch (NetworkException ex) {
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
