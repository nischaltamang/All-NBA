package com.gmail.jorgegilcavazos.ballislife.features.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gmail.jorgegilcavazos.ballislife.R;
import com.gmail.jorgegilcavazos.ballislife.util.AuthListener;
import com.gmail.jorgegilcavazos.ballislife.network.RedditAuthentication;
import com.gmail.jorgegilcavazos.ballislife.util.MyDebug;

import java.net.URL;

import butterknife.BindView;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.login_webview) WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("Log in to Reddit");

        final AuthListener authListener = new AuthListener() {
            @Override
            public void onSuccess() {
                if (MyDebug.LOG) {
                    Log.d(TAG, "success");
                }
                RedditAuthentication.getInstance().saveRefreshTokenInPrefs(getApplicationContext());
                finish();
            }

            @Override
            public void onFailure() {
                if (MyDebug.LOG) {
                    Log.d(TAG, "failed");
                }
                finish();
            }
        };

        URL authURL = RedditAuthentication.getInstance().getAuthorizationUrl();
        webView = (WebView) findViewById(R.id.login_webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "started: " + url);
                if (url.contains("code=")) {
                    webView.stopLoading();
                    RedditAuthentication.getInstance().authenticateWithUser(url, authListener);
                    //TODO: show spinner
                }
            }
        });
        webView.loadUrl(authURL.toExternalForm());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        RedditAuthentication.getInstance().cancelUserAuthTaskIfRunning();
        webView.destroy();
        super.onDestroy();
    }
}
