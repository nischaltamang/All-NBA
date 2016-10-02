package com.example.jorgegil.closegamealert.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jorgegil.closegamealert.General.NBAGame;
import com.example.jorgegil.closegamealert.R;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
    private Context mContext;
    private List<NBAGame> mNbaGames;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public GameAdapter(Context context, List<NBAGame> nbaGames,
                       OnItemClickListener onItemClickListener) {
        mContext = context;
        mNbaGames = nbaGames;
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_game,
                parent, false);

        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GameViewHolder holder, int position) {
        NBAGame nbaGame = mNbaGames.get(position);

        int resKeyHome = mContext.getResources().getIdentifier(nbaGame.getHomeTeamAbbr()
                .toLowerCase(), "drawable", mContext.getPackageName());
        int resKeyAway = mContext.getResources().getIdentifier(nbaGame.getAwayTeamAbbr()
                .toLowerCase(), "drawable", mContext.getPackageName());

        holder.mHomeTeamLabel.setText(nbaGame.getHomeTeamAbbr());
        holder.mAwayTeamLabel.setText(nbaGame.getAwayTeamAbbr());
        holder.homeLogo.setImageResource(resKeyHome);
        holder.awayLogo.setImageResource(resKeyAway);
        holder.mHomeScoreLabel.setText(nbaGame.getHomeTeamScore());
        holder.mAwayScoreLabel.setText(nbaGame.getAwayTeamScore());
        holder.mClockLabel.setText(nbaGame.getGameClock());
        holder.mPeriodLabel.setText(String.valueOf(nbaGame.getPeriodValue())
                + " " + nbaGame.getPeriodName());

        holder.mHomeScoreLabel.setVisibility(View.GONE);
        holder.mAwayScoreLabel.setVisibility(View.GONE);
        holder.mClockLabel.setVisibility(View.GONE);
        holder.mPeriodLabel.setVisibility(View.GONE);
        holder.mFinalLabel.setVisibility(View.GONE);
        holder.mTimeLabel.setVisibility(View.GONE);

        switch (nbaGame.getGameStatus()) {
            case NBAGame.PRE_GAME:
                holder.mTimeLabel.setVisibility(View.VISIBLE);
                holder.mTimeLabel.setText(nbaGame.getPeriodStatus());
                break;
            case NBAGame.IN_GAME:
                holder.mHomeScoreLabel.setVisibility(View.VISIBLE);
                holder.mAwayScoreLabel.setVisibility(View.VISIBLE);
                holder.mClockLabel.setVisibility(View.VISIBLE);
                holder.mPeriodLabel.setVisibility(View.VISIBLE);
                break;
            case NBAGame.POST_GAME:
                holder.mHomeScoreLabel.setVisibility(View.VISIBLE);
                holder.mAwayScoreLabel.setVisibility(View.VISIBLE);
                holder.mFinalLabel.setVisibility(View.VISIBLE);
                holder.mFinalLabel.setText("FINAL");
                break;
        }

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != mNbaGames ? mNbaGames.size() : 0;
    }

    public void swap(List<NBAGame> data) {
        mNbaGames.clear();
        mNbaGames.addAll(data);
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
