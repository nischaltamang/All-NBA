package com.gmail.jorgegilcavazos.ballislife.features.standings;

import com.gmail.jorgegilcavazos.ballislife.features.model.StandingsResult;
import com.gmail.jorgegilcavazos.ballislife.features.model.TeamRecord;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class StandingsUtils {

    public static StandingsResult parseStandings(ResponseBody responseBody) {
        StandingsResult result = new StandingsResult();
        List<TeamRecord> eastStandings = new ArrayList<>();
        List<TeamRecord> westStandings = new ArrayList<>();

        try {
            String jsonString = responseBody.string();

            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("resultSets");

            // Eastern conference object
            JSONObject eastObj = jsonArray.getJSONObject(2);
            JSONArray eastData = eastObj.getJSONArray("rowSet");

            // Add Eastern Conference standings
            /**
            dark = false;
            int rank = 0;
            addRow(0, "EASTERN", "W", "L", "%", "GB");
             */

            int rank = 0;
            for (int i = 0; i < eastData.length(); i++) {
                JSONArray arr = eastData.getJSONArray(i);
                if (!arr.isNull(1)) {
                    rank = arr.getInt(1);
                } else {
                    rank++;
                }
                String teamName = arr.get(2).toString();
                String wins = arr.get(4).toString();
                String losses = arr.get(5).toString();
                String per = arr.get(6).toString();
                String gb = arr.get(11).toString();

                eastStandings.add(new TeamRecord(rank, teamName, wins, losses, per, gb));

                //addRow(rank, teamName, wins, losses, per, gb);
            }


            // Western conference object
            JSONObject westObj = jsonArray.getJSONObject(3);
            JSONArray westData = westObj.getJSONArray("rowSet");

            // Add Western Conference standings
            /*
            dark = false;
            rank = 0;
            addRow(0, "WESTERN", "W", "L", "%", "GB");
            */

            rank = 0;
            for (int i = 0; i < westData.length(); i++) {
                JSONArray arr = westData.getJSONArray(i);
                if (!arr.isNull(1)) {
                    rank = arr.getInt(1);
                } else {
                    rank++;
                }
                String teamName = arr.get(2).toString();
                String wins = arr.get(4).toString();
                String losses = arr.get(5).toString();
                String per = arr.get(6).toString();
                String gb = arr.get(11).toString();

                westStandings.add(new TeamRecord(rank, teamName, wins, losses, per, gb));

                //addRow(rank, teamName, wins, losses, per, gb);
            }

            result.setEastStandings(eastStandings);
            result.setWestStandings(westStandings);

        } catch (Exception e) {
            result.setException(e);
        }

        return result;
    }
}
