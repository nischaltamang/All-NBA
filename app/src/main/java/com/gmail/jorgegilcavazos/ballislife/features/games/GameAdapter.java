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

        holder.mHomeTeamLabel.setText(nbaGame.getHomeTeamAbbr());
        holder.mAwayTeamLabel.setText(nbaGame.getAwayTeamAbbr());
        holder.homeLogo.setImageResource(resKeyHome);
        holder.awayLogo.setImageResource(resKeyAway);
        holder.mHomeScoreLabel.setText(nbaGame.getHomeTeamScore());
        holder.mAwayScoreLabel.setText(nbaGame.getAwayTeamScore());
        holder.mClockLabel.setText(nbaGame.getGameClock());
        holder.mPeriodLabel.setText(Utilities.getPeriodString(nbaGame.getPeriodValue(),
                nbaGame.getPeriodName()));

        holder.mHomeScoreLabel.setVisibility(View.GONE);
        holder.mAwayScoreLabel.setVisibility(View.GONE);
        holder.mClockLabel.setVisibility(View.GONE);
        holder.mPeriodLabel.setVisibility(View.GONE);
        holder.mFinalLabel.setVisibility(View.GONE);
        holder.mTimeLabel.setVisibility(View.GONE);

        switch (nbaGame.getGameStatus()) {
            case NbaGame.PRE_GAME:
                holder.mTimeLabel.setVisibility(View.VISIBLE);
                holder.mTimeLabel.setText(nbaGame.getPeriodStatus());
                break;
            case NbaGame.IN_GAME:
                holder.mHomeScoreLabel.setVisibility(View.VISIBLE);
                holder.mAwayScoreLabel.setVisibility(View.VISIBLE);
                holder.mClockLabel.setVisibility(View.VISIBLE);
                holder.mPeriodLabel.setVisibility(View.VISIBLE);
                break;
            case NbaGame.POST_GAME:
                holder.mHomeScoreLabel.setVisibility(View.VISIBLE);
                holder.mAwayScoreLabel.setVisibility(View.VISIBLE);
                holder.mFinalLabel.setVisibility(View.VISIBLE);
                holder.mFinalLabel.setText("FINAL");
                break;
        }

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
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
        public View mContainer;
        public TextView mHomeTeamLabel, mAwayTeamLabel, mHomeScoreLabel, mAwayScoreLabel,
                mClockLabel, mPeriodLabel, mTimeLabel, mFinalLabel;
        public ImageView homeLogo, awayLogo;

        public GameViewHolder(View view) {
            super(view);
            mContainer = view;
            mHomeTeamLabel = (TextView) view.findViewById(R.id.homelabel);
            mAwayTeamLabel = (TextView) view.findViewById(R.id.awaylabel);
            homeLogo = (ImageView) view.findViewById(R.id.homeicon);
            awayLogo = (ImageView) view.findViewById(R.id.awayicon);
            mHomeScoreLabel = (TextView) view.findViewById(R.id.homescore);
            mAwayScoreLabel = (TextView) view.findViewById(R.id.awayscore);
            mClockLabel = (TextView) view.findViewById(R.id.clock);
            mPeriodLabel = (TextView) view.findViewById(R.id.period);
            mTimeLabel = (TextView) view.findViewById(R.id.extraLabel);
            mFinalLabel = (TextView) view.findViewById(R.id.extraLabel2);
        }
    }
}
