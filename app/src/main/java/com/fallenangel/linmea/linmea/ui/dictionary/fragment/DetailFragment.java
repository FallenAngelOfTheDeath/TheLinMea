package com.fallenangel.linmea.linmea.ui.dictionary.fragment;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.interfaces.OnValueEventListener;
import com.fallenangel.linmea.linmea.database.FirebaseHelper;
import com.fallenangel.linmea.linmea.ui.dictionary.BaseDetailActivity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private EditText mWordET, mTranslationET, mDescriptionET;
    private Switch mLearnedSwitcher, mFavoriteSwitcher;
    private FloatingActionButton mFabDetail;
    private CardView mDescriptionCV;
    private Toolbar mActionBarToolbar;

    private String mod;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_word_detail, container, false);

        implementUI(view);



        setMods();

        return view;
    }


    private void implementUI(View view) {
        mActionBarToolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
        //setSupportActionBar(mActionBarToolbar);

        mWordET = (EditText) view.findViewById(R.id.word_detail_edit_text);
        mTranslationET = (EditText) view.findViewById(R.id.translation_detail_edit_text);
        mDescriptionET = (EditText) view.findViewById(R.id.description_detail_edit_text);
        mDescriptionCV = (CardView) view.findViewById(R.id.description_card_view);
        mLearnedSwitcher = (Switch) view.findViewById(R.id.learned_switcher);
        mFavoriteSwitcher = (Switch) view.findViewById(R.id.favorite_switcher);
        mFabDetail = (FloatingActionButton) view.findViewById(R.id.fab_detail);

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

        mWordET.setCursorVisible(false);
        mWordET.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_ATOP);

        mTranslationET.setCursorVisible(false);
        mTranslationET.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_ATOP);

        mDescriptionET.setCursorVisible(false);
        mDescriptionET.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_ATOP);


    //    mWordET.setBackgroundColor(getResources().getColor(R.color.backgroud_edit_text));
    //    mTranslationET.setBackgroundColor(getResources().getColor(R.color.backgroud_edit_text));
    //    mDescriptionET.setBackgroundColor(getResources().getColor(R.color.backgroud_edit_text));

        mFabDetail.setImageResource(R.drawable.ic_edit);

        mFavoriteSwitcher.setOnCheckedChangeListener(this);
        mLearnedSwitcher.setOnCheckedChangeListener(this);
    }

    private void setEditMod () {
        mod = "EditMod";
        mWordET.setEnabled(true);
        mTranslationET.setEnabled(true);
        mDescriptionET.setEnabled(true);

        mWordET.setCursorVisible(true);
        mWordET.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        mTranslationET.setCursorVisible(true);
        mTranslationET.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        mDescriptionET.setCursorVisible(true);
        mDescriptionET.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);



        mFabDetail.setImageResource(R.drawable.ic_done);

     //   mWordET.setBackgroundColor(Color.TRANSPARENT);
     //   mTranslationET.setBackgroundColor(getResources().getColor(R.color.common_google_signin_btn_text_light_default));
     //   mDescriptionET.setBackgroundColor(getResources().getColor(R.color.superLightGray));

    }

    private void setAddMod () {
        mWordET.setText("");
        mTranslationET.setText("");
        mDescriptionET.setText("");
        mDescriptionET.setText("");

        mFabDetail.setImageResource(R.drawable.ic_done);

        mFavoriteSwitcher.setChecked(false);
        mLearnedSwitcher.setChecked(false);
    }

    private void implementUIData (String uid) {
        String path = "custom_dictionary/" + user.getCurrentUserUID() + "/dictionaries/" + getDictionary();

        ArrayList<CustomDictionaryModel> item = new ArrayList<CustomDictionaryModel>();
        FirebaseHelper firebaseHelper = new FirebaseHelper(getActivity(), item, path);
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

//        Singleton singleton = Singleton.getInstance(getActivity());
//        // singleton.getItemsList(user.getCurrentUserUID(), "My First Dictionary", null);
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
        //Intent intent = getActivity().getIntent();
        //return intent.getStringExtra("WordUID");
        return getArguments().getString("WordUID");
    }
    private String getDictionary() {
//        Intent intent = getActivity().getIntent();
//        return intent.getStringExtra("DictionaryName");
        return getArguments().getString("DictionaryName");
    }

    private int getDictionarySize(){
        return getArguments().getInt("DictionarySize");
    }

    private String getMod(){
//        Intent intent = getActivity().getIntent();
//        return intent.getStringExtra("Mod");
        return getArguments().getString("Mod");
    }

    private Boolean closeAfterAdded (){
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("closeAfterAdd", true);
    }

    public void changeWord () {

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
                getActivity().onBackPressed();
            }
        } else {
            Toast.makeText(getActivity(), R.string.field_of_a_word_or_translation_is_empty, Toast.LENGTH_LONG).show();
        }


    }

                                    private void replaceFragmentWithEditMod (Fragment newFragment){
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        Fragment fragment = fm.findFragmentById(R.id.detail_activity_container);
                                        if (fragment == null) {
                                            fragment = newFragment;
                                            fragment.setArguments(BaseDetailActivity.getBundle(getWordUID(), getDictionary(), "EditMod", getDictionarySize()));
                                            fm.beginTransaction().replace(R.id.detail_activity_container, fragment).commit();
                                        }
                                    }

                                    @Override
                                    public void onClick(View v) {
                                        switch (getMod()){
                                            case "DetailViewMod":
                                                replaceFragmentWithEditMod(new DetailFragment());
                                                setEditMod();

                                                Log.i("00000000", "onClick: ");
                                                break;
                                            case "EditMod":
                                                changeWord();
                                                getActivity().onBackPressed();
                                                break;
                                            case "AddMOD":
                                                addNewWord();
                                                break;
                                        }
                                    }

//
//    @Override
//    public void onClick(View v) {
//        switch (getMod()){
//            case "DetailViewMod":
//                mod = "EditMod";
//                setEditMod();
//                mWordET.invalidate();
//                break;
//            case "EditMod":
//                changeWord();
//                getActivity().onBackPressed();
//                break;
//            case "AddMOD":
//                addNewWord();
//                break;
//        }
//    }

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
