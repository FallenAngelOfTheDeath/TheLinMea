/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.main.dagger;

import android.app.NotificationManager;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by NineB on 1/18/2018.
 */

@Module
public class AndroidModule {
    Context context;

    public AndroidModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return context;
    }

    @Provides
    @Singleton
    NotificationManager providesNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
