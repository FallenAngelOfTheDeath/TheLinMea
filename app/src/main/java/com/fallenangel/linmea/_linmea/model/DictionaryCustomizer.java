package com.fallenangel.linmea._linmea.model;

import android.content.Context;
import android.graphics.Color;

import com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.fallenangel.linmea._modulus.auth.User.getCurrentUserUID;
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
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.TEXT_TO_SPEECH_SPEED;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.MAIN_GLOBAL_SETTINGS;
import static com.fallenangel.linmea._modulus.prferences.utils.PreferenceUtils.getBooleanPreference;
import static com.fallenangel.linmea._modulus.prferences.utils.PreferenceUtils.getIntPreference;
import static com.fallenangel.linmea._modulus.prferences.utils.PreferenceUtils.getStringPreference;
import static com.fallenangel.linmea._modulus.prferences.utils.PreferenceUtils.putPreference;

/**
 * Created by NineB on 11/19/2017.
 */

public class DictionaryCustomizer {

    private Context mContext;

//    public static final String DICTIONARY_PAGE = "DICTIONARY_PAGE";
//    public static final String DICTIONARY = "DICTIONARY";
//    public static final String CUSTOM_NAME = "CUSTOM_NAME";
//    public static final String STRING_OF_SORTED_UID = "STRING_OF_SORTED_UID";
//    public static final String REARRANGEMENT = "REARRANGEMENT";
//    public static final String FAVORITE = "FAVORITE";
//    public static final String LEARNED = "LEARNED";
//    public static final String GLOBAL_SETTINGS = "GLOBAL_SETTINGS";
//    public static final String DRAG_AND_DROP = "DRAG_AND_DROP";
//    public static final String FOUNT_SIZE_WORD = "FOUNT_SIZE_WORD";
//    public static final String FOUNT_SIZE_TRANSLATION = "FOUNT_SIZE_TRANSLATION";
//    public static final String DISPLAY_TYPE = "DISPLAY_TYPE";
//
//    public static final String OPTIONS_MENU = "OPTIONS_MENU";
//
//    public static final String CUSTOM_DICTIONARY_PAGE_1 = "CUSTOM_DICTIONARY_PAGE_1";
//    public static final String CUSTOM_DICTIONARY_PAGE_2 = "CUSTOM_DICTIONARY_PAGE_2";
//    //public static final String SINGLE_PAGE = "SINGLE_PAGE";
//    public static final String CUSTOM_DICTIONARY_LIST = "CUSTOM_DICTIONARY_LIST";
//    public static final String MAIN_GLOBAL_SETTINGS = "MAIN_GLOBAL_SETTINGS";
//    public static final String TEXT_TO_SPEECH_SPEED = "TEXT_TO_SPEECH_SPEED";
//    public static final String LEARNED_COLOR = "LEARNED_COLOR";
//    public static final String COLOR_OF_SELECTED = "COLOR_OF_SELECTED";

    private PreferenceMode mMode;
    private String mDictionary, mDictionaryName, mStringOfSortingUIDS;
    private int mSorting, mFavorite, mLearned,  mFountSizeWord, mFountSizeTranslation, mDisplayType;
    private Boolean mDragAndDrop, mGlobalSettings;
    private List<String> mListOfDictionaries;

    private int mOptionsMenu, mTTSSpeed, mLearnedColor, mColorOfSelected;
    private PreferenceMode mDefaultPage;

    public DictionaryCustomizer(Context context, PreferenceMode mode) {
        this.mContext = context;
        this.mMode = mode;
        mListOfDictionaries = new ArrayList<>();
    }

    public DictionaryCustomizer(Context context){
        this.mContext = context;
    }

    public void loadMode(PreferenceMode mode){
        this.mMode = mode;
        mListOfDictionaries = new ArrayList<>();
    }

    /*
    * Data
     */
    private List<String> getDictionaryList(){
        String path = "custom_dictionary/" + getCurrentUserUID() + "/meta_data/";
        mListOfDictionaries.clear();
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.child(path).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mListOfDictionaries.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return mListOfDictionaries;
    }

    public static String switchHelper(Context context, int value, int arrayRes){
        String[] arrayStr = context.getResources().getStringArray(arrayRes);
        switch (value){
            case 0:
                return arrayStr[0];
            case 1:
                return arrayStr[1];
            case 2:
                return arrayStr[2];
            case 3:
                return arrayStr[3];
            case 4:
                return arrayStr[4];
        }
        return null;
    }

    public PreferenceMode getDefaultPage() {
        mDefaultPage = PreferenceMode.strToEnum(getStringPreference(mContext, MAIN_GLOBAL_SETTINGS, DEFAULT_START_UP_PAGE));
        return mDefaultPage;
    }

    public void setDefaultPage(PreferenceMode mDefaultPage) {
        putPreference(mContext, MAIN_GLOBAL_SETTINGS, DEFAULT_START_UP_PAGE, mDefaultPage.name());
        this.mDefaultPage = mDefaultPage;
    }

    public Boolean getGlobalSettings() {
        mGlobalSettings = getBooleanPreference(mContext, mMode, GLOBAL_SETTINGS);
        return mGlobalSettings;
    }

    public void setGlobalSettings(Boolean globalSettings) {
        putPreference(mContext, mMode, GLOBAL_SETTINGS, globalSettings);
        mGlobalSettings = globalSettings;
    }

