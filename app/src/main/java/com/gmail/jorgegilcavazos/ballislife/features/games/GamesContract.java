package com.gmail.jorgegilcavazos.ballislife.features.games;

import com.gmail.jorgegilcavazos.ballislife.features.data.NbaGame;

import java.util.Calendar;
import java.util.List;

public interface GamesContract {

    interface View {

        void setProgressIndicator(boolean active);

        void setNavigatorDate(Calendar date);

        void hideGames();

        void showGames(List<NbaGame> games);

        void showGameDetails(NbaGame game);

        void setNoGamesIndicator(boolean active);

        void showSnackbar(boolean canReload);

        void dismissSnackbar();
    }

    interface UserActionsListener {

        Calendar changeNavigatorDate(Calendar calendar, int delta);

        void loadNavigatorDate(Calendar date);

        void loadGames(Calendar date, boolean forceUpdate);

        void openGameDetails(NbaGame requestedGame);

        void updateGames(String gameData);

        void dismissSnackbar();
    }
}
