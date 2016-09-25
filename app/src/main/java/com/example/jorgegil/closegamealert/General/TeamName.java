package com.example.jorgegil.closegamealert.General;

/**
 * Enumerate all NBA teams by their abbreviation.
 */
public enum TeamName {
    ATL("Atlanta Hawks"),
    BKN("Brooklyn Nets"),
    BOS("Boston Celtics"),
    CHA("Charlotte Hornets"),
    CHI("Chicago Bulls"),
    CLE("Cleveland Cavaliers"),
    DAL("Dallas Mavericks"),
    DEN("Denver Nuggets"),
    DET("Detroit Pistons"),
    GSW("Golden State Warriors"),
    HOU("Houston Rockets"),
    IND("Indiana Pacers"),
    LAC("Los Angeles Clippers"),
    LAL("Los Angeles Lakers"),
    MEM("Memphis Grizzlies"),
    MIA("Miami Heat"),
    MIL("Milwaukee Bucks"),
    MIN("Minnesota Timberwolves"),
    NOP("New Orleans Pelicans"),
    NYK("New York Knicks"),
    OKC("Oklahoma City Thunder"),
    ORL("Orlando Magic"),
    PHI("Philadelphia 76ers"),
    PHX("Phoenix Suns"),
    POR("Portland Trail Blazers"),
    SAS("San Antonio Spurs"),
    SAC("Sacramento Kings"),
    TOR("Toronto Raptors"),
    UTA("Utah Jazz"),
    WAS("Washington Wizards");

    private String teamName;

    TeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }
}
