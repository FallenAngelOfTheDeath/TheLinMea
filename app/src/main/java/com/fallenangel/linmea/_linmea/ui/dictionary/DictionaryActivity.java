package com.fallenangel.linmea._linmea.ui.dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.fallenangel.linmea.R;

public class DictionaryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DictionaryActivity";
    private Toolbar mToolbar;
    private CustomDictionaryFragment mCDF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        Intent intent = getIntent();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //mToolbar.setHomeAsUpIndicator(R.drawable.ic_action_goleft);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setTitle(intent.getStringExtra("DictionaryName"));
        Log.i(TAG, "onCreate: " + intent.getStringExtra("DictionaryName"));
        setDefaultScreen(CustomDictionaryFragment.class, null);
    }

    private void setDefaultScreen(Class fragmentClass, Fragment fragment) {

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
