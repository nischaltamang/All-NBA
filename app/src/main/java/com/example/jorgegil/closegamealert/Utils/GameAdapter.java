package com.example.jorgegil.closegamealert.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jorgegil.closegamealert.General.NBAGame;
import com.example.jorgegil.closegamealert.R;

import java.util.List;

public class GameAdapter extends BaseAdapter {
    private final Context context;
    private final List<NBAGame> nbaGames;
    private static LayoutInflater inflater = null;

    public GameAdapter(Context context, List<NBAGame> nbaGames) {
        this.context = context;
        this.nbaGames = nbaGames;
    }

    @Override
    public int getCount() {
        return nbaGames.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_game, parent, false);
        TextView homeTeamLabel = (TextView) rowView.findViewById(R.id.homelabel);
        TextView awayTeamLabel = (TextView) rowView.findViewById(R.id.awaylabel);
        ImageView homeLogo = (ImageView) rowView.findViewById(R.id.homeicon);
        ImageView awayLogo = (ImageView) rowView.findViewById(R.id.awayicon);
        TextView homeScoreLabel = (TextView) rowView.findViewById(R.id.homescore);
        TextView awayScoreLabel = (TextView) rowView.findViewById(R.id.awayscore);
        TextView clockLabel = (TextView) rowView.findViewById(R.id.clock);
        TextView periodLabel = (TextView) rowView.findViewById(R.id.period);
        TextView extraLabel = (TextView) rowView.findViewById(R.id.extraLabel);
        TextView finalLabel = (TextView) rowView.findViewById(R.id.extraLabel2);

        NBAGame nbaGame = nbaGames.get(position);
        Log.d("GameAdapter", nbaGame.getHomeTeamAbbr() + "-" + nbaGame.getAwayTeamAbbr());

        int resKeyHome = context.getResources().getIdentifier(nbaGame.getHomeTeamAbbr().toLowerCase(),
                "drawable", context.getPackageName());
        int resKeyAway = context.getResources().getIdentifier(nbaGame.getAwayTeamAbbr().toLowerCase(),
                "drawable", context.getPackageName());

        homeTeamLabel.setText(nbaGame.getHomeTeamAbbr());
        awayTeamLabel.setText(nbaGame.getAwayTeamAbbr());
        homeLogo.setImageResource(resKeyHome);
        awayLogo.setImageResource(resKeyAway);
        homeScoreLabel.setText(nbaGame.getHomeTeamScore());
        awayScoreLabel.setText(nbaGame.getAwayTeamScore());
        clockLabel.setText(nbaGame.getGameClock());
        periodLabel.setText(String.valueOf(nbaGame.getPeriodValue()));

        switch (nbaGame.getGameStatus()) {
            case NBAGame.PRE_GAME:
                extraLabel.setVisibility(View.VISIBLE);
                finalLabel.setVisibility(View.GONE);
                extraLabel.setText(nbaGame.getPeriodStatus());
                break;
            case NBAGame.IN_GAME:
                extraLabel.setVisibility(View.GONE);
                finalLabel.setVisibility(View.GONE);
                break;
            case NBAGame.POST_GAME:
                extraLabel.setVisibility(View.GONE);
                periodLabel.setVisibility(View.INVISIBLE);
                clockLabel.setVisibility(View.INVISIBLE);
                finalLabel.setVisibility(View.VISIBLE);
                finalLabel.setText("FINAL");
                break;
        }

        return rowView;
    }
}
