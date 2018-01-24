package com.fallenangel.linmea._modulus.non.utils;

import android.content.Context;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode;

import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.COLOR_OF_SELECTED;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.CUSTOM_NAME;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.DEFAULT_START_UP_PAGE;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.DISPLAY_TYPE;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.DRAG_AND_DROP;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.FAVORITE;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.FOUNT_SIZE_TRANSLATION;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.FOUNT_SIZE_WORD;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.GLOBAL_SETTINGS;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.LEARNED;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.LEARNED_COLOR;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.OPTIONS_MENU;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.STRING_OF_SORTED_UID;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.TEXT_TO_SPEECH_SPEED;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.TRANSLATION_PAIR;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.CUSTOM_DICTIONARY_PAGE_1;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.CUSTOM_DICTIONARY_PAGE_2;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.MAIN_GLOBAL_SETTINGS;
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
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, LEARNED_COLOR, R.color.learnedCardView);
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, COLOR_OF_SELECTED, R.color.selectedColor);//0x9934B5E4);
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, TEXT_TO_SPEECH_SPEED, 2);//0x9934B5E4);
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, DEFAULT_START_UP_PAGE, CUSTOM_DICTIONARY_PAGE_1.name());//0x9934B5E4);

    }

    public void loadDefaultCustomDictPageOne(){
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, CUSTOM_NAME, "Main");
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, FAVORITE, 0);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, LEARNED, 0);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, STRING_OF_SORTED_UID, "");
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, GLOBAL_SETTINGS, false);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, DRAG_AND_DROP, false);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, FOUNT_SIZE_WORD, 18);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, FOUNT_SIZE_TRANSLATION, 16);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_1, DISPLAY_TYPE, 0);
    }

    public void loadDefaultCustomDictPageTwo(){
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, CUSTOM_NAME, "Second");
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, FAVORITE, 1);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, LEARNED, 0);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, STRING_OF_SORTED_UID, "");
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, GLOBAL_SETTINGS, false);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, DRAG_AND_DROP, false);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, FOUNT_SIZE_WORD, 18);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, FOUNT_SIZE_TRANSLATION, 16);
        putPreference(mContext.getApplicationContext(), CUSTOM_DICTIONARY_PAGE_2, DISPLAY_TYPE, 1);
    }

    public void loadDefaultCustomDictSinglePage(){
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, CUSTOM_NAME, "Dictionary");
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, FAVORITE, 0);
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, LEARNED, 0);
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, STRING_OF_SORTED_UID, "");
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, GLOBAL_SETTINGS, false);
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, DRAG_AND_DROP, true);
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, FOUNT_SIZE_WORD, 18);
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, FOUNT_SIZE_TRANSLATION, 16);
        putPreference(mContext.getApplicationContext(), MAIN_GLOBAL_SETTINGS, DISPLAY_TYPE, 0);
    }

    public void resetAllSettings(){
        loadDefaultCustomDictPageOne();
        loadDefaultCustomDictPageTwo();
        loadDefaultCustomDictSinglePage();
        loadDefaultMainSettings();
    }
}
