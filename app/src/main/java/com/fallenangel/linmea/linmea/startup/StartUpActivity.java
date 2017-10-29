package com.fallenangel.linmea.linmea.startup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.linmea.service.UpdateUserData;
import com.fallenangel.linmea.linmea.ui.MainActivity;
import com.fallenangel.linmea.linmea.user.authentication.user;
import com.fallenangel.linmea.linmea.user.utils.SharedPreferencesUtils;
import com.google.firebase.auth.FirebaseAuth;

import static com.fallenangel.linmea.linmea.utils.image.ImageUplDwnlUtils.saveCurrentFireMD5OfImageFromServer;
import static com.fallenangel.linmea.linmea.utils.info.NetworkConnection.hasConnection;

public class StartUpActivity extends AppCompatActivity {

    private String mFirstStart;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Thread mThread;
    String TAG = "StartUpAsyncTask";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                startUp(StartUpActivity.this);
//                try {
//                    Thread.sleep(30000);
//                } catch (Exception e) {
//                    Log.i(TAG, e.getMessage());
//                }
                finishAsyncTask(StartUpActivity.this, mFirstStart);

            }
        });

        mThread.setDaemon(true);
        mThread.start();



    }
    private void startUp (Context context) {
        if(user.getCurrentUser() != null){
            saveCurrentFireMD5OfImageFromServer(context, user.getCurrentUserUID(), "FireMD5");
        }
        UpdateUserData.getUserDataFromFire(StartUpActivity.this);
        context.startService(new Intent(context, UpdateUserData.class));
    }

    private void finishAsyncTask (Context context, String mFirstStart) {
        SharedPreferencesUtils.getFromSharedPreferences(context, "FIRST_START", "");
        if (mFirstStart == null){
            DefaultSettings.setDefaultSettings();
            SharedPreferencesUtils.putToSharedPreferences(context, "FIRST_START", "", "false");
            Intent firstStart = new Intent(context, FirstStartActivity.class);
            context.startActivity(firstStart);
        } else {
            if (hasConnection(context) == true){
                Intent mainActivity = new Intent(context, MainActivity.class);
                context.startActivity(mainActivity);

            } else {
                Intent mainActivity = new Intent(context, MainActivity.class);
                context.startActivity(mainActivity);
            }

        }
    }
}