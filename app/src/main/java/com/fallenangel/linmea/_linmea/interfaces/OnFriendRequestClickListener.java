package com.fallenangel.linmea._linmea.interfaces;

import android.view.View;

/**
 * Created by NineB on 12/3/2017.
 */

public interface OnFriendRequestClickListener {
    void onAccept(View view, int position);
    void onCancel(View view, int position);
}
