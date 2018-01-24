package com.fallenangel.linmea._linmea.data;

import com.fallenangel.linmea._linmea.model.Translation;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by NineB on 1/20/2018.
 */

public interface GetRetroItem {
    @GET("rx-retrofit-and-android-screen-orientation.php")
    Observable<Translation> getItem();
}
