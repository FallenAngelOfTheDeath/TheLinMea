package com.fallenangel.linmea.linmea.ui.dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.linmea.ui.dictionary.fragment.DetailFragment;

public class BaseDetailActivity extends AppCompatActivity {

//    private FloatingActionButton mFabDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_detail);
//
//        mFabDetail = (FloatingActionButton) findViewById(R.id.fab_detail);
//        mFabDetail.setOnClickListener(this);


        commitFragment(new DetailFragment());
        //setMod();
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
        Log.i("TRASHTAG", "getDictionarySize: " + intent.getIntExtra("DictionarySize", 0));
        return intent.getIntExtra("DictionarySize", 0);
    }

    public static Bundle getBundle(String wordUID, String dictionary, String mod, int size){
        Bundle bundle = new Bundle();
        bundle.putString("WordUID", wordUID);
        bundle.putString("DictionaryName", dictionary);
        bundle.putString("Mod", mod);
        bundle.putInt("DictionarySize", size);
        return bundle;
    }
//
//    private void replaceFragmentWithEditMod (Fragment newFragment){
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = fm.findFragmentById(R.id.detail_activity_container);
//        if (fragment == null) {
//            fragment = newFragment;
//            fragment.setArguments(getBundle(getWordUID(), getDictionary(), "EditMod"));
//            fm.beginTransaction().replace(R.id.detail_activity_container, fragment).commit();
//        }
//    }



//    private void setMod(){
//        switch (getMod()){
//            case "DetailViewMod":
//                mFabDetail.setImageResource(R.drawable.ic_edit);
//                break;
//            case "EditMod":
//                mFabDetail.setImageResource(R.drawable.ic_done);
//                break;
//            case "AddMOD":
//                mFabDetail.setImageResource(R.drawable.ic_done);
//                break;
//        }
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (getMod()){
//            case "DetailViewMod":
//                replaceFragmentWithEditMod(new DetailFragment());
//
//                break;
//            case "EditMod":
//                changeWord();
//                onBackPressed();
//                break;
//            case "AddMOD":
//                addNewWord();
//                break;
//        }
//    }

}
