package com.fallenangel.linmea._linmea.util;

import android.content.Context;

import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.CUSTOM_DICTIONARY_PAGE_1;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.CUSTOM_DICTIONARY_PAGE_2;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.CUSTOM_NAME;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.DISPLAY_TYPE;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.DRAG_AND_DROP;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.FAVORITE;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.FOUNT_SIZE_TRANSLATION;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.FOUNT_SIZE_WORD;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.GLOBAL_SETTINGS;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.LEARNED;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.MAIN_GLOBAL_SETTINGS;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.OPTIONS_MENU;
import static com.fallenangel.linmea._linmea.model.DictionaryCustomizer.STRING_OF_SORTED_UID;
import static com.fallenangel.linmea._linmea.util.SharedPreferencesUtils.putToSharedPreferences;

/**
 * Created by NineB on 11/12/2017.
 */

public class LoadDefaultConfig {

    private Context mContext;
    private String mDictionary;

    public LoadDefaultConfig(Context context) {
        this.mContext = context;
    }

    public void loadDefaultSettings(String dictionary){
        ///putToSharedPreferences(mContext.getApplicationContext(), dictionary, DICTIONARY, "0000");
        putToSharedPreferences(mContext.getApplicationContext(), dictionary, CUSTOM_NAME, "Main");
        putToSharedPreferences(mContext.getApplicationContext(), dictionary, FAVORITE, "0");
        putToSharedPreferences(mContext.getApplicationContext(), dictionary, LEARNED, "0");
        putToSharedPreferences(mContext.getApplicationContext(), dictionary, STRING_OF_SORTED_UID, "");
        putToSharedPreferences(mContext.getApplicationContext(), dictionary, GLOBAL_SETTINGS, "0");
        putToSharedPreferences(mContext.getApplicationContext(), dictionary, DRAG_AND_DROP, "false");
        putToSharedPreferences(mContext.getApplicationContext(), dictionary, FOUNT_SIZE_WORD, "18");
        putToSharedPreferences(mContext.getApplicationContext(), dictionary, FOUNT_SIZE_TRANSLATION, "16");
        putToSharedPreferences(mContext.getApplicationContext(), dictionary, DISPLAY_TYPE, 0);
    }

    public void loadDefaultMainSettings(){
        putToSharedPreferences(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, OPTIONS_MENU, 0);
        SharedPreferencesUtils.putToSharedPreferences(mContext.getApplicationContext(), "translator", "pair", "en-ru");
    }

    public void loadDefaultCustomDictPageOne(){
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, CUSTOM_NAME, "Main");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, FAVORITE, "0");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, LEARNED, "0");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, STRING_OF_SORTED_UID, "");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, GLOBAL_SETTINGS, "0");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, DRAG_AND_DROP, "false");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, FOUNT_SIZE_WORD, "18");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, FOUNT_SIZE_TRANSLATION, "16");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, DISPLAY_TYPE, 0);
    }

    public void loadDefaultCustomDictPageTwo(){
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, CUSTOM_NAME, "Favorite");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, FAVORITE, "1");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, LEARNED, "0");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, STRING_OF_SORTED_UID, "");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, GLOBAL_SETTINGS, "0");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, DRAG_AND_DROP, "false");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, FOUNT_SIZE_WORD, "18");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, FOUNT_SIZE_TRANSLATION, "16");
        putToSharedPreferences(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, DISPLAY_TYPE, 1);
    }

    public void loadDefaultCustomDictSinglePage(){
        putToSharedPreferences(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, CUSTOM_NAME, "Dictionary");
        putToSharedPreferences(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, FAVORITE, "0");
        putToSharedPreferences(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, LEARNED, "0");
        putToSharedPreferences(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, STRING_OF_SORTED_UID, "");
        putToSharedPreferences(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, GLOBAL_SETTINGS, "0");
        putToSharedPreferences(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, DRAG_AND_DROP, "false");
        putToSharedPreferences(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, FOUNT_SIZE_WORD, "18");
        putToSharedPreferences(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, FOUNT_SIZE_TRANSLATION, "16");
        putToSharedPreferences(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, DISPLAY_TYPE, 0);
    }
}
