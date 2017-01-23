package com.gmail.jorgegilcavazos.ballislife.features.standings;


import com.gmail.jorgegilcavazos.ballislife.features.model.StandingsResult;
import com.gmail.jorgegilcavazos.ballislife.features.model.TeamRecord;
import com.gmail.jorgegilcavazos.ballislife.network.API.NbaStandingsService;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class StandingsPresenter extends MvpBasePresenter<StandingsView> {

    private CompositeDisposable disposables;

    public StandingsPresenter() {
        disposables = new CompositeDisposable();
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (!retainInstance) {
            disposables.clear();
        }
    }

    public void loadStandings() {
        getView().setLoadingIndicator(true);
        getView().dismissSnackbar();
        getView().hideStandings();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nba-app-ca681.firebaseio.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        NbaStandingsService service = retrofit.create(NbaStandingsService.class);

        Observable<ResponseBody> standings = service.fetchStandings("22016");

        disposables.clear();
        disposables.add(
                standings.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        getView().setLoadingIndicator(false);

                        StandingsResult result = StandingsUtils.parseStandings(responseBody);
                        if (result.getException() == null) {
                            List<TeamRecord> eastStandings = result.getEastStandings();
                            List<TeamRecord> westStandings = result.getWestStandings();
                            getView().showStandings(eastStandings, westStandings);
                        } else {
                            getView().showSnackbar(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().setLoadingIndicator(false);
                        getView().showSnackbar(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );

    }

    public void dismissSnackbar() {
        getView().dismissSnackbar();
    }
}
