/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 2/1/18 11:03 PM
 */

package com.fallenangel.linmea._modulus.prferences.utils;

import android.content.Context;

import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.CLOSE_AFTER_ADD_OR_CHANGE_WORD;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.COLOR_OF_SELECTED;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.CUSTOM_NAME;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.DEFAULT_START_UP_PAGE;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.DICTIONARY;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.DISPLAY_TYPE;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.DRAG_AND_DROP;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.FAVORITE;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.FOUNT_SIZE_TRANSLATION;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.FOUNT_SIZE_WORD;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.GLOBAL_SETTINGS;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.LEARNED;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.LEARNED_COLOR;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.OPTIONS_MENU;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.SEARCH_BY;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.STRING_OF_SORTED_UID;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.TEXT_TO_SPEECH_SPEED;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.TRANSLATION_PAIR;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.UNDERLAY_CUSTOM_DICTIONARY;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.CUSTOM_DICTIONARY_LIST;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.CUSTOM_DICTIONARY_PAGE_1;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.CUSTOM_DICTIONARY_PAGE_2;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.MAIN_GLOBAL_SETTINGS;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.SINGLE_PAGE;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.TRANSLATOR;
import static com.fallenangel.linmea._modulus.prferences.utils.PreferenceUtils.putPreference;

/**
 * Created by NineB on 11/12/2017.
 */

public class LoadDefaultConfig {

    private Context mContext;
    private String mDictionary;

    public LoadDefaultConfig(Context context) {
        this.mContext = context;
    }

    public void loadDefaultSettings(PreferenceMode dictionary){
        ///putToSharedPreferences(mContext.getApplicationContext(), OxfordDictionary, DICTIONARY, "0000");
        putPreference(mContext.getApplicationContext(), dictionary, CUSTOM_NAME, "Main");
        putPreference(mContext.getApplicationContext(), dictionary, FAVORITE, 0);
        putPreference(mContext.getApplicationContext(), dictionary, LEARNED, 0);
        putPreference(mContext.getApplicationContext(), dictionary, STRING_OF_SORTED_UID, "");
        putPreference(mContext.getApplicationContext(), dictionary, GLOBAL_SETTINGS, false);
        putPreference(mContext.getApplicationContext(), dictionary, DRAG_AND_DROP, false);
        putPreference(mContext.getApplicationContext(), dictionary, FOUNT_SIZE_WORD, 18);
        putPreference(mContext.getApplicationContext(), dictionary, FOUNT_SIZE_TRANSLATION, 16);
        putPreference(mContext.getApplicationContext(), dictionary, DISPLAY_TYPE, 0);
    }

