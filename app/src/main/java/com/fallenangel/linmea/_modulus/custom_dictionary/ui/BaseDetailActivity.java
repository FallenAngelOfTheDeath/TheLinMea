/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.custom_dictionary.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.custom_dictionary.model.CustomDictionaryModel;

import java.util.ArrayList;
import java.util.List;

public class BaseDetailActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Bundle bundle;
    private List<CustomDictionaryModel> mItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_detail);
        commitFragment(new DetailFragment());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setTitle(getDictionaryName());
    }

    private void commitFragment (Fragment newFragment){
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.detail_activity_container);
        if (fragment == null) {
            fragment = newFragment;
            bundle = getIntent().getBundleExtra("DictionaryExtra");
            fragment.setArguments(bundle);
            fm.beginTransaction().add(R.id.detail_activity_container, fragment).commit();
        }
    }


    private String getDictionaryName() {
        return bundle.getString("DictionaryName");
    }

}
