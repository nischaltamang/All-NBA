package com.example.jorgegil.closegamealert.General;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jorgegil on 11/29/15.
 */
public class TeamNames {

    Map<String, String> names = new HashMap<>();

    public TeamNames() {
        names.put("ATL", "Atlanta Hawks");
        names.put("BKN", "Brooklyn Nets");
        names.put("BOS", "Boston Celtics");
        names.put("CHA", "Charlotte Hornets");
        names.put("CHI", "Chicago Bulls");
        names.put("CLE", "Cleveland Cavaliers");
        names.put("DAL", "Dallas Mavericks");
        names.put("DEN", "Denver Nuggets");
        names.put("DET", "Detroit Pistons");
        names.put("GS", "Golden State Warriors");
        names.put("HOU", "Houston Rockets");
        names.put("IND", "Indiana Pacers");
        names.put("LAC", "Los Angeles Clippers");
        names.put("LAL", "Los Angeles Lakers");
        names.put("MEM", "Memphis Grizzlies");
        names.put("MIA", "Miami Heat");
        names.put("MIL", "Milwaukee Bucks");
        names.put("MIN", "Minnesota Timberwolves");
        names.put("NO", "New Orleans Pelicans");
        names.put("NY", "New York Knicks");
        names.put("OKC", "Oklahoma City Thunder");
        names.put("ORL", "Orlando Magic");
        names.put("PHI", "Philadelphia 76ers");
        names.put("PHX", "Phoenix Suns"); //ESPN
        names.put("POR", "Portland Trail Blazers");
        names.put("SA", "San Antonio Spurs");
        names.put("SAC", "Sacramento Kings");
        names.put("TOR", "Toronto Raptors");
        names.put("UTAH", "Utah Jazz");
        names.put("WSH", "Washington Wizards"); //ESPN
    }

    public String getName(String code) {
        return names.get(code.toUpperCase());
    }

}
