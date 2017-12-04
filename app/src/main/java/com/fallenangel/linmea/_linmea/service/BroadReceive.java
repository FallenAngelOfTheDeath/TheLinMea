package com.fallenangel.linmea._linmea.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fallenangel.linmea._test.BoundService;

public class BroadReceive extends BroadcastReceiver {

    public BroadReceive() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(context, BoundService.class));
            // intent = new Intent(context, BoundService.class);
            // context.startService(intent);
        }
    }
}
