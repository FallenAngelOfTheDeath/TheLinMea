package com.fallenangel.linmea._modulus.main.supclasses;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.fallenangel.linmea.LinMea;
import com.fallenangel.linmea._modulus.main.dagger.AppComponent;

/**
 * Created by NineB on 1/18/2018.
 */

public class SuperAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public AppComponent getAppComponent() {
        return ((LinMea)getApplication()).getAppComponent();
    }

}
