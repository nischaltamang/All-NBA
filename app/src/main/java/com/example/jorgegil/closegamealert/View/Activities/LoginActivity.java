package com.example.jorgegil.closegamealert.View.Activities;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.jorgegil.closegamealert.R;
import com.example.jorgegil.closegamealert.Utils.RedditAuthentication;

import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;
import net.dean.jraw.models.LoggedInAccount;

import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private static final String CLIENT_ID = "XDtA2eYVKp1wWA";
    private static final String REDIRECT_URL = "http://localhost/authorize_callback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final OAuthHelper oAuthHelper = RedditAuthentication.sRedditClient.getOAuthHelper();
        final Credentials credentials = Credentials.installedApp(CLIENT_ID, REDIRECT_URL);
        String[] scopes = {"identity", "edit", "flair", "mysubreddits", "read", "vote",
                "submit", "subscribe"};
        URL authURL = oAuthHelper.getAuthorizationUrl(credentials, true, true, scopes);

        final WebView webView = (WebView) findViewById(R.id.webView1);
        webView.loadUrl(authURL.toExternalForm());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "url: " + url);
                if (url.contains("code=")) {
                    new AuthenticateTask(oAuthHelper, credentials).execute(url);
                    finish();
                }
            }
        });
    }

    private class AuthenticateTask extends AsyncTask<String, Void, String> {
        private OAuthHelper mOAuthHelper;
        private Credentials mCredentials;

        public AuthenticateTask(OAuthHelper oAuthHelper, Credentials credentials) {
            mOAuthHelper = oAuthHelper;
            mCredentials = credentials;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OAuthData oAuthData = mOAuthHelper.onUserChallenge(params[0], mCredentials);
                if (oAuthData != null) {
                    RedditAuthentication.sRedditClient.authenticate(oAuthData);
                    RedditAuthentication.sLoggedInStatus = true;
                    String refreshToken = RedditAuthentication.sRedditClient.getOAuthData().getRefreshToken();
                    LoggedInAccount me = RedditAuthentication.sRedditClient.me();
                    Log.d(TAG, me.getFullName());
                    return me.getFullName();
                    // TODO: save name and token in shared prefs.
                } else {
                    Log.i(TAG, "OAuthData returned null.");
                }
            } catch (OAuthException e) {
                Log.e(TAG, "Could not get OAuthData. ", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String username) {
            String textToToast;
            if (username != null) {
                textToToast = "Logged in as " + username;
            } else {
                textToToast = "Could not log in";
            }
            Toast.makeText(getApplicationContext(), textToToast, Toast.LENGTH_SHORT).show();
        }
    }

}
