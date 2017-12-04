package com.fallenangel.linmea._linmea.model;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.util.SharedPreferencesUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.fallenangel.linmea._linmea.util.SharedPreferencesUtils.getFromSharedPreferences;
import static com.fallenangel.linmea._linmea.util.SharedPreferencesUtils.getIntFromSharedPreferences;
import static com.fallenangel.linmea._linmea.util.SharedPreferencesUtils.putToSharedPreferences;
import static com.fallenangel.linmea.linmea.user.authentication.User.getCurrentUserUID;

/**
 * Created by NineB on 11/19/2017.
 */

public class DictionaryCustomizer {

    public interface UpdateUI{
        void updateUI();
    }

    private Context mContext;

    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;
    private UpdateUI updateUI;

    public static final String DICTIONARY_PAGE = "DICTIONARY_PAGE";
    public static final String DICTIONARY = "DICTIONARY";
    public static final String CUSTOM_NAME = "CUSTOM_NAME";
    public static final String STRING_OF_SORTED_UID = "STRING_OF_SORTED_UID";
    public static final String REARRANGEMENT = "REARRANGEMENT";
    public static final String FAVORITE = "FAVORITE";
    public static final String LEARNED = "LEARNED";
    public static final String GLOBAL_SETTINGS = "GLOBAL_SETTINGS";
    public static final String DRAG_AND_DROP = "DRAG_AND_DROP";
    public static final String FOUNT_SIZE_WORD = "FOUNT_SIZE_WORD";
    public static final String FOUNT_SIZE_TRANSLATION = "FOUNT_SIZE_TRANSLATION";
    public static final String DISPLAY_TYPE = "DISPLAY_TYPE";

    public static final String OPTIONS_MENU = "OPTIONS_MENU";

    public static final String CUSTOM_DICTIONARY_PAGE_1 = "CUSTOM_DICTIONARY_PAGE_1";
    public static final String CUSTOM_DICTIONARY_PAGE_2 = "CUSTOM_DICTIONARY_PAGE_2";
    //public static final String SINGLE_PAGE = "SINGLE_PAGE";
    public static final String CUSTOM_DICTIONARY_LIST = "CUSTOM_DICTIONARY_LIST";
    public static final String MAIN_GLOBAL_SETTINGS = "MAIN_GLOBAL_SETTINGS";
    public static final String TEXT_TO_SPEECH_SPEED = "TEXT_TO_SPEECH_SPEED";

    private String mDictionary, mDictionaryName, mDictionaryPage, mStringOfSortingUIDS;
    private int mSorting, mFavorite, mLearned,  mFountSizeWord, mFountSizeTranslation, mDisplayType;
    private Boolean mDragAndDrop, mGlobalSettings;
    private List<String> mListOfDictionaries;
    private int mOptionsMenu;
    private int mTTSSpeed;

    private int mCheckedItemOfAlertDialog;
    private int mPickedFilterValue;

