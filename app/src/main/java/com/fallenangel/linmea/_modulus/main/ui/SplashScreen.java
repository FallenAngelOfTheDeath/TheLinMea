package com.fallenangel.linmea._modulus.main.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fallenangel.linmea._linmea.util.SharedPreferencesUtils;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.non.utils.LoadDefaultConfig;
import com.fallenangel.linmea._modulus.non.utils.Utils;
import com.fallenangel.linmea._modulus.auth.ui.LoginActivity;
import com.fallenangel.linmea._modulus.auth.ui.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

/**
 * Created by NineB on 11/10/2017.
 */

public class SplashScreen extends SuperAppCompatActivity implements OnCompleteListener {

    private static final String TAG = "SplashScreen";
    private String mFirstStart;

    @Inject public User user;
    @Inject public Context mContext;

    private LoadDefaultConfig mLoadDefaultConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.hideKeyboard(this);
        getAppComponent().inject(this);

        mLoadDefaultConfig = new LoadDefaultConfig(mContext);

        mFirstStart = SharedPreferencesUtils.getFromSharedPreferences(mContext, "FIRST", "START");
        if (mFirstStart == null){
            mLoadDefaultConfig.resetAllSettings();
            SharedPreferencesUtils.putToSharedPreferences(mContext, "FIRST", "START", "false");
            Intent firstStart = new Intent(mContext, SignUpActivity.class);
            startActivity(firstStart);
            finish();
        } else {
            if (user.isNull()){
                Intent login = new Intent(mContext, LoginActivity.class);
                startActivity(login);
                finish();
            } else {
                Intent mainActivity = new Intent(mContext, MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        }
    }

    @Override
    public void onComplete(@NonNull Task task) {

    }
}


