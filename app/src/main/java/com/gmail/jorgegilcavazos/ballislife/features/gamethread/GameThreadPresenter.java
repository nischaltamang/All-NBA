package com.gmail.jorgegilcavazos.ballislife.features.gamethread;

import com.gmail.jorgegilcavazos.ballislife.features.model.GameThreadSummary;
import com.gmail.jorgegilcavazos.ballislife.network.API.GameThreadFinderService;
import com.gmail.jorgegilcavazos.ballislife.network.API.RedditGameThreadsService;
import com.gmail.jorgegilcavazos.ballislife.network.API.RedditService;
import com.gmail.jorgegilcavazos.ballislife.network.RedditAuthentication;
import com.gmail.jorgegilcavazos.ballislife.util.DateFormatUtil;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import net.dean.jraw.models.CommentNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GameThreadPresenter extends MvpBasePresenter<GameThreadView> {

    private long gameDate;

    private CompositeDisposable disposables;

    public GameThreadPresenter(long gameDate) {
        disposables = new CompositeDisposable();
        this.gameDate = gameDate;
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (!retainInstance) {
            disposables.clear();
        }
    }

    public void loadComments(final String type,
                             final String homeTeamAbbr, final String awayTeamAbbr) {
        getView().setLoadingIndicator(true);
        getView().hideComments();
        getView().dismissSnackbar();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nba-app-ca681.firebaseio.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RedditGameThreadsService gameThreadsService = retrofit
                .create(RedditGameThreadsService.class);

        disposables.clear();
        disposables.add(gameThreadsService.fetchGameThreads(
                DateFormatUtil.getNoDashDateString(new Date(gameDate)))
                .flatMap(new Function<List<GameThreadSummary>, Observable<String>>() {
                    @Override
                    public Observable<String> apply(List<GameThreadSummary> threads) throws Exception {
                        return GameThreadFinderService.findGameThreadInList(threads, type,
                                homeTeamAbbr, awayTeamAbbr);
                    }
                })
                .flatMap(new Function<String, Observable<List<CommentNode>>>() {
                    @Override
                    public Observable<List<CommentNode>> apply(String threadId) throws Exception {
                        if (threadId.equals("")) {
                            List<CommentNode> list = new ArrayList<>();
                            return Observable.just(list);
                        }

                        RedditAuthentication reddit = RedditAuthentication.getInstance();
                        return new RedditService(reddit.getRedditClient())
                                .getComments(threadId, type);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<CommentNode>>() {
                    @Override
                    public void onNext(List<CommentNode> commentNodes) {
                        getView().setLoadingIndicator(false);
                        if(commentNodes.size() == 0) {
                            getView().showSnackbar(true);
                        } else {
                            getView().showComments(commentNodes);
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
        if (isViewAttached()) {
            getView().dismissSnackbar();
        }
    }
}
