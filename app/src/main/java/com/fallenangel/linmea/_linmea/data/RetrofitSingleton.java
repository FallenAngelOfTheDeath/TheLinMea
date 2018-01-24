package com.fallenangel.linmea._linmea.data;

import com.fallenangel.linmea._linmea.model.Translation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * Created by NineB on 1/20/2018.
 */

public class RetrofitSingleton {

    private static Observable<Translation> observableRetrofit;

    private static BehaviorSubject<Translation> observableModel;
    private static Subscription subscription;

    private static String textToTranslate, languagePair;

    public RetrofitSingleton(String textToTranslate, String languagePair) {
        RetrofitSingleton.textToTranslate = textToTranslate;
        RetrofitSingleton.languagePair = languagePair;
    }

    public static void init() {
        URL yandexTranslateURL = null;
        String yandexKey = "trnsl.1.1.20171204T173948Z.38b5e8c4370a447a.880aa8ef40fb744d054d8fa98af8f6ce03f5c23d";
        String yandexUrl = "https://translate.yandex.net/";
                //+ "api/v1.5/tr.json/translate?key=" + yandexKey
                //+ "&text=" + textToTranslate + "&lang=" + languagePair;
                //+ "&text=" + "Good" + "&lang=" + "en-ru" + "";
        try {
            yandexTranslateURL = new URL(yandexUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(yandexUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(rxAdapter)
                .build();

        GetRetroItem apiService = retrofit.create(GetRetroItem.class);

        observableRetrofit = apiService.getItem();
    }

    public static void resetModelsObservable() {
        observableModel = BehaviorSubject.create();

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription = observableRetrofit.subscribe(new Subscriber<Translation>() {
            @Override
            public void onCompleted() {
                //do nothing
            }

            @Override
            public void onError(Throwable e) {
                observableModel.onError(e);
            }

            @Override
            public void onNext(Translation model) {
                observableModel.onNext(model);
            }
        });
    }

    public static Observable<Translation> getModelsObservable() {
        if (observableModel == null) {
            resetModelsObservable();
        }
        return observableModel;
    }
}

