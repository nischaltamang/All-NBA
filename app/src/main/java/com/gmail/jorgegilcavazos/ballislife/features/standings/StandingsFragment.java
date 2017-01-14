package com.gmail.jorgegilcavazos.ballislife.features.standings;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gmail.jorgegilcavazos.ballislife.R;
import com.gmail.jorgegilcavazos.ballislife.features.model.TeamRecord;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StandingsFragment extends MvpFragment<StandingsView, StandingsPresenter>
        implements StandingsView, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "StandingsFragment";

    @BindView(R.id.standings_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.standings_table_layout) TableLayout tableLayout;

    private Snackbar snackbar;
    private Unbinder unbinder;

    public StandingsFragment() {
        // Required empty public constructor.
    }

    public static StandingsFragment newInstance() {
        return new StandingsFragment();
    }

    @Override
    public StandingsPresenter createPresenter() {
        return new StandingsPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadStandings();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_standings_2, container, false);
        getActivity().setTitle(R.string.standings_fragment_title);

        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                presenter.loadStandings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        presenter.dismissSnackbar();
        super.onPause();
    }

    @Override
    public void onRefresh() {
        presenter.loadStandings();
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        swipeRefreshLayout.setRefreshing(active);
    }

    @Override
    public void showStandings(List<TeamRecord> eastStandings, List<TeamRecord> westStandings) {
        tableLayout.removeAllViews();

        boolean dark = true;

        // EAST rows
        addRow(0, "EAST", "W", "L", "%", "GB", dark);
        for (TeamRecord tr : eastStandings) {
            dark = !dark;
            addRow(tr.getRecord(), tr.getTeamName(), tr.getWins(), tr.getLosses(),
                    tr.getPercentage(), tr.getGamesBehind(), dark);
        }

        // WEST rows
        dark = !dark;
        addRow(0, "WEST", "W", "L", "%", "GB", dark);
        for (TeamRecord tr : westStandings) {
            dark = !dark;
            addRow(tr.getRecord(), tr.getTeamName(), tr.getWins(), tr.getLosses(),
                    tr.getPercentage(), tr.getGamesBehind(), dark);
        }

    }

    @Override
    public void hideStandings() {
        tableLayout.removeAllViews();
    }

    @Override
    public void showSnackbar(boolean canReload) {
        snackbar = Snackbar.make(getView(), R.string.failed_standings_data,
                Snackbar.LENGTH_INDEFINITE);
        if (canReload) {
            snackbar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.loadStandings();
                }
            });
        }
        snackbar.show();
    }

    @Override
    public void dismissSnackbar() {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    /**
     * Adds a row to the tablelayout that contains the team standings.
     */
    private void addRow(int rank, String teamName, String w, String l, String per, String gb,
                        boolean dark) {
        Context context = getActivity();
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (5 * scale + 0.5f);

        TableRow row = new TableRow(context);
        TextView textTV = new TextView(context);
        TextView winsTV = new TextView(context);
        TextView lossesTV = new TextView(context);
        TextView perTV = new TextView(context);
        TextView gbTV = new TextView(context);

        TableRow.LayoutParams lgParams = new TableRow.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0.38f);
        TableRow.LayoutParams smParams = new TableRow.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0.13f);

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
        textTV.setText(rankText + teamName);
        textTV.setLayoutParams(lgParams);
        textTV.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        textTV.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
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

        View view = new View(context);
        view.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        row.addView(view);

        tableLayout.addView(row, new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
