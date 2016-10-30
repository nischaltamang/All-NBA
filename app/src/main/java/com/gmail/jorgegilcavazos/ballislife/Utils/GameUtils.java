package com.gmail.jorgegilcavazos.ballislife.Utils;

import com.gmail.jorgegilcavazos.ballislife.General.NBAGame;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public final class GameUtils {
    public static List<NBAGame> getGamesListFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, new TypeToken<List<NBAGame>>(){}.getType());
    }
}
