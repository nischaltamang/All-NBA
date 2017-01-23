package com.gmail.jorgegilcavazos.ballislife.network.API;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NbaStandingsService {

    @GET("standings/{SeasonID}.json")
    Observable<ResponseBody> fetchStandings(@Path("SeasonID") String seasonID);

}
