package com.example.jorgegil.closegamealert.View.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jorgegil.closegamealert.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class StandingsFragment extends Fragment {
    Context context;
    String standingsURL = "http://stats.nba.com/stats/playoffpicture?LeagueID=00&SeasonID=22015";
    View rootView;
    LinearLayout linlaHeaderProgress;
    TableLayout tableLayout;

    boolean dark;


    public StandingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getActivity();
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_standings, container, false);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);

        tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);
        getStandings();


        return rootView;
    }

    public void getStandings() {

        tableLayout.removeAllViews();

        linlaHeaderProgress.setVisibility(View.VISIBLE);
        tableLayout.setVisibility(View.GONE);

        //Request Standings from stats.nba.com
        StringRequest request = new StringRequest(standingsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseStandings(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Standings", "Volley error: " + error.toString());
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private void parseStandings(String jsonString) {
        try {
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("resultSets");
            Log.d("Standings", "num: " + jsonArray.length());

            JSONObject eastObj = jsonArray.getJSONObject(2);
            JSONArray eastData = eastObj.getJSONArray("rowSet");

            // Add Eastern Conference standings
            dark = false;
            addRow(0, "EASTERN", "W", "L", "%", "GB");
            for (int i = 0; i < eastData.length(); i++) {
                JSONArray arr = eastData.getJSONArray(i);
                int rank = arr.getInt(1);
                String teamName = arr.get(2).toString();
                String wins = arr.get(4).toString();
                String losses = arr.get(5).toString();
                String per = arr.get(6).toString();
                String gb = arr.get(11).toString();

                addRow(rank, teamName, wins, losses, per, gb);
            }

            JSONObject westObj = jsonArray.getJSONObject(3);
            JSONArray westData = westObj.getJSONArray("rowSet");

            // Add Western Conference standings
            dark = false;
            addRow(0, "WESTERN", "W", "L", "%", "GB");
            for (int i = 0; i < westData.length(); i++) {
                JSONArray arr = westData.getJSONArray(i);
                int rank = arr.getInt(1);
                String teamName = arr.get(2).toString();
                String wins = arr.get(4).toString();
                String losses = arr.get(5).toString();
                String per = arr.get(6).toString();
                String gb = arr.get(11).toString();

                addRow(rank, teamName, wins, losses, per, gb);
            }

            linlaHeaderProgress.setVisibility(View.GONE);
            tableLayout.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Log.e("Standings", "Voley error: " + e.toString());
        }
    }

    private void addRow(int rank, String text, String w, String l, String per, String gb) {

        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (5 * scale + 0.5f);

        TableRow row = new TableRow(context);
        TextView textTV = new TextView(context);
        TextView winsTV = new TextView(context);
        TextView lossesTV = new TextView(context);
        TextView perTV = new TextView(context);
        TextView gbTV = new TextView(context);

        TableRow.LayoutParams lgParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.38f);
        TableRow.LayoutParams smParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.13f);

        String rankText;
        if (rank == 0) {
            rankText = "";
            textTV.setTypeface(null, Typeface.BOLD);
            winsTV.setTypeface(null, Typeface.BOLD);
            lossesTV.setTypeface(null, Typeface.BOLD);
            perTV.setTypeface(null, Typeface.BOLD);
            gbTV.setTypeface(null, Typeface.BOLD);
            row.setPadding(0, dpAsPixels, 0, dpAsPixels);
        } else {
            rankText = "#" + String.valueOf(rank) + "    ";
        }
        textTV.setText(rankText + text);
        textTV.setLayoutParams(lgParams);
        textTV.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        textTV.setTextColor(context.getResources().getColor(R.color.primaryText));
        textTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);

        winsTV.setText(w);
        winsTV.setLayoutParams(smParams);
        winsTV.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        winsTV.setGravity(Gravity.CENTER);
        winsTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);

        lossesTV.setText(l);
        lossesTV.setLayoutParams(smParams);
        lossesTV.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        lossesTV.setGravity(Gravity.CENTER);
        lossesTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);

        perTV.setText(per);
        perTV.setLayoutParams(smParams);
        perTV.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        perTV.setGravity(Gravity.CENTER);
        perTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);

        gbTV.setText(gb);
        gbTV.setLayoutParams(smParams);
        gbTV.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        gbTV.setGravity(Gravity.CENTER);
        gbTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);

        row.addView(textTV);
        row.addView(winsTV);
        row.addView(lossesTV);
        row.addView(perTV);
        row.addView(gbTV);

        if (dark)
            row.setBackgroundColor(context.getResources().getColor(R.color.lightGray));
        else
            row.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        dark = !dark;

        View view = new View(context);
        view.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        row.addView(view);

        tableLayout.addView(row, new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getStandings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
