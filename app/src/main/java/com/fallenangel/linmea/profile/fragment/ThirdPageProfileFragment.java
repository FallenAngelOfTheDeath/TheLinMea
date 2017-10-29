package com.fallenangel.linmea.profile.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fallenangel.linmea.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdPageProfileFragment extends Fragment {


    public ThirdPageProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third_page_profile, container, false);
    }

}
