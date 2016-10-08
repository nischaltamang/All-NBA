package com.gmail.jorgegilcavazos.ballislife.View.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.gmail.jorgegilcavazos.ballislife.R;


public class BoxScoreFragment extends Fragment {
    private static final String NBA_BOX_SCORE_URL = "http://stats.nba.com/game/#!/";
    public static final String GAME_ID_KEY = "GAME_ID";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_box_score, container, false);

        String gameId = getArguments().getString(GAME_ID_KEY);

        WebView webView = (WebView) view.findViewById(R.id.webView1);
        // TODO: Fix or replace.
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(NBA_BOX_SCORE_URL + gameId);
        return view;
    }
}
