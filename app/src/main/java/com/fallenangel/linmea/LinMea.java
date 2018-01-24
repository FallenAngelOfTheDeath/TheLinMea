package com.fallenangel.linmea;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.fallenangel.linmea._modulus.main.dagger.AndroidModule;
import com.fallenangel.linmea._modulus.main.dagger.AppComponent;
import com.fallenangel.linmea._modulus.main.dagger.AppModule;
import com.fallenangel.linmea._modulus.main.dagger.DaggerAppComponent;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by NineB on 12/6/2017.
 */

public class LinMea extends Application {

    private static AppComponent appComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

        appComponent =
                DaggerAppComponent
                        .builder()
                        .androidModule(new AndroidModule(this))
                        .appModule(new AppModule())
                        .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
