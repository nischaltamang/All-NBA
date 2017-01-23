package com.gmail.jorgegilcavazos.ballislife.network.API;

import com.gmail.jorgegilcavazos.ballislife.features.model.GameThreadSummary;
import com.gmail.jorgegilcavazos.ballislife.util.RedditUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class GameThreadFinderService {

    public static Observable<String> findGameThreadInList(final List<GameThreadSummary> threads,
                                                          final String type,
                                                          final String homeTeamAbbr,
                                                          final String awayTeamAbbr) {
        Observable<String> observable = Observable.create(
                new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        e.onNext(RedditUtils.findGameThreadId(threads, type,
                                homeTeamAbbr, awayTeamAbbr));
                    }
                }
        );

        return observable;
    }

}