    public void loadDefaultMainSettings(){
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, OPTIONS_MENU, 0);
        putPreference(mContext.getApplicationContext(), TRANSLATOR, TRANSLATION_PAIR, "en-ru");
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_LIST, STRING_OF_SORTED_UID, "");
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, LEARNED_COLOR, 620799487);
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, COLOR_OF_SELECTED, 1349913599);//0x9934B5E4);
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, TEXT_TO_SPEECH_SPEED, 2);//0x9934B5E4);
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, DEFAULT_START_UP_PAGE, CUSTOM_DICTIONARY_PAGE_1.name());
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, SEARCH_BY, 0);//0x9934B5E4);
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, CLOSE_AFTER_ADD_OR_CHANGE_WORD, 2);//0x9934B5E4);
    }

    public void loadDefaultCustomDictPageOne(){
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, CUSTOM_NAME, "Read");
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, FAVORITE, 0);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, LEARNED, 0);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, STRING_OF_SORTED_UID, "");
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, GLOBAL_SETTINGS, false);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, DRAG_AND_DROP, true);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, FOUNT_SIZE_WORD, 18);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, FOUNT_SIZE_TRANSLATION, 16);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, DISPLAY_TYPE, 0);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, UNDERLAY_CUSTOM_DICTIONARY, 1);
    }

    public void loadDefaultCustomDictPageTwo(){
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, CUSTOM_NAME, "Learn");
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, FAVORITE, 0);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, LEARNED, 0);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, STRING_OF_SORTED_UID, "");
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, GLOBAL_SETTINGS, false);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, DRAG_AND_DROP, false);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, FOUNT_SIZE_WORD, 18);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, FOUNT_SIZE_TRANSLATION, 16);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, DISPLAY_TYPE, 1);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, UNDERLAY_CUSTOM_DICTIONARY, 2);
    }

    public void loadDefaultCustomDictSinglePage(){
        putPreference(mContext.getApplicationContext(), SINGLE_PAGE, CUSTOM_NAME, "Dictionary");
        putPreference(mContext.getApplicationContext(), SINGLE_PAGE, FAVORITE, 0);
        putPreference(mContext.getApplicationContext(), SINGLE_PAGE, LEARNED, 0);
        putPreference(mContext.getApplicationContext(), SINGLE_PAGE, STRING_OF_SORTED_UID, "");
        putPreference(mContext.getApplicationContext(), SINGLE_PAGE, GLOBAL_SETTINGS, false);
        putPreference(mContext.getApplicationContext(), SINGLE_PAGE, DRAG_AND_DROP, true);
        putPreference(mContext.getApplicationContext(), SINGLE_PAGE, FOUNT_SIZE_WORD, 18);
        putPreference(mContext.getApplicationContext(), SINGLE_PAGE, FOUNT_SIZE_TRANSLATION, 16);
        putPreference(mContext.getApplicationContext(), SINGLE_PAGE, DISPLAY_TYPE, 0);
        putPreference(mContext.getApplicationContext(), SINGLE_PAGE, UNDERLAY_CUSTOM_DICTIONARY, 1);
    }

    public void resetAllSettings(){
        loadDefaultCustomDictPageOne();
        loadDefaultCustomDictPageTwo();
        loadDefaultCustomDictSinglePage();
        loadDefaultMainSettings();

    }

    public void firstStart(){
        loadDefaultCustomDictPageOne();
        loadDefaultCustomDictPageTwo();
        loadDefaultCustomDictSinglePage();
        loadDefaultMainSettings();
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, DICTIONARY, "Sample Dictionary");
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, DICTIONARY, "Sample Dictionary");
    }

    public void createSampleCustomDictionary(){
        String path = "custom_dictionary/" + User.getCurrentUserUID() + "/dictionaries/Sample Dictionary/";
        String pathMeta = "custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/Sample Dictionary/";
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference();
        String sampleStr = "Hello world - привет мир - true - true\n" +
                "viscosity - вязкость - false - false\n" +
                "puddle - лужа - false - false\n" +
                "pavement - тротуар - false - false\n" +
                "proposed - предложенный - false - false\n" +
                "patch - заплатка - false - false\n" +
                "suspicion - подозрение - false - false\n" +
                "bother - беспокоить - false - false\n" +
                "convenient - удобный - false - false\n" +
                "advantage - преимущество - false - false\n" +
                "close down - закрыть (насовсем) - false - false\n" +
                "apartment - квартира - false - false\n" +
                "boast - хвастовство - false - false";
        String[] strar = sampleStr.split("\n");
        for (String sss:strar) {
            String[] t = sss.split(" - ");
            List<String> tmptr = new ArrayList<>();
            tmptr.add(t[1].trim());
            HashMap<String, Object> tmp = new HashMap<>();
            tmp.put("word", t[0].trim());
            tmp.put("translation", tmptr);
            tmp.put("status", Boolean.valueOf(t[2]));
            tmp.put("favorite", Boolean.valueOf(t[3]));
            String key = dbr.push().getKey();
            dbr.child(path).child(key).setValue(tmp);
        }
        HashMap<String, Object> tmpd = new HashMap<>();
        tmpd.put("editable", false);
        tmpd.put("name", "Sample Dictionary");
        tmpd.put("size", 13);
        tmpd.put("description", "This is just a sample dictionary");
        dbr.child(pathMeta).setValue(tmpd);
    }


}
