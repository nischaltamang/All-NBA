package com.gmail.jorgegilcavazos.ballislife.Games;

import com.gmail.jorgegilcavazos.ballislife.General.NBAGame;
import com.gmail.jorgegilcavazos.ballislife.Utils.GameUtils;

import java.util.Calendar;
import java.util.List;

public class GamesPresenter implements GamesContract.UserActionsListener {

    private final GamesContract.View mGamesView;
    private final GamesRepository mGamesRepository;

    public GamesPresenter(GamesContract.View gamesView, GamesRepository gamesRepository) {
        mGamesView = gamesView;
        mGamesRepository = gamesRepository;
    }

    @Override
    public Calendar changeNavigatorDate(Calendar date, int delta) {
        if (date == null) {
            date = Calendar.getInstance();
        }
        date.add(Calendar.DAY_OF_YEAR, delta);
        mGamesView.setNavigatorDate(date);
        return date;
    }

    @Override
    public void loadNavigatorDate(Calendar date) {
        mGamesView.setNavigatorDate(date);
    }

    @Override
    public void loadGames(Calendar date, boolean forceUpdate) {
        mGamesView.dismissSnackbar();
        mGamesView.hideGames();
        mGamesView.setNoGamesIndicator(false);
        mGamesView.setProgressIndicator(true);
        if (date == null) {
            date = Calendar.getInstance();
        }
        mGamesRepository.getGames(date.getTime(), new GamesRepository.LoadGamesCallback() {
            @Override
            public void onGamesLoaded(List<NBAGame> games) {
                mGamesView.setProgressIndicator(false);
                mGamesView.showGames(games);
                if (games.size() == 0) {
                    mGamesView.setNoGamesIndicator(true);
                }
            }

            @Override
            public void onFailure(String error) {
                mGamesView.setProgressIndicator(false);
                mGamesView.showSnackbar(true);
            }
        });
    }

    @Override
    public void openGameDetails(NBAGame requestedGame) {
        mGamesView.showGameDetails(requestedGame);
    }

    @Override
    public void updateGames(String gameData) {
        mGamesView.showGames(GameUtils.getGamesListFromJson(gameData));
    }

    @Override
    public void dismissSnackbar() {
        mGamesView.dismissSnackbar();
    }
}
