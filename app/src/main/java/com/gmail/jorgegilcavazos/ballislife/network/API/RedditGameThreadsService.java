package com.gmail.jorgegilcavazos.ballislife.network.API;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RedditGameThreadsService {

    @GET("gamethreads/{date}.json")
    Observable<ResponseBody> fetchGameThreads(@Path("date") String date);

}
