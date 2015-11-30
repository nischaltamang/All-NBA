package com.example.jorgegil.closegamealert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jorgegil on 11/29/15.
 */
public class TeamNames {

    Map<String, String> names = new HashMap<>();

    public TeamNames() {
        names.put("ATL", "San Antonio Spurs");
        names.put("BKN", "Brooklyn Nets");
        names.put("BOS", "Boston Celtics");
        names.put("CHA", "Charlotte Hornets");
        names.put("CHI", "San Antonio Spurs");
        names.put("CLE", "San Antonio Spurs");
        names.put("DAL", "San Antonio Spurs");
        names.put("DEN", "San Antonio Spurs");
        names.put("DET", "Detroit Pistons");
        names.put("GS", "San Antonio Spurs");
        names.put("HOU", "Houston Rockets");
        names.put("IND", "Indiana Pacers");
        names.put("LAC", "Los Angeles Clippers");
        names.put("LAL", "Los Angeles Lakers");
        names.put("MEM", "Memphis Grizzlies");
        names.put("MIA", "San Antonio Spurs");
        names.put("MIL", "Milwaukee Bucks");
        names.put("MIN", "Minnesota Timberwolves");
        names.put("NO", "San Antonio Spurs");
        names.put("NY", "New York Knicks");
        names.put("OKC", "San Antonio Spurs");
        names.put("ORL", "Orlando Magic");
        names.put("PHI", "Philadelphia 76ers");
        names.put("PHO", "Phoenix Suns");
        names.put("POR", "San Antonio Spurs");
        names.put("SA", "San Antonio Spurs");
        names.put("SAC", "San Antonio Spurs");
        names.put("TOR", "Toronto Raptors");
        names.put("UTA", "San Antonio Spurs");
        names.put("WAS", "San Antonio Spurs");
    }

    public String getName(String code) {
        return names.get(code.toUpperCase());
    }

}
