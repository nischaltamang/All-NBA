package com.gmail.jorgegilcavazos.ballislife.features.games;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.jorgegilcavazos.ballislife.features.data.NbaGame;
import com.gmail.jorgegilcavazos.ballislife.R;
import com.gmail.jorgegilcavazos.ballislife.util.Utilities;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecyclerView Adapter used by the {@link GamesFragment} to display a list of games.
 */
public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
    private Context context;
    private List<NbaGame> nbaGameList;
    private GamesFragment.GameItemListener gameItemListener;

    public GameAdapter(List<NbaGame> nbaGames,
                       GamesFragment.GameItemListener itemListener) {
        nbaGameList = nbaGames;
        gameItemListener = itemListener;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_game,
                parent, false);
        context = parent.getContext();
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GameViewHolder holder, int position) {
        NbaGame nbaGame = nbaGameList.get(position);

        int resKeyHome = context.getResources().getIdentifier(nbaGame.getHomeTeamAbbr()
                .toLowerCase(), "drawable", context.getPackageName());
        int resKeyAway = context.getResources().getIdentifier(nbaGame.getAwayTeamAbbr()
                .toLowerCase(), "drawable", context.getPackageName());

        holder.tvHomeTeam.setText(nbaGame.getHomeTeamAbbr());
        holder.tvAwayTeam.setText(nbaGame.getAwayTeamAbbr());
        holder.ivHomeLogo.setImageResource(resKeyHome);
        holder.ivAwayLogo.setImageResource(resKeyAway);
        holder.tvHomeScore.setText(nbaGame.getHomeTeamScore());
        holder.tvAwayScore.setText(nbaGame.getAwayTeamScore());
        holder.tvClock.setText(nbaGame.getGameClock());
        holder.tvPeriod.setText(Utilities.getPeriodString(nbaGame.getPeriodValue(),
                nbaGame.getPeriodName()));

        holder.tvHomeScore.setVisibility(View.GONE);
        holder.tvAwayScore.setVisibility(View.GONE);
        holder.tvClock.setVisibility(View.GONE);
        holder.tvPeriod.setVisibility(View.GONE);
        holder.tvFinal.setVisibility(View.GONE);
        holder.tvTime.setVisibility(View.GONE);

        switch (nbaGame.getGameStatus()) {
            case NbaGame.PRE_GAME:
                holder.tvTime.setVisibility(View.VISIBLE);
                holder.tvTime.setText(nbaGame.getPeriodStatus());
                break;
            case NbaGame.IN_GAME:
                holder.tvHomeScore.setVisibility(View.VISIBLE);
                holder.tvAwayScore.setVisibility(View.VISIBLE);
                holder.tvClock.setVisibility(View.VISIBLE);
                holder.tvPeriod.setVisibility(View.VISIBLE);
                break;
            case NbaGame.POST_GAME:
                holder.tvHomeScore.setVisibility(View.VISIBLE);
                holder.tvAwayScore.setVisibility(View.VISIBLE);
                holder.tvFinal.setVisibility(View.VISIBLE);
                holder.tvFinal.setText("FINAL");
                break;
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameItemListener.onGameClick(nbaGameList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != nbaGameList ? nbaGameList.size() : 0;
    }

    public void swap(List<NbaGame> data) {
        nbaGameList.clear();
        nbaGameList.addAll(data);
        notifyDataSetChanged();
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {
        View container;
        @BindView(R.id.homelabel) TextView tvHomeTeam;
        @BindView(R.id.awaylabel) TextView tvAwayTeam;
        @BindView(R.id.homescore) TextView tvHomeScore;
        @BindView(R.id.awayscore) TextView tvAwayScore;
        @BindView(R.id.clock) TextView tvClock;
        @BindView(R.id.period) TextView tvPeriod;
        @BindView(R.id.extraLabel) TextView tvTime;
        @BindView(R.id.extraLabel2) TextView tvFinal;
        @BindView(R.id.homeicon) ImageView ivHomeLogo;
        @BindView(R.id.awayicon) ImageView ivAwayLogo;

        public GameViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            container = view;
        }
    }
}
