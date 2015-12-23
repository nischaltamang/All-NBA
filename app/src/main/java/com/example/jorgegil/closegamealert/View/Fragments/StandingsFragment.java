package com.example.jorgegil.closegamealert.View.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jorgegil.closegamealert.R;

public class StandingsFragment extends Fragment {

    LinearLayout linlaHeaderProgress;

    public StandingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setProgressBarIndeterminateVisibility(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_standings, container, false);
        //linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        //linlaHeaderProgress.setVisibility(View.VISIBLE);

        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText("Standings");

        getActivity().setProgressBarIndeterminateVisibility(false);
        //linlaHeaderProgress.setVisibility(View.GONE);


        return view;
    }
}