    public String getDictionary() {
        return mDictionary;
    }

    public void setDictionary(String dictionary) {
        putPreference(mContext, mMode, DICTIONARY, dictionary);
        mDictionary = dictionary;
    }

    public String getDictionaryName() {
        mDictionaryName = getStringPreference(mContext, mMode, CUSTOM_NAME);
        return mDictionaryName;
    }
    public static String getDictionaryName(Context context, PreferenceMode dictionaryPage) {
        return getStringPreference(context, dictionaryPage, CUSTOM_NAME);
    }

    public void setDictionaryName(String dictionaryName) {
        putPreference(mContext, mMode, CUSTOM_NAME, dictionaryName);
        mDictionaryName = dictionaryName;
    }

    public int getSorting() {
        return mSorting;
    }

    public void setSorting(int sorting) {
        mSorting = sorting;
    }

    public int getFavorite() {
        mFavorite = getIntPreference(mContext, mMode, FAVORITE);
        return mFavorite;
    }

    public void setFavorite(int favorite) {
        putPreference(mContext, mMode, FAVORITE, favorite);
        mFavorite = favorite;
    }

    public int getLearned() {
        mLearned = getIntPreference(mContext, mMode, LEARNED);
        return mLearned;
    }

    public void setLearned(int learned) {
        putPreference(mContext, mMode, LEARNED, learned);
        mLearned = learned;
    }

    public Boolean getDragAndDrop() {
        mDragAndDrop = getBooleanPreference(mContext, mMode, DRAG_AND_DROP);
        return mDragAndDrop;
    }

    public void setDragAndDrop(Boolean dragAndDrop) {
        putPreference(mContext, mMode, DRAG_AND_DROP, dragAndDrop);
        mDragAndDrop = dragAndDrop;
    }

    public List<String> getListOfDictionaries() {
        getDictionaryList();

        return mListOfDictionaries;
    }

    public void setListOfDictionaries(List<String> listOfDictionaries) {
        mListOfDictionaries = listOfDictionaries;
    }

    public int getFountSizeWord() {
        mFountSizeWord = getIntPreference(mContext, mMode, FOUNT_SIZE_WORD);
        return mFountSizeWord;
    }

    public void setFountSizeWord(int fountSizeWord) {
        putPreference(mContext, mMode, FOUNT_SIZE_WORD, fountSizeWord);
        mFountSizeWord = fountSizeWord;
    }

    public int getFountSizeTranslation() {
        mFountSizeTranslation = getIntPreference(mContext, mMode, FOUNT_SIZE_TRANSLATION);
        return mFountSizeTranslation;
    }

    public void setFountSizeTranslation(int fountSizeTranslation) {
        putPreference(mContext, mMode, FOUNT_SIZE_TRANSLATION, fountSizeTranslation);
        mFountSizeTranslation = fountSizeTranslation;
    }

    public int getDisplayType() {
        mDisplayType = getIntPreference(mContext, mMode, DISPLAY_TYPE);
        return mDisplayType;
    }

    public void setDisplayType(int displayType) {
        putPreference(mContext, mMode, DISPLAY_TYPE, displayType);
        mDisplayType = displayType;
    }

    public int getOptionsMenu() {
        mOptionsMenu = getIntPreference(mContext, MAIN_GLOBAL_SETTINGS, OPTIONS_MENU);
        return mOptionsMenu;
    }

    public void setOptionsMenu(int optionsMenu) {
        putPreference(mContext, MAIN_GLOBAL_SETTINGS, OPTIONS_MENU, optionsMenu);
        mOptionsMenu = optionsMenu;
    }

    public static int getTTSSpeed(Context context) {
        return getIntPreference(context, MAIN_GLOBAL_SETTINGS, TEXT_TO_SPEECH_SPEED);
    }

    public int getTTSSpeedNonStat() {
        mTTSSpeed = getIntPreference(mContext, MAIN_GLOBAL_SETTINGS, TEXT_TO_SPEECH_SPEED);
        return mTTSSpeed;
    }

    public void setTTSSpeed(Context context, int TTSSpeed) {
        putPreference(context, MAIN_GLOBAL_SETTINGS, TEXT_TO_SPEECH_SPEED, TTSSpeed);
        mTTSSpeed = TTSSpeed;
    }

    public int getLearnedColor() {
        mLearnedColor = getIntPreference(mContext, MAIN_GLOBAL_SETTINGS, LEARNED_COLOR);
        return mLearnedColor;
    }

    public static int getLearnedColorStatic(Context mContext) {
        return getIntPreference(mContext, MAIN_GLOBAL_SETTINGS, LEARNED_COLOR);
    }

    public void setLearnedColor(Context context, int alpha, int red, int green, int blue) {
        this.mLearnedColor = Color.argb(alpha, red, green, blue);
        putPreference(context, MAIN_GLOBAL_SETTINGS, LEARNED_COLOR, mLearnedColor);
    }

    public int getColorOfSelected() {
        mColorOfSelected = getIntPreference(mContext, MAIN_GLOBAL_SETTINGS, COLOR_OF_SELECTED);
        return mColorOfSelected;
    }

    public void setColorOfSelected(Context context, int alpha, int red, int green, int blue) {
        this.mColorOfSelected = Color.argb(alpha, red, green, blue);
        putPreference(context, MAIN_GLOBAL_SETTINGS, COLOR_OF_SELECTED, mColorOfSelected);
    }
}
