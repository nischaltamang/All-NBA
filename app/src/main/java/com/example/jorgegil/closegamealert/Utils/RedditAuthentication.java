package com.example.jorgegil.closegamealert.Utils;

import android.content.Context;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.LoggedInAccount;

public class RedditAuthentication {
    private static final String TAG = "RedditAuthentication";

    private static final String CLIENT_ID = "XDtA2eYVKp1wWA";
    private static final String REDIRECT_URL = "http://localhost/authorize_callback";

    public static RedditClient sRedditClient;
    public static LoggedInAccount sAccount;
    public static boolean sLoggedInStatus;

    public RedditAuthentication(Context context) {
        sRedditClient = new RedditClient(UserAgent.of("android", "com.example.jorgegil96.allnba",
                "v0.1", "jorgegil96"));
        sLoggedInStatus = false;



        // TODO: check that internet is available

    }

    /*
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
    */

}
