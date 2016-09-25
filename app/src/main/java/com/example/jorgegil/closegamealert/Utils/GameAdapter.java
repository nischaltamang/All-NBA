package com.example.jorgegil.closegamealert.Utils;

import android.content.Context;
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

        NBAGame nbaGame = nbaGames.get(position);

        int resKeyHome = context.getResources().getIdentifier(nbaGame.getHomeTeam().toLowerCase(),
                "drawable", context.getPackageName());
        int resKeyAway = context.getResources().getIdentifier(nbaGame.getAwayTeam().toLowerCase(),
                "drawable", context.getPackageName());

        homeTeamLabel.setText(nbaGame.getHomeTeam());
        awayTeamLabel.setText(nbaGame.getAwayTeam());
        homeLogo.setImageResource(resKeyHome);
        awayLogo.setImageResource(resKeyAway);
        homeScoreLabel.setText(nbaGame.getHomeScore());
        awayScoreLabel.setText(nbaGame.getAwayScore());
        clockLabel.setText(nbaGame.getClock());
        periodLabel.setText(String.valueOf(nbaGame.getPeriod()));

        return rowView;
    }
}
