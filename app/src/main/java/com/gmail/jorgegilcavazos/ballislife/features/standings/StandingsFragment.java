package com.gmail.jorgegilcavazos.ballislife.features.standings;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.gmail.jorgegilcavazos.ballislife.R;
import com.gmail.jorgegilcavazos.ballislife.util.MyDebug;

import org.json.JSONArray;
import org.json.JSONObject;

public class StandingsFragment extends Fragment {
    private static final String TAG = "StandingsFragment";
    private static final String standingsURL = "http://stats.nba.com/stats/playoffpicture?LeagueID=00&SeasonID=22016";

    private Context context;
    private View rootView;
    private LinearLayout linlaHeaderProgress;
    private TableLayout tableLayout;
    private Snackbar snackbar;

    private boolean dark;

    public StandingsFragment() {

    }

    public static StandingsFragment newInstance() {
        return new StandingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_standings, container, false);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);

        tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout);

        getActivity().setTitle(R.string.standings_fragment_title);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        getStandings();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void getStandings() {
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
                if (MyDebug.LOG) {
                    Log.e(TAG, "Volley error: " + error.toString());
                }
                showSnackBar("Could not load content", true /* retry */);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private void parseStandings(String jsonString) {
        try {
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("resultSets");

            JSONObject eastObj = jsonArray.getJSONObject(2);
            JSONArray eastData = eastObj.getJSONArray("rowSet");

            // Add Eastern Conference standings
            dark = false;
            int rank = 0;
            addRow(0, "EASTERN", "W", "L", "%", "GB");
            for (int i = 0; i < eastData.length(); i++) {
                JSONArray arr = eastData.getJSONArray(i);
                if (!arr.isNull(1)) {
                    rank = arr.getInt(1);
                } else {
                    rank++;
                }
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
            rank = 0;
            addRow(0, "WESTERN", "W", "L", "%", "GB");
            for (int i = 0; i < westData.length(); i++) {
                JSONArray arr = westData.getJSONArray(i);
                if (!arr.isNull(1)) {
                    rank = arr.getInt(1);
                } else {
                    rank++;
                }
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
            if (MyDebug.LOG) {
                Log.e(TAG, "Volley error: " + e.toString());
                Log.d(TAG, "Could not parse standings");
            }
            showSnackBar("Error parsing data", true);
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
            row.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGray));
        else
            row.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        dark = !dark;

        View view = new View(context);
        view.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        row.addView(view);

        tableLayout.addView(row, new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void showSnackBar(String message, boolean retry) {
        snackbar = Snackbar.make(rootView, message,
                Snackbar.LENGTH_INDEFINITE);
        if (retry) {
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getStandings();
                }
            });
        }
        linlaHeaderProgress.setVisibility(View.GONE);
        snackbar.show();
    }

    private void dismissSnackbar() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                dismissSnackbar();
                getStandings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        dismissSnackbar();
        super.onPause();
    }
}
