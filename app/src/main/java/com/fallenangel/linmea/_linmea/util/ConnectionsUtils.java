package com.fallenangel.linmea._linmea.util;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import static android.view.View.Z;

/**
 * Created by NineB on 11/15/2017.
 */

public class ConnectionsUtils {

    public static boolean pingServer(InetAddress addr, int port, int timeout) {
        Socket socket = new Socket();
        Exception exception = null;
        try {
            socket.connect(new InetSocketAddress(addr, port), timeout);
        } catch (IOException e) {
            exception = e;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        return exception == null ? true : false;
    }

//    public boolean testPing (String url) {
//        try {
//            URL url = new URL("http://"+url);
//
//            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
//            urlc.setRequestProperty("User-Agent", "Android Application:"+Z.APP_VERSION);
//            urlc.setRequestProperty("Connection", "close");
//            urlc.setConnectTimeout(1000 * 30); // mTimeout is in seconds
//            urlc.connect();
//
//            if (urlc.getResponseCode() == 200) {
//                Log.i("tsg", "getResponseCode == 200");
//                return new Boolean(true);
//            }
//        } catch (MalformedURLException e1) {
//            e1.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}

