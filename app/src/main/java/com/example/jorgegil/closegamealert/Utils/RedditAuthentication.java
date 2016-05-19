package com.example.jorgegil.closegamealert.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;

import java.util.UUID;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by jorgegil on 5/7/16.
 */
public class RedditAuthentication {
    private static final String TAG = "RedditAuthentication";
    private static RedditAuthentication mInstance = null;

    private static final String CLIENT_ID = "XDtA2eYVKp1wWA";

    public static RedditClient redditClient;
    public static Credentials credentials;
    public static OAuthData oAuthData;

    private Context context;

    public static RedditAuthentication getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RedditAuthentication(context.getApplicationContext());
        }
        return mInstance;
    }

    private RedditAuthentication(Context context) {
        Log.d(TAG, "Preparing to authenticate");
        UserAgent userAgent = UserAgent.of("mobile", "com.example.jorgegil96.allnba", "v0.1", "jorgegil96");
        redditClient = new RedditClient(userAgent);
        credentials = Credentials.userlessApp(CLIENT_ID, UUID.randomUUID());
        authenticate();
        this.context = context;
    }

    private void authenticate() {
        rx.Observable<String> authenticateReddit = rx.Observable.create(new rx.Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String data = "daaataa";
                    UserAgent userAgent = UserAgent.of("mobile", "com.example.jorgegil96.allnba", "v0.1", "jorgegil96");
                    redditClient = new RedditClient(userAgent);
                    credentials = Credentials.userlessApp(CLIENT_ID, UUID.randomUUID());
                    try {
                        oAuthData = redditClient.getOAuthHelper().easyAuth(credentials);
                        redditClient.authenticate(oAuthData);
                        Log.d(TAG, "Client authenticated");
                    } catch (OAuthException e) {
                        Log.d(TAG, "Error authenticating: " + e.toString());
                    }
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });

        authenticateReddit
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "CALL: " + s);
                        Toast.makeText(context, "text: " + s, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public class GetSubmissionsTask extends AsyncTask<Void, Void, Listing<Submission>> {
        @Override
        protected Listing<Submission> doInBackground(Void... voids) {
            if (redditClient.isAuthenticated()) {
                SubredditPaginator paginator = new SubredditPaginator(redditClient, "nba");
                paginator.setLimit(100);
                paginator.setSorting(Sorting.HOT);
                return paginator.next(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Listing<Submission> submissions) {

        }
    }

    public static boolean isAuthenticated() {
        return redditClient.isAuthenticated();
    }

}