    public DictionaryCustomizer(Context context, String dictionaryPage) {
        this.mContext = context;
        this.mDictionaryPage = dictionaryPage;
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

//    public void alertDialogDict(){
//        mAlertDialogBuilder = new AlertDialog.Builder(mContext);
//
//        for (int i = 0; i < mListOfDictionaries.size(); i++) {
//            if (mListOfDictionaries.contains(getDictionary())){
//                mCheckedItemOfAlertDialog = i;
//            }
//        }
//        String[] mStringOfDictionaries = mListOfDictionaries.toArray(new String[0]);
//
//        if (mListOfDictionaries.isEmpty()){
//            LayoutInflater inflater = LayoutInflater.from(mContext);
//            final View text_view_layout = inflater.inflate(R.layout.text_view_alert_dialog_dictionary, null);
//            mAlertDialogBuilder.setView(text_view_layout);
//        }else {
//            mAlertDialogBuilder.setTitle(R.string.dict_customizer_dict_description);
//            mAlertDialogBuilder.setSingleChoiceItems(mStringOfDictionaries, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    mPickedFilterValue = which;
//                }
//            });
//        }
//        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                setDictionary(mListOfDictionaries.get(mPickedFilterValue));
//                //updateUI.updateUI();
//            }
//        });
//        mAlertDialogBuilder.setNeutralButton(R.string.add_new_dictionary, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent addDictionary = new Intent(mContext, AddCustomDictionaryActivity.class);
//                mContext.startActivity(addDictionary);
//            }
//        });
//        mAlertDialogBuilder.setNegativeButton(mContext.getString(R.string.cancel), null);
//        mAlertDialog = mAlertDialogBuilder.create();
//        mAlertDialog.show();
//    }

    public void alertDialogDictCustomName() {
//        final EditText dictNameET = new EditText(mContext);
//        mAlertDialogBuilder = new AlertDialog.Builder(mContext);
//        mAlertDialogBuilder.setTitle(R.string.enter_name_of_dictionary);
//        mAlertDialogBuilder.setView(dictNameET);
//        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                setDictionaryName(dictNameET.getText().toString());
//                //updateUI.updateUI();
//            }
//        });
//        mAlertDialogBuilder.setNegativeButton(mContext.getString(R.string.cancel), null);
//        mAlertDialogBuilder.show();
    }

    public void alertDialogDictFavorite(){
//        mAlertDialogBuilder = new AlertDialog.Builder(mContext);
//        mAlertDialogBuilder.setTitle(R.string.favorite_title);
//        mCheckedItemOfAlertDialog = getFavorite();
//        mAlertDialogBuilder.setSingleChoiceItems(R.array.hide_favorite, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                mPickedFilterValue = which;
//            }
//        });
//        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                setFavorite(mPickedFilterValue);
//                //updateUI.updateUI();
//            }
//        });
//        mAlertDialogBuilder.setNegativeButton(mContext.getString(R.string.cancel), null);
//        mAlertDialog = mAlertDialogBuilder.create();
//        mAlertDialog.show();
    }

    public void alertDialogDictLearned(){
//        mAlertDialogBuilder = new AlertDialog.Builder(mContext);
//        mAlertDialogBuilder.setTitle(R.string.learned_title);
//
//        mCheckedItemOfAlertDialog = getLearned();
//        mAlertDialogBuilder.setSingleChoiceItems(R.array.hide_learned, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                mPickedFilterValue = which;
//            }
//        });
//        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                setLearned(mPickedFilterValue);
//                //updateUI.updateUI();
//            }
//        });
//        mAlertDialogBuilder.setNegativeButton(mContext.getString(R.string.cancel), null);
//        mAlertDialog = mAlertDialogBuilder.create();
//        mAlertDialog.show();
    }

    public void alertDialogDictRearrangement(){
        mAlertDialogBuilder = new AlertDialog.Builder(mContext);
        mAlertDialogBuilder.setTitle(R.string.sorting_elements);
       // final String[] array = mContext.getResources().getStringArray(R.array.sorting_elements);
        mCheckedItemOfAlertDialog = getFavorite();
        mAlertDialogBuilder.setSingleChoiceItems(R.array.sorting_elements, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPickedFilterValue = which;
            }
        });
        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //array.c
                setSorting(mPickedFilterValue);
                //updateUI.updateUI();
            }
        });
        mAlertDialogBuilder.setNegativeButton(mContext.getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    public void alertDialogDictDisplayType(){
//        mAlertDialogBuilder = new AlertDialog.Builder(mContext);
//        mAlertDialogBuilder.setTitle(R.string.display_type_title);
//        final String[] array = mContext.getResources().getStringArray(R.array.display_type);
//        mCheckedItemOfAlertDialog = getDisplayType();
//        mAlertDialogBuilder.setSingleChoiceItems(R.array.display_type, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                mPickedFilterValue = which;
//            }
//        });
//        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                setDisplayType(mPickedFilterValue);
//                //updateUI.updateUI();
//            }
//        });
//        mAlertDialogBuilder.setNegativeButton(mContext.getString(R.string.cancel), null);
//        mAlertDialog = mAlertDialogBuilder.create();
//        mAlertDialog.show();
    }

    public void alertDialogDictResetFilter(){
//        mAlertDialogBuilder = new AlertDialog.Builder(mContext);
//        mAlertDialogBuilder.setTitle(mContext.getResources().getString(R.string.reset_filter_title));
//        mAlertDialogBuilder.setMessage(mContext.getResources().getString(R.string.description_reset_filter));
//        mAlertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                LoadDefaultConfig ldc = new LoadDefaultConfig(mContext);
//                switch (mDictionaryPage){
//                    case CUSTOM_DICTIONARY_PAGE_1:
//                        ldc.loadDefaultCustomDictPageOne();
//                        break;
//                    case CUSTOM_DICTIONARY_PAGE_2:
//                        ldc.loadDefaultCustomDictPageTwo();
//                        break;
//                    case MAIN_GLOBAL_SETTINGS:
//                        ldc.loadDefaultCustomDictSinglePage();
//                        break;
//                    case CUSTOM_DICTIONARY_LIST:
//                        break;
//                }
//                Toast.makeText(mContext, mContext.getResources().getString(R.string.dict_settings_has_been_reset), Toast.LENGTH_SHORT);
//            }
//        });
//        mAlertDialogBuilder.setNegativeButton("No", null);
//        mAlertDialogBuilder.show();
    }

    public void alertDialogOptionsMenu(){
        mAlertDialogBuilder = new AlertDialog.Builder(mContext);
        mAlertDialogBuilder.setTitle(mContext.getResources().getString(R.string.options_menu_title));
        //mAlertDialogBuilder.setMessage(mContext.getResources().getString(R.string.description_options_menu));
        mCheckedItemOfAlertDialog = getOptionsMenu();
        mAlertDialogBuilder.setSingleChoiceItems(R.array.options_menu, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPickedFilterValue = which;
            }
        });
        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setOptionsMenu(mPickedFilterValue);
                //updateUI.updateUI();
            }
        });
        mAlertDialogBuilder.setNegativeButton(mContext.getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    public void alertDialogTTSSpeed(){
        mAlertDialogBuilder = new AlertDialog.Builder(mContext);
        mAlertDialogBuilder.setTitle(mContext.getResources().getString(R.string.speed_of_tts));
        //mAlertDialogBuilder.setMessage(mContext.getResources().getString(R.string.description_options_menu));
        mCheckedItemOfAlertDialog = getTTSSpeed(mContext);
        mAlertDialogBuilder.setSingleChoiceItems(R.array.speed_of_tts, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPickedFilterValue = which;
            }
        });
        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setTTSSpeed(mContext, mPickedFilterValue);
                //updateUI.updateUI();
            }
        });
        mAlertDialogBuilder.setNegativeButton(mContext.getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    public String switchHelper(int value, int arrayRes){
        String[] arrayStr = mContext.getResources().getStringArray(arrayRes);
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

    public Boolean getGlobalSettings() {
        mGlobalSettings = Boolean.valueOf(getFromSharedPreferences(mContext, mDictionaryPage, GLOBAL_SETTINGS));
        return mGlobalSettings;
    }

    public void setGlobalSettings(Boolean globalSettings) {
        putToSharedPreferences(mContext, mDictionaryPage, GLOBAL_SETTINGS, String.valueOf(globalSettings));
        mGlobalSettings = globalSettings;
    }

    public String getDictionary() {
        return mDictionary;
    }

    public void setDictionary(String dictionary) {
        putToSharedPreferences(mContext, mDictionaryPage, DICTIONARY, dictionary);
        mDictionary = dictionary;
    }

    public String getDictionaryName() {
        mDictionaryName = getFromSharedPreferences(mContext, mDictionaryPage, CUSTOM_NAME);
        return mDictionaryName;
    }
    public static String getDictionaryName(Context context, String dictionaryPage) {
        return getFromSharedPreferences(context, dictionaryPage, CUSTOM_NAME);
    }

    public void setDictionaryName(String dictionaryName) {
        putToSharedPreferences(mContext, mDictionaryPage, CUSTOM_NAME, dictionaryName);
        //String path = "custom_dictionary/" + getCurrentUserUID() + "/meta_data";
        //mDatabaseReference
        //      .child(path).setValue(dictionaryName);
        // .updateChildren(hashMap);
        mDictionaryName = dictionaryName;
    }

    public int getSorting() {
        return mSorting;
    }

    public void setSorting(int sorting) {
        mSorting = sorting;
    }

    public int getFavorite() {
        mFavorite = Integer.parseInt(getFromSharedPreferences(mContext, mDictionaryPage, LEARNED));
        return mFavorite;
    }

    public void setFavorite(int favorite) {
        putToSharedPreferences(mContext, mDictionaryPage, LEARNED, String.valueOf(favorite));
        mFavorite = favorite;
    }

    public int getLearned() {
        mLearned = Integer.parseInt(getFromSharedPreferences(mContext, mDictionaryPage, LEARNED));
        return mLearned;
    }

    public void setLearned(int learned) {
        putToSharedPreferences(mContext, mDictionaryPage, LEARNED, String.valueOf(learned));
        mLearned = learned;
    }

    public Boolean getDragAndDrop() {
        mDragAndDrop = Boolean.valueOf(getFromSharedPreferences(mContext, mDictionaryPage, DRAG_AND_DROP));
        return mDragAndDrop;
    }

    public void setDragAndDrop(Boolean dragAndDrop) {
        putToSharedPreferences(mContext, mDictionaryPage, DRAG_AND_DROP , String.valueOf(dragAndDrop));
        mDragAndDrop = dragAndDrop;
    }

    public List<String> getListOfDictionaries() {
        getDictionaryList();
        //Log.i(TAG, "getListOfDictionaries: " +mListOfDictionaries);
        //Log.i(TAG, "getListOfDictionaries: " + mListOfDictionaries);
        return mListOfDictionaries;
    }

    public void setListOfDictionaries(List<String> listOfDictionaries) {
        mListOfDictionaries = listOfDictionaries;
    }

    public int getFountSizeWord() {
        mFountSizeWord = Integer.parseInt(getFromSharedPreferences(mContext, mDictionaryPage, FOUNT_SIZE_WORD));
        return mFountSizeWord;
    }

    public void setFountSizeWord(int fountSizeWord) {
        putToSharedPreferences(mContext, mDictionaryPage, FOUNT_SIZE_WORD , String.valueOf(fountSizeWord));
        mFountSizeWord = fountSizeWord;
    }

    public int getFountSizeTranslation() {
        mFountSizeTranslation = Integer.parseInt(getFromSharedPreferences(mContext, mDictionaryPage, FOUNT_SIZE_TRANSLATION));
        return mFountSizeTranslation;
    }

    public void setFountSizeTranslation(int fountSizeTranslation) {
        putToSharedPreferences(mContext, mDictionaryPage, FOUNT_SIZE_TRANSLATION, String.valueOf(fountSizeTranslation));
        mFountSizeTranslation = fountSizeTranslation;
    }

    public int getDisplayType() {
        mDisplayType = getIntFromSharedPreferences(mContext, mDictionaryPage, DISPLAY_TYPE);
        return mDisplayType;
    }

    public void setDisplayType(int displayType) {
        putToSharedPreferences(mContext, mDictionaryPage, DISPLAY_TYPE , displayType);
        mDisplayType = displayType;
    }

    public int getOptionsMenu() {
        mOptionsMenu = getIntFromSharedPreferences(mContext, MAIN_GLOBAL_SETTINGS, OPTIONS_MENU);
        return mOptionsMenu;
    }

    public void setOptionsMenu(int optionsMenu) {
        putToSharedPreferences(mContext, MAIN_GLOBAL_SETTINGS, OPTIONS_MENU, optionsMenu);
        mOptionsMenu = optionsMenu;
    }

    public static int getTTSSpeed(Context context) {
        return SharedPreferencesUtils.getIntFromSharedPreferences(context, GLOBAL_SETTINGS, TEXT_TO_SPEECH_SPEED);
    }

    public void setTTSSpeed(Context context, int TTSSpeed) {
        SharedPreferencesUtils.putToSharedPreferences(context, GLOBAL_SETTINGS, TEXT_TO_SPEECH_SPEED, TTSSpeed);
        mTTSSpeed = TTSSpeed;
    }
}
