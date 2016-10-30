package com.gmail.jorgegilcavazos.ballislife.features.games;

import com.gmail.jorgegilcavazos.ballislife.features.data.GamesRepository;
import com.gmail.jorgegilcavazos.ballislife.features.data.NbaGame;
import com.gmail.jorgegilcavazos.ballislife.util.GameUtils;

import java.util.Calendar;
import java.util.List;

public class GamesPresenter implements GamesContract.UserActionsListener {

    private final GamesContract.View gamesView;
    private final GamesRepository gamesRepository;

    public GamesPresenter(GamesContract.View gamesView, GamesRepository gamesRepository) {
        this.gamesView = gamesView;
        this.gamesRepository = gamesRepository;
    }

    @Override
    public Calendar changeNavigatorDate(Calendar date, int delta) {
        if (date == null) {
            date = Calendar.getInstance();
        }
        date.add(Calendar.DAY_OF_YEAR, delta);
        gamesView.setNavigatorDate(date);
        return date;
    }

    @Override
    public void loadNavigatorDate(Calendar date) {
        gamesView.setNavigatorDate(date);
    }

    @Override
    public void loadGames(Calendar date, boolean forceUpdate) {
        gamesView.dismissSnackbar();
        gamesView.hideGames();
        gamesView.setNoGamesIndicator(false);
        gamesView.setProgressIndicator(true);
        if (date == null) {
            date = Calendar.getInstance();
        }
        gamesRepository.getGames(date.getTime(), new GamesRepository.LoadGamesCallback() {
            @Override
            public void onGamesLoaded(List<NbaGame> games) {
                gamesView.setProgressIndicator(false);
                gamesView.showGames(games);
                if (games.size() == 0) {
                    gamesView.setNoGamesIndicator(true);
                }
            }

            @Override
            public void onFailure(String error) {
                gamesView.setProgressIndicator(false);
                gamesView.showSnackbar(true);
            }
        });
    }

    @Override
    public void openGameDetails(NbaGame requestedGame) {
        gamesView.showGameDetails(requestedGame);
    }

    @Override
    public void updateGames(String gameData) {
        gamesView.showGames(GameUtils.getGamesListFromJson(gameData));
    }

    @Override
    public void dismissSnackbar() {
        gamesView.dismissSnackbar();
    }
}
