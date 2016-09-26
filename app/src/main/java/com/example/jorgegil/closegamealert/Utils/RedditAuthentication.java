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
import net.dean.jraw.http.oauth.OAuthHelper;
import net.dean.jraw.models.LoggedInAccount;

import java.util.UUID;

public class RedditAuthentication {
    private static final String TAG = "RedditAuthentication";

    public static final String CLIENT_ID = "XDtA2eYVKp1wWA";
    public static final String REDIRECT_URL = "http://localhost/authorize_callback";

    public static RedditClient redditClient;
    public static boolean isLoggedIn;
    public static boolean isAuthenticated;

    public RedditAuthentication() {
        redditClient = new RedditClient(UserAgent.of("android", "com.example.jorgegil96.allnba",
                "v0.1", "jorgegil96"));
        isLoggedIn = false;
        isAuthenticated = false;
    }

    public void updateToken(Context context) {
        if (redditClient == null) {
            redditClient = new RedditClient(UserAgent.of("android", "com.example.jorgegil96.allnba",
                    "v0.1", "jorgegil96"));
            isLoggedIn = false;
            isAuthenticated = false;
        }
        new UserlessAuthTask(context).execute();
    }

    private class UserlessAuthTask extends AsyncTask<Void, Void, Void> {
        private Context mContext;

        public UserlessAuthTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Credentials credentials = Credentials.userlessApp(CLIENT_ID, UUID.randomUUID());
            OAuthData oAuthData;
            try {
                oAuthData = redditClient.getOAuthHelper().easyAuth(credentials);
                redditClient.authenticate(oAuthData);
            } catch (OAuthException e) {
                Log.e(TAG, "OAthException error: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (redditClient.isAuthenticated()) {
                RedditAuthentication.isAuthenticated = true;
                Toast.makeText(mContext, "Authenticated", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
