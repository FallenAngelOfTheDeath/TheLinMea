/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.main.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.auth.ui.LoginActivity;
import com.fallenangel.linmea._modulus.auth.ui.SignUpActivity;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.non.utils.Utils;
import com.fallenangel.linmea._modulus.prferences.utils.LoadDefaultConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import static com.fallenangel.linmea._modulus.non.utils.SharedPreferencesUtils.getBollFromSharedPreferences;
import static com.fallenangel.linmea._modulus.non.utils.SharedPreferencesUtils.putToSharedPreferences;

/**
 * Created by NineB on 11/10/2017.
 */

public class SplashScreen extends SuperAppCompatActivity implements OnCompleteListener {

    private static final String TAG = "SplashScreen";
    private Boolean mFirstStart;

    @Inject public User user;
    @Inject public Context mContext;

    private LoadDefaultConfig mLoadDefaultConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.hideKeyboard(this);
        getAppComponent().inject(this);

        mLoadDefaultConfig = new LoadDefaultConfig(mContext);

        mFirstStart = getBollFromSharedPreferences(mContext, "FIRST", "START");
        if (mFirstStart == false){
            mLoadDefaultConfig.firstStart();
            putToSharedPreferences(mContext, "FIRST", "START", true);
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


