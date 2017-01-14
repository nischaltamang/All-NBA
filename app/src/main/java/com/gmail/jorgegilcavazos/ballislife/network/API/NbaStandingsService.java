package com.gmail.jorgegilcavazos.ballislife.network.API;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NbaStandingsService {

    @GET("playoffpicture?LeagueID=00")
    Observable<ResponseBody> fetchStandings(@Query("SeasonID") String seasonID);

}
