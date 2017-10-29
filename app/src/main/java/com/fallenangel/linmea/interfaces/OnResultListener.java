package com.fallenangel.linmea.interfaces;

/**
 * Created by NineB on 9/12/2017.
 */

public interface OnResultListener<T> {
    String onComplete (T result);
    String onSuccess(T result);
    void onFailure(Throwable error);
}

