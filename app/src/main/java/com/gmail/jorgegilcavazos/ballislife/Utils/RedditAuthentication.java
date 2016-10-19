package com.gmail.jorgegilcavazos.ballislife.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;

import java.net.URL;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

/**
 * Singleton class responsible for authenticating with Reddit.
 *
 * There are two types of authentication:
 * (1) UserlessAuth: the user does not log in with a username and password. But the application
 * still needs to authenticate with Reddit to view content.
 * (2) UserAuth: the user enters a username and password. This method allows the application to view
 * content and perform actions from the logged in account (post, comment, upvote, etc..).
 *
 * Whenever UserAuth is used, a refresh token must be saved in shared preferences so that future
 * authentications attempts can use that instead of asking to the user to login again.
 */
public class RedditAuthentication {
    private static final String TAG = "RedditAuthentication";

    public static final String REDDIT_AUTH_PREFS = "RedditAuthPrefs";
    public static final String CLIENT_ID = "XDtA2eYVKp1wWA";
    public static final String REDIRECT_URL = "http://localhost/authorize_callback";
    public static final String REDDIT_TOKEN_KEY = "REDDIT_TOKEN";

    private static RedditAuthentication mInstance = null;

    private RedditClient mRedditClient;

    private RedditAuthentication() {
        mRedditClient = new RedditClient(UserAgent.of("android",
                "com.gmail.jorgegilcavazos.ballislife", "v0.1.0", "jorgegil96"));
    }

    public static RedditAuthentication getInstance() {
        if (mInstance == null) {
            mInstance = new RedditAuthentication();
        }
        return mInstance;
    }

    public RedditClient getRedditClient() {
        return mRedditClient;
    }

    /**
     * If a refresh token is saved in shared preferences, a {@link ReAuthTask} is started, if
     * there is no token saved, a {@link UserlessAuthTask} is started.
     */
    public void authenticate(Context context, AuthListener listener) {
        String refreshToken = getRefreshTokenFromPrefs(context);
        if (refreshToken == null) {
            authenticateWithoutUser(listener);
        } else {
            reAuthenticate(refreshToken, listener);
        }
    }

    private void authenticateWithoutUser(AuthListener listener) {
        new UserlessAuthTask(listener).execute();
    }

    private void reAuthenticate(String refreshToken, AuthListener listener) {
        OAuthHelper helper = mRedditClient.getOAuthHelper();
        helper.setRefreshToken(refreshToken);
        Credentials credentials = Credentials.installedApp(CLIENT_ID, REDIRECT_URL);

        new ReAuthTask(credentials, refreshToken, listener).execute();
    }

    public void authenticateWithUser(String url, AuthListener listener) {
        OAuthHelper oAuthHelper = mRedditClient.getOAuthHelper();
        Credentials credentials = Credentials.installedApp(CLIENT_ID, REDIRECT_URL);
        new UserAuthTask(oAuthHelper, credentials, url, listener).execute();
    }

    public URL getAuthorizationUrl() {
        OAuthHelper oAuthHelper = mRedditClient.getOAuthHelper();
        Credentials credentials = Credentials.installedApp(CLIENT_ID, REDIRECT_URL);
        String[] scopes = {"identity", "edit", "flair", "mysubreddits", "read", "vote",
                "submit", "subscribe"};
        return oAuthHelper.getAuthorizationUrl(credentials, true, true, scopes);
    }

    public String getRefreshTokenFromPrefs(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(REDDIT_AUTH_PREFS, MODE_PRIVATE);
        return preferences.getString(REDDIT_TOKEN_KEY, null);
    }

    public void saveRefreshTokenInPrefs(Context context) {
        String refreshToken = mRedditClient.getOAuthData().getRefreshToken();
        if (refreshToken != null) {
            SharedPreferences preferences = context
                    .getSharedPreferences(REDDIT_AUTH_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(REDDIT_TOKEN_KEY, refreshToken);
            editor.apply();
        }
    }

    private class UserlessAuthTask extends AsyncTask<Void, Void, Void> {
        private AuthListener mListener;

        public UserlessAuthTask(AuthListener listener) {
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Credentials credentials = Credentials.userlessApp(CLIENT_ID, UUID.randomUUID());

            try {
                OAuthData oAuthData = mRedditClient.getOAuthHelper().easyAuth(credentials);
                mRedditClient.authenticate(oAuthData);
            } catch (Exception e) {
                if (MyDebug.LOG) {
                    Log.d(TAG, "Error trying to authenticate: " + e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mRedditClient.isAuthenticated()) {
                if (MyDebug.LOG) {
                    Log.d(TAG, "UserlessAuthTask: logged in without user context");
                }
                mListener.onSuccess();
            } else {
                if (MyDebug.LOG) {
                    Log.d(TAG, "UserlessAuthTask: isAuthenticated: false");
                }
                mListener.onFailure();
            }
        }
    }

    private class UserAuthTask extends AsyncTask<Void, Void, Void> {
        private OAuthHelper mOAuthHelper;
        private Credentials mCredentials;
        private String mUrl;
        private AuthListener mListener;

        public UserAuthTask(OAuthHelper oAuthHelper, Credentials credentials, String url,
                            AuthListener listener) {
            mOAuthHelper = oAuthHelper;
            mCredentials = credentials;
            mUrl = url;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                OAuthData oAuthData = mOAuthHelper.onUserChallenge(mUrl, mCredentials);
                mRedditClient.authenticate(oAuthData);
            } catch (OAuthException e) {
                if (MyDebug.LOG) {
                    Log.e(TAG, "UserAuthTask: Could not get OAuthData. ", e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mRedditClient.isAuthenticated() && mRedditClient.hasActiveUserContext()) {
                if (MyDebug.LOG) {
                    Log.i(TAG, "UserAuthTask: Logged in as " +
                            mRedditClient.getAuthenticatedUser());
                }
                mListener.onSuccess();
            } else {
                mListener.onFailure();
            }
        }
    }

    private class ReAuthTask extends AsyncTask<Void, Void, Void> {
        private Credentials mCredentials;
        private String mRefreshToken;
        private AuthListener mListener;

        public ReAuthTask(Credentials credentials, String refreshToken, AuthListener listener) {
            mCredentials = credentials;
            mRefreshToken = refreshToken;
            mListener = listener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            OAuthHelper helper = mRedditClient.getOAuthHelper();
            helper.setRefreshToken(mRefreshToken);

            try {
                OAuthData oAuthData = helper.refreshToken(mCredentials);
                mRedditClient.authenticate(oAuthData);
            } catch (OAuthException e) {
                if (MyDebug.LOG) {
                    Log.e(TAG, "ReAuthTask: Could not get OAuthData. ", e);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mRedditClient.isAuthenticated() && mRedditClient.hasActiveUserContext()) {
                if (MyDebug.LOG) {
                    Log.i(TAG, "ReAuthTask: Logged in as " + mRedditClient.getAuthenticatedUser());
                }
                mListener.onSuccess();
            } else {
                mListener.onFailure();
            }
        }
    }

}
