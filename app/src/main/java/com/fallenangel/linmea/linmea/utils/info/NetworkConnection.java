package com.fallenangel.linmea.linmea.utils.info;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by NineB on 9/29/2017.
 */

public class NetworkConnection {

    public static boolean hasConnection(Context context){

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null && info.isConnected())
        {
            return true;
        }
        info = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (info != null && info.isConnected())
        {
            return true;
        }
        info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected())
        {
            return true;
        }
        return false;
    }

}
