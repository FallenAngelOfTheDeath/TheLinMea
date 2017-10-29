package com.fallenangel.linmea.interfaces;

/**
 * Created by NineB on 10/14/2017.
 */

public interface OnDownloadListener<T> {

        String onComplete (T result);
        String onSuccess(T result);
        void onFailure(Throwable error);
}
