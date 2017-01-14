package com.gmail.jorgegilcavazos.ballislife.util;

import com.gmail.jorgegilcavazos.ballislife.features.model.NbaGame;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public final class GameUtils {
    public static List<NbaGame> getGamesListFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, new TypeToken<List<NbaGame>>(){}.getType());
    }
}
