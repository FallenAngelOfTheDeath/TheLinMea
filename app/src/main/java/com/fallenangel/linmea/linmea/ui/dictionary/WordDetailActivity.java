package com.fallenangel.linmea.linmea.ui.dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.interfaces.OnValueEventListener;
import com.fallenangel.linmea.linmea.database.FirebaseHelper;
import com.fallenangel.linmea.linmea.user.authentication.user;
import com.fallenangel.linmea.model.CustomDictionaryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WordDetailActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EditText mWordET, mTranslationET, mDescriptionET;
    private Switch mLearnedSwitcher, mFavoriteSwitcher;
    private FloatingActionButton mFabDetail;
    private CardView mDescriptionCV;
    private Toolbar mActionBarToolbar;

    private String mod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);
        implementUI();

        setMods();

    }

    private void implementUI() {
        mActionBarToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mActionBarToolbar);

        mWordET = (EditText) findViewById(R.id.word_detail_edit_text);
        mTranslationET = (EditText) findViewById(R.id.translation_detail_edit_text);
        mDescriptionET = (EditText) findViewById(R.id.description_detail_edit_text);
        mFabDetail = (FloatingActionButton) findViewById(R.id.fab_detail);
        mDescriptionCV = (CardView) findViewById(R.id.description_card_view);
        mLearnedSwitcher = (Switch) findViewById(R.id.learned_switcher);
        mFavoriteSwitcher = (Switch) findViewById(R.id.favorite_switcher);

        mFabDetail.setOnClickListener(this);
    }

    private void setMods () {
        switch (getMod()){
            case "DetailViewMod":
                mod = getMod();
                setDetailViewMod();
                implementUIData(getWordUID());
                break;
            case "EditMod":
                mod = getMod();
                setEditMod();
                implementUIData(getWordUID());
                break;
            case "AddMOD":
                mod = getMod();
                setAddMod();
                break;
        }
    }

    private void setDetailViewMod () {
        mWordET.setEnabled(false);
        mTranslationET.setEnabled(false);
        mDescriptionET.setEnabled(false);

        mWordET.setBackgroundColor(getResources().getColor(R.color.backgroud_edit_text));
        mTranslationET.setBackgroundColor(getResources().getColor(R.color.backgroud_edit_text));
        mDescriptionET.setBackgroundColor(getResources().getColor(R.color.backgroud_edit_text));

        mFabDetail.setImageResource(R.drawable.ic_edit);

        mFavoriteSwitcher.setOnCheckedChangeListener(this);
        mLearnedSwitcher.setOnCheckedChangeListener(this);
    }

    private void setEditMod () {
        mWordET.setBackgroundColor(getResources().getColor(R.color.backgroud_edit_text));
        mTranslationET.setBackgroundColor(getResources().getColor(R.color.backgroud_edit_text));
        mDescriptionET.setBackgroundColor(getResources().getColor(R.color.backgroud_edit_text));

        mFabDetail.setImageResource(R.drawable.ic_done);
    }

    private void setAddMod () {
        mFabDetail.setImageResource(R.drawable.ic_done);

        mWordET.setText("");
        mTranslationET.setText("");
        mDescriptionET.setText("");
        mDescriptionET.setText("");


        mFavoriteSwitcher.setChecked(false);
        mLearnedSwitcher.setChecked(false);
    }

    private void implementUIData (String uid) {
        String path = "custom_dictionary/" + user.getCurrentUserUID() + "/dictionaries/" + getDictionary();

        ArrayList<CustomDictionaryModel> item = new ArrayList<CustomDictionaryModel>();
        FirebaseHelper firebaseHelper = new FirebaseHelper(this, item, path);
        firebaseHelper.getItem(getWordUID(), new OnValueEventListener<CustomDictionaryModel>() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot,  CustomDictionaryModel mItem) {
                mWordET.setText(mItem.getWord());
                mTranslationET.setText(mItem.getTranslationString());
                mDescriptionET.setText(mItem.getDescription());
                if (mItem.getDescription() == null){
                    mDescriptionCV.setVisibility(View.GONE);
                } else{
                    mDescriptionET.setText(mItem.getDescription());
                }

                mFavoriteSwitcher.setChecked(mItem.getFavorite());
                mLearnedSwitcher.setChecked(mItem.getStatus());
            }

            @Override
            public void onCancelled(DatabaseError databaseError,  CustomDictionaryModel mItem) {

            }
        });


