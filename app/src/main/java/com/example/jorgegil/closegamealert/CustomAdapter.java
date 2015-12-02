package com.example.jorgegil.closegamealert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jorgegil on 11/5/15.
 */
public class CustomAdapter extends BaseAdapter {
    private final Context context;
    private final String[] homeTeam, awayTeam, homeScore, awayScore, clock, period;
    private static LayoutInflater inflater = null;

    public CustomAdapter(Context context, String[] homeTeam, String[] awayTeam, String[] homeScore,
                          String[] awayScore, String[] clock, String[] period) {
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
        return homeTeam.length;
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
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView homeTeamLabel = (TextView) rowView.findViewById(R.id.homelabel);
        TextView awayTeamLabel = (TextView) rowView.findViewById(R.id.awaylabel);
        ImageView homeLogo = (ImageView) rowView.findViewById(R.id.homeicon);
        ImageView awayLogo = (ImageView) rowView.findViewById(R.id.awayicon);
        TextView homeScoreLabel = (TextView) rowView.findViewById(R.id.homescore);
        TextView awayScoreLabel = (TextView) rowView.findViewById(R.id.awayscore);
        TextView clockLabel = (TextView) rowView.findViewById(R.id.clock);
        TextView periodLabel = (TextView) rowView.findViewById(R.id.period);

        int resKeyHome = context.getResources().getIdentifier(homeTeam[position].toLowerCase(), "drawable", context.getPackageName());
        int resKeyAway = context.getResources().getIdentifier(awayTeam[position].toLowerCase(), "drawable", context.getPackageName());

        homeTeamLabel.setText(homeTeam[position]);
        awayTeamLabel.setText(awayTeam[position]);
        homeLogo.setImageResource(resKeyHome);
        awayLogo.setImageResource(resKeyAway);
        homeScoreLabel.setText(homeScore[position]);
        awayScoreLabel.setText(awayScore[position]);
        clockLabel.setText(clock[position]);
        periodLabel.setText(period[position]);

        return rowView;
    }

    public void clearData() {
        for (int i = 0; i < getCount(); i++) {
            homeTeam[i] = "";
        }
    }
}
