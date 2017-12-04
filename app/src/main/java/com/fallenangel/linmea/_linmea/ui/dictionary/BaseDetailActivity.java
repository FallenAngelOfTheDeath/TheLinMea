package com.fallenangel.linmea._linmea.ui.dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fallenangel.linmea.R;

public class BaseDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_detail);
        commitFragment(new DetailFragment());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setTitle(getDictionary());
        setSupportActionBar(mToolbar);
    }

    private String getMod(){
        Intent intent = getIntent();
        return intent.getStringExtra("Mod");
    }

    private void commitFragment (Fragment newFragment){
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.detail_activity_container);
        if (fragment == null) {
            fragment = newFragment;
            fragment.setArguments(getBundle(getWordUID(), getDictionary(), getMod(), getDictionarySize()));
            fm.beginTransaction().add(R.id.detail_activity_container, fragment).commit();
        }
    }

    private String getWordUID(){
        Intent intent = getIntent();
        return intent.getStringExtra("WordUID");
    }
    private String getDictionary() {
        Intent intent = getIntent();
        return intent.getStringExtra("DictionaryName");
    }
    private int getDictionarySize() {
        Intent intent = getIntent();
        return intent.getIntExtra("DictionarySize", -1);
    }

    public static Bundle getBundle(String wordUID, String dictionary, String mod, int size){
        Bundle bundle = new Bundle();
        bundle.putString("WordUID", wordUID);
        bundle.putString("DictionaryName", dictionary);
        bundle.putString("Mod", mod);
        bundle.putInt("DictionarySize", size);
        return bundle;
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
