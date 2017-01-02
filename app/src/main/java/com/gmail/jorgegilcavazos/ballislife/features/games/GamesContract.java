package com.gmail.jorgegilcavazos.ballislife.features.games;

import com.gmail.jorgegilcavazos.ballislife.features.BasePresenter;
import com.gmail.jorgegilcavazos.ballislife.features.BaseView;
import com.gmail.jorgegilcavazos.ballislife.features.data.NbaGame;

import java.util.Calendar;
import java.util.List;

/**
 * Contract specification between the {@link GamesFragment} and the {@link GamesPresenter}.
 */
public interface GamesContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void setDateNavigatorText(String dateText);

        void hideGames();

        void showGames(List<NbaGame> games);

        void showGameDetails(NbaGame game);

        void setNoGamesIndicator(boolean active);

        void showSnackbar(boolean canReload);

        void dismissSnackbar();
    }

    interface Presenter extends BasePresenter {

        void addOrSubstractDay(int delta);

        void loadDateNavigatorText();

        void loadGames(boolean forceUpdate);

        void openGameDetails(NbaGame requestedGame);

        void updateGames(String gameData);

        void dismissSnackbar();
    }
}
