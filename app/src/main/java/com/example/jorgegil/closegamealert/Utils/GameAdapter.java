package com.example.jorgegil.closegamealert.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jorgegil.closegamealert.R;

import java.util.List;

public class GameAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> homeTeam, awayTeam, homeScore, awayScore, clock, period;
    private static LayoutInflater inflater = null;

    public GameAdapter(Context context, List<String> homeTeam, List<String> awayTeam,
                       List<String> homeScore, List<String> awayScore,
                       List<String> clock, List<String> period) {
        this.context = context;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.clock = clock;
        this.period = period;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return homeTeam.size();
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
        View rowView = inflater.inflate(R.layout.row_game, parent, false);
        TextView homeTeamLabel = (TextView) rowView.findViewById(R.id.homelabel);
        TextView awayTeamLabel = (TextView) rowView.findViewById(R.id.awaylabel);
        ImageView homeLogo = (ImageView) rowView.findViewById(R.id.homeicon);
        ImageView awayLogo = (ImageView) rowView.findViewById(R.id.awayicon);
        TextView homeScoreLabel = (TextView) rowView.findViewById(R.id.homescore);
        TextView awayScoreLabel = (TextView) rowView.findViewById(R.id.awayscore);
        TextView clockLabel = (TextView) rowView.findViewById(R.id.clock);
        TextView periodLabel = (TextView) rowView.findViewById(R.id.period);

        int resKeyHome = context.getResources().getIdentifier(homeTeam.get(position).toLowerCase(),
                "drawable", context.getPackageName());
        int resKeyAway = context.getResources().getIdentifier(awayTeam.get(position).toLowerCase(),
                "drawable", context.getPackageName());

        homeTeamLabel.setText(homeTeam.get(position));
        awayTeamLabel.setText(awayTeam.get(position));
        homeLogo.setImageResource(resKeyHome);
        awayLogo.setImageResource(resKeyAway);
        homeScoreLabel.setText(homeScore.get(position));
        awayScoreLabel.setText(awayScore.get(position));
        clockLabel.setText(clock.get(position));
        periodLabel.setText(period.get(position));

        return rowView;
    }
}
