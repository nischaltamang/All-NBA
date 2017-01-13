package com.gmail.jorgegilcavazos.ballislife.features.games;

import android.util.Log;

import com.gmail.jorgegilcavazos.ballislife.features.data.NbaGame;
import com.gmail.jorgegilcavazos.ballislife.network.NbaGamesService;
import com.gmail.jorgegilcavazos.ballislife.util.DateFormatUtil;
import com.gmail.jorgegilcavazos.ballislife.util.GameUtils;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GamesPresenter extends MvpBasePresenter<GamesView> {

    private List<NbaGame> gamesList;
    private Calendar selectedDate;

    private CompositeDisposable disposables;

    public GamesPresenter() {
        disposables = new CompositeDisposable();
        selectedDate = Calendar.getInstance();
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (!retainInstance) {
            disposables.clear();
        }
    }

    public void loadGames() {
        loadDateNavigatorText();
        getView().setLoadingIndicator(true);
        getView().dismissSnackbar();
        getView().hideGames();
        getView().setNoGamesIndicator(false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://phpstack-4722-10615-67130.cloudwaysapps.com/api/v1/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NbaGamesService gamesService = retrofit.create(NbaGamesService.class);

        Observable<List<NbaGame>> games = gamesService.listGames(
                DateFormatUtil.getDashedDateString(selectedDate.getTime()));

        disposables.clear();
        disposables.add(
                games.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<NbaGame>>() {
                    @Override
                    public void onNext(List<NbaGame> nbaGames) {
                        getView().setLoadingIndicator(false);
                        gamesList = nbaGames;
                        getView().showGames(gamesList);
                        if (gamesList.size() == 0) {
                            getView().setNoGamesIndicator(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().showSnackbar(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    public void addOrSubstractDay(int delta) {
        selectedDate.add(Calendar.DAY_OF_YEAR, delta);
        loadDateNavigatorText();
        loadGames();
    }

    public void loadDateNavigatorText() {
        String dateText = DateFormatUtil.formatNavigatorDate(selectedDate.getTime());
        getView().setDateNavigatorText(dateText);
    }

    public void openGameDetails(NbaGame requestedGame) {
        getView().showGameDetails(requestedGame);
    }

    public void updateGames(String gameData) {
        if (DateFormatUtil.isDateToday(selectedDate.getTime())) {
            getView().showGames(GameUtils.getGamesListFromJson(gameData));
        }
    }

    public void dismissSnackbar() {
        getView().dismissSnackbar();
    }

}
