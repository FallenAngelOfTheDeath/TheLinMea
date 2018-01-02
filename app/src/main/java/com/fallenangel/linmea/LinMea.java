package com.fallenangel.linmea;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by NineB on 12/6/2017.
 */

public class LinMea extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