//
//
//        Singleton singleton = Singleton.getInstance(this);
//       // singleton.getItemsList(user.getCurrentUserUID(), "My First Dictionary", null);
//        singleton.getItem(user.getCurrentUserUID(), getDictionary(), uid, new OnValueEventListener<CustomDictionaryModel>() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot, CustomDictionaryModel mItem) {
//                mWordET.setText(mItem.getWord());
//                mTranslationET.setText(mItem.getTranslationString());
//                mDescriptionET.setText(mItem.getDescription());
//                if (mItem.getDescription() == null){
//                    mDescriptionCV.setVisibility(View.GONE);
//                } else{
//                    mDescriptionET.setText(mItem.getDescription());
//                }
//
//                mFavoriteSwitcher.setChecked(mItem.getFavorite());
//                mLearnedSwitcher.setChecked(mItem.getStatus());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError, CustomDictionaryModel mItem) {
//
//            }
//        });



    }




    private String getWordUID(){
        Intent intent = getIntent();
        return intent.getStringExtra("WordUID");
    }
    private String getDictionary() {
        Intent intent = getIntent();
        return intent.getStringExtra("DictionaryName");
    }

    private String getMod(){
        Intent intent = getIntent();
        return intent.getStringExtra("Mod");
    }


    private Boolean closeAfterAdded (){
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("closeAfterAdd", true);
    }

    private void changeWord () {

        String[] translationsList = (mTranslationET.getText().toString()).split(", ");
        ArrayList<String> translationList = new ArrayList<String>(Arrays.asList(translationsList));

        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("word", mWordET.getText().toString());
        hashMap.put("translation", translationList);
        if  (mDescriptionET.getText().length() != 0) {hashMap.put("description", mDescriptionET.getText().toString());}
        hashMap.put("status", mLearnedSwitcher.isChecked());
        hashMap.put("favorite", mFavoriteSwitcher.isChecked());



        FirebaseDatabase.getInstance().getReference()
                .child("custom_dictionary")
                .child(user.getCurrentUserUID())
                .child("dictionaries")
                .child(getDictionary())
                .child(getWordUID()).updateChildren(hashMap);
        //.setValue(item);

    }


    private void addNewWord () {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String[] translationsList = (mTranslationET.getText().toString()).split(", ");
        ArrayList<String> translationList = new ArrayList<String>(Arrays.asList(translationsList));

        Log.i("0000", "addNewWord: " + getDictionary());
        String key = databaseReference
                .child("custom_dictionary")
                .child(user.getCurrentUserUID())
                .child("dictionaries")
                .child(getDictionary())
                .push()
                .getKey();

        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("word", mWordET.getText().toString());
        hashMap.put("translation", translationList);
        if  (mDescriptionET.getText().length() != 0) {
            hashMap.put("description", mDescriptionET.getText().toString());
        }
        hashMap.put("status", mLearnedSwitcher.isChecked());
        hashMap.put("favorite", mFavoriteSwitcher.isChecked());



        if (mWordET.getText().length() != 0 && mTranslationET.getText().length() != 0) {
            databaseReference
                    .child("custom_dictionary")
                    .child(user.getCurrentUserUID())
                    .child("dictionaries")
                    .child(getDictionary())
                    .child(key).setValue(hashMap);
            if (closeAfterAdded() == true){
                onBackPressed();
            }
        } else {
            Toast.makeText(this, R.string.field_of_a_word_or_translation_is_empty, Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public void onClick(View v) {
        switch (getMod()){
            case "DetailViewMod":
                mod = "EditMod";
                setEditMod();
                mWordET.invalidate();
                break;
            case "EditMod":
                changeWord();
                onBackPressed();
                break;
            case "AddMOD":
                addNewWord();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        switch (buttonView.getId()){
            case R.id.favorite_switcher:
                databaseReference
                        .child("custom_dictionary")
                        .child(user.getCurrentUserUID())
                        .child("dictionaries")
                        .child(getDictionary())
                        .child(getWordUID())
                        .child("favorite")
                        .setValue(mFavoriteSwitcher.isChecked());
                break;
            case R.id.learned_switcher:
                databaseReference
                        .child("custom_dictionary")
                        .child(user.getCurrentUserUID())
                        .child("dictionaries")
                        .child(getDictionary())
                        .child(getWordUID())
                        .child("status")
                        .setValue(mLearnedSwitcher.isChecked());
                break;
        }
    }
}
