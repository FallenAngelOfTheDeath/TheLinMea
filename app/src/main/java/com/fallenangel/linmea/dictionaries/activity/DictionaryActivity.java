package com.fallenangel.linmea.dictionaries.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.linmea.ui.dictionary.fragment.CustomDictionaryFragment;
import com.fallenangel.linmea.firebase.AddWordCustomDictionaryActivity;

public class DictionaryActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        setDefaultScreen(CustomDictionaryFragment.class, null);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.dictionary_floating_action_button);
        mFloatingActionButton.setOnClickListener(this);
    }

    private void setDefaultScreen (Class fragmentClass,Fragment fragment){

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
        Intent intent = getIntent();
        String extra = intent.getStringExtra("DictionaryName");
        Intent addWordIntent = new Intent(this, AddWordCustomDictionaryActivity.class);
        addWordIntent.putExtra("DictionaryName", extra);
        startActivity(addWordIntent);
    }
}
