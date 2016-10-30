package com.gmail.jorgegilcavazos.ballislife.Games;

import com.gmail.jorgegilcavazos.ballislife.General.NBAGame;

import java.util.Calendar;
import java.util.List;

public interface GamesContract {

    interface View {

        void setProgressIndicator(boolean active);

        void setNavigatorDate(Calendar date);

        void hideGames();

        void showGames(List<NBAGame> games);

        void showGameDetails(NBAGame game);

        void setNoGamesIndicator(boolean active);

        void showSnackbar(boolean canReload);

        void dismissSnackbar();
    }

    interface UserActionsListener {

        Calendar changeNavigatorDate(Calendar calendar, int delta);

        void loadNavigatorDate(Calendar date);

        void loadGames(Calendar date, boolean forceUpdate);

        void openGameDetails(NBAGame requestedGame);

        void updateGames(String gameData);

        void dismissSnackbar();
    }
}
