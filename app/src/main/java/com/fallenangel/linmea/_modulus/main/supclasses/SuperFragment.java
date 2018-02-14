/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.main.supclasses;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.fallenangel.linmea.LinMea;
import com.fallenangel.linmea._modulus.main.dagger.AppComponent;


/**
 * Created by NineB on 1/19/2018.
 */

public class SuperFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public AppComponent getAppComponent() {
        return ((LinMea) getActivity().getApplication()).getAppComponent();
    }
}
