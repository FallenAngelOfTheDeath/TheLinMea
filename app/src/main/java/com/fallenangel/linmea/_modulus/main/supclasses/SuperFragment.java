package com.fallenangel.linmea._modulus.main.supclasses;

import android.support.v4.app.Fragment;

import com.fallenangel.linmea.LinMea;
import com.fallenangel.linmea._modulus.main.dagger.AppComponent;


/**
 * Created by NineB on 1/19/2018.
 */

public class SuperFragment extends Fragment {


    public AppComponent getAppComponent() {
        return ((LinMea) getActivity().getApplication()).getAppComponent();
    }
}
