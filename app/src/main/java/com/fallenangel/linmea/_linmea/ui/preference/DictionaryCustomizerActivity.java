package com.fallenangel.linmea._linmea.ui.preference;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.model.DictionaryCustomizer;
import com.fallenangel.linmea._linmea.ui.dictionary.AddCustomDictionaryActivity;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.non.utils.LoadDefaultConfig;
import com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode;

import java.util.List;

import javax.inject.Inject;

import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.DICTIONARY;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.DICTIONARY_PAGE;

public class DictionaryCustomizerActivity extends SuperAppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "DCA";
    private LinearLayout mDictionaryContainer, mDictionaryNameContainer, mSortingContainer, mFavoriteContainer, mLearnedContainer, mResetFilterContainer, mDisplayTypeContainer;
    private TextView mDictionaryDescription, mDictionaryNameDescription, mSortingDescription, mFavoriteDescription, mLearnedDescription, mResetFilterDescription, mFountSizeWordCounter, mFountSizeTranslationCounter, mDisplayTypeDscription;
    private Switch mDragAndDropSwitch;
   // private CheckBox mGlobalSettingsCheckBox;
    private Toolbar mToolbar;
    private SeekBar mFountSizeWordSeekBar, mFountSizeTranslationSeekBar;

    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;
    @Inject public DictionaryCustomizer mDictionaryCustomizer;


    private Intent intent;

    private int mCheckedItemOfAlertDialog;
    private int mPickedFilterValue;

    private List<String> mDictList;

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary_customizer);
        getAppComponent().inject(this);

        intent = getIntent();
        mDictionaryCustomizer.loadMode(getDictionaryPageFromIntent());
        mDictList = mDictionaryCustomizer.getListOfDictionaries();

        implementUI();

        applyDataToUI();
    }

    private void updateUIforDisplayType(){
        switch (mDictionaryCustomizer.getDisplayType()){
            case 0:
                mFountSizeWordSeekBar.setOnSeekBarChangeListener(this);
                mFountSizeTranslationSeekBar.setOnSeekBarChangeListener(this);
                mFountSizeWordSeekBar.setEnabled(true);
                mFountSizeTranslationSeekBar.setEnabled(true);
                break;
            case 1:
                mFountSizeWordSeekBar.setEnabled(true);
                mFountSizeWordSeekBar.setOnSeekBarChangeListener(this);

                mFountSizeTranslationSeekBar.setEnabled(false);
                mFountSizeTranslationSeekBar.setOnSeekBarChangeListener(null);
                break;
            case 2:
                mFountSizeWordSeekBar.setEnabled(false);
                mFountSizeWordSeekBar.setOnSeekBarChangeListener(null);

                mFountSizeTranslationSeekBar.setEnabled(true);
                mFountSizeTranslationSeekBar.setOnSeekBarChangeListener(this);
                break;
            case 3:
                mFountSizeWordSeekBar.setOnSeekBarChangeListener(this);
                mFountSizeTranslationSeekBar.setOnSeekBarChangeListener(this);
                mFountSizeWordSeekBar.setEnabled(true);
                mFountSizeTranslationSeekBar.setEnabled(true);
                break;
        }
    }

    private void implementUI() {
        mDictionaryContainer = (LinearLayout) findViewById(R.id.dict_customizer_dict_container);
        mDictionaryNameContainer = (LinearLayout) findViewById(R.id.dict_customizer_dict_dictionary_name_container);
        mSortingContainer = (LinearLayout) findViewById(R.id.dict_customizer_rearrangement_container);
        mFavoriteContainer = (LinearLayout) findViewById(R.id.dict_customizer_favorite_status_container);
        mLearnedContainer = (LinearLayout) findViewById(R.id.dict_customizer_learned_status_container);
        mResetFilterContainer = (LinearLayout) findViewById(R.id.dict_customizer_reset_filter_container);
        mDisplayTypeContainer = (LinearLayout) findViewById(R.id.dict_customizer_display_type_container);

        mDragAndDropSwitch = (Switch) findViewById(R.id.dict_customizer_drag_and_drop_switch);
       // mGlobalSettingsCheckBox = (CheckBox) findViewById(R.id.dict_customizer_use_global_settings_checkbox);
        mFountSizeWordSeekBar = (SeekBar) findViewById(R.id.dict_customizer_word_size_seek_bar);
        mFountSizeTranslationSeekBar = (SeekBar) findViewById(R.id.dict_customizer_translation_size_seek_bar);

        mFountSizeWordCounter = (TextView) findViewById(R.id.dict_customizer_word_size_counter);
        mFountSizeTranslationCounter = (TextView) findViewById(R.id.dict_customizer_translation_size_counter);
        mDictionaryDescription = (TextView) findViewById(R.id.dict_customizer_dict_description);
        mDictionaryNameDescription = (TextView) findViewById(R.id.dict_customizer_dict_dictionary_name_description);
        mSortingDescription = (TextView) findViewById(R.id.dict_customizer_description_of_auto_rearrangement);
        mFavoriteDescription = (TextView) findViewById(R.id.dict_customizer_favorite_status_description);
        mLearnedDescription = (TextView) findViewById(R.id.dict_customizer_learned_status_description);
        mDisplayTypeDscription = (TextView) findViewById(R.id.dict_customizer_display_type_description);
        if (!mDictionaryCustomizer.getGlobalSettings()){
            mFavoriteContainer.setOnClickListener(this);
            mLearnedContainer.setOnClickListener(this);
            mDisplayTypeContainer.setOnClickListener(this);
//            mFountSizeWordSeekBar.setOnSeekBarChangeListener(this);
//            mFountSizeTranslationSeekBar.setOnSeekBarChangeListener(this);
            updateUIforDisplayType();
        } else {
            mFountSizeWordSeekBar.setEnabled(false);
            mFountSizeTranslationSeekBar.setEnabled(false);
        }
        mDictionaryContainer.setOnClickListener(this);
        mDictionaryNameContainer.setOnClickListener(this);
        mSortingContainer.setOnClickListener(this);
        mResetFilterContainer.setOnClickListener(this);

        mDragAndDropSwitch.setOnCheckedChangeListener(this);
       // mGlobalSettingsCheckBox.setOnCheckedChangeListener(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setTitle(R.string.dictionary_customizer);


    }

    private void globalSettingsChange(Boolean bool){
        if (!bool){
            mFavoriteContainer.setOnClickListener(this);
            mLearnedContainer.setOnClickListener(this);
            mDisplayTypeContainer.setOnClickListener(this);
            mFountSizeWordSeekBar.setOnSeekBarChangeListener(this);
            mFountSizeTranslationSeekBar.setOnSeekBarChangeListener(this);
            mFountSizeWordSeekBar.setEnabled(true);
            mFountSizeTranslationSeekBar.setEnabled(true);
            mDictionaryContainer.setOnClickListener(this);
            mDictionaryNameContainer.setOnClickListener(this);
            mSortingContainer.setOnClickListener(this);
            mResetFilterContainer.setOnClickListener(this);

            mDragAndDropSwitch.setOnCheckedChangeListener(this);
            //mGlobalSettingsCheckBox.setOnCheckedChangeListener(this);
        } else {
            mFavoriteContainer.setOnClickListener(null);
            mLearnedContainer.setOnClickListener(null);
            mDisplayTypeContainer.setOnClickListener(null);
            mFountSizeWordSeekBar.setOnSeekBarChangeListener(null);
            mFountSizeTranslationSeekBar.setOnSeekBarChangeListener(null);
            mFountSizeWordSeekBar.setEnabled(false);
            mFountSizeTranslationSeekBar.setEnabled(false);
            mDictionaryContainer.setOnClickListener(this);
            mDictionaryNameContainer.setOnClickListener(this);
            mSortingContainer.setOnClickListener(this);
            mResetFilterContainer.setOnClickListener(this);

            mDragAndDropSwitch.setOnCheckedChangeListener(null);
           // mGlobalSettingsCheckBox.setOnCheckedChangeListener(this);
        }


    }


    private void applyDataToUI(){
        updateUIforDisplayType();
        mDictionaryDescription.setText(getDictionaryNameFromIntent());
        mDictionaryNameDescription.setText(mDictionaryCustomizer.getDictionaryName());
        mFavoriteDescription.setText(mDictionaryCustomizer.switchHelper(getApplicationContext(), mDictionaryCustomizer.getFavorite(), R.array.hide_favorite));
        mLearnedDescription.setText(mDictionaryCustomizer.switchHelper(getApplicationContext(),mDictionaryCustomizer.getLearned(), R.array.hide_learned));
        mFountSizeWordSeekBar.setProgress(mDictionaryCustomizer.getFountSizeWord());
        mFountSizeTranslationSeekBar.setProgress(mDictionaryCustomizer.getFountSizeTranslation());
        mFountSizeWordCounter.setText(String.valueOf(mDictionaryCustomizer.getFountSizeWord()));
        mFountSizeTranslationCounter.setText(String.valueOf(mDictionaryCustomizer.getFountSizeTranslation()));
        mDisplayTypeDscription.setText(mDictionaryCustomizer.switchHelper(getApplicationContext(),mDictionaryCustomizer.getDisplayType(), R.array.display_type));
        mDragAndDropSwitch.setChecked(mDictionaryCustomizer.getDragAndDrop());
      //  mGlobalSettingsCheckBox.setChecked(mDictionaryCustomizer.getGlobalSettings());
    }

    public void updateUI(){
        updateUIforDisplayType();
        Log.i(TAG, "updateUI: " + mDictionaryCustomizer.getDictionary());
        if (mDictionaryCustomizer.getDictionary() == null){
            mDictionaryDescription.setText(getDictionaryNameFromIntent());
        } else {
            mDictionaryDescription.setText(mDictionaryCustomizer.getDictionary());
        }
        mDictionaryNameDescription.setText(mDictionaryCustomizer.getDictionaryName());
        mFavoriteDescription.setText(mDictionaryCustomizer.switchHelper(getApplicationContext(),mDictionaryCustomizer.getFavorite(), R.array.hide_favorite));
        mLearnedDescription.setText(mDictionaryCustomizer.switchHelper(getApplicationContext(),mDictionaryCustomizer.getLearned(), R.array.hide_learned));
        mDisplayTypeDscription.setText(mDictionaryCustomizer.switchHelper(getApplicationContext(),mDictionaryCustomizer.getDisplayType(), R.array.display_type));
//        mSortingDescription.setText(mDictionaryCustomizer.getSorting());
        mDragAndDropSwitch.setChecked(mDictionaryCustomizer.getDragAndDrop());
      //  mGlobalSettingsCheckBox.setChecked(mDictionaryCustomizer.getGlobalSettings());
    }


//    private void implementRecyclerViewData () {
//        String path = "custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/";
//        mFirebaseHelper = new FirebaseHelper<MyDictionaryModel>(this, path, mItems, mAdapter);
//
//        mItems = mFirebaseHelper.getItemsList(FirebaseHelper.ORDER_BY_KEY, new OnChildListener<MyDictionaryModel>() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s, int index) {
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s, int index) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot, int index) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s, int index) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//
//            @Override
//            public MyDictionaryModel getItem(DataSnapshot dataSnapshot) {
//                return mFirebaseDictionaryWrapper.getDictionaryItem(dataSnapshot);
//            }
//
//            @Override
//            public String getSortedString() {
//                return null;
//            }
//        });
//    }
                    //
                    //
                    //    private List<String> getDictionaryList(){
                    //        String path = "custom_dictionary/" + getCurrentUserUID() + "/meta_data/";
                    //        mListOfDictionaries.clear();
                    //        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    //
                    //        mDatabaseReference.child(path).addChildEventListener(new ChildEventListener() {
                    //            @Override
                    //            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //                mListOfDictionaries.add(dataSnapshot.getKey());
                    //            }
                    //
                    //            @Override
                    //            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    //
                    //            }
                    //
                    //            @Override
                    //            public void onChildRemoved(DataSnapshot dataSnapshot) {
                    //
                    //            }
                    //
                    //            @Override
                    //            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    //
                    //            }
                    //
                    //            @Override
                    //            public void onCancelled(DatabaseError databaseError) {
                    //
                    //            }
                    //        });
                    //        return mListOfDictionaries;
                    //    }
                    //
    

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case -1:
                finish();
                break;
            case 0:
                break;
            case R.id.dict_customizer_dict_container:
                mAlertDialogBuilder = new AlertDialog.Builder(this);

                for (int i = 0; i < mDictList.size(); i++) {
                    if (mDictList.contains(mDictionaryCustomizer.getDictionary())){
                        mCheckedItemOfAlertDialog = i;
                    }
                }
                String[] mStringOfDictionaries = mDictList.toArray(new String[0]);

                if (mDictList.isEmpty()){
                    LayoutInflater inflater = LayoutInflater.from(this);
                    final View text_view_layout = inflater.inflate(R.layout.text_view_alert_dialog_dictionary, null);
                    mAlertDialogBuilder.setView(text_view_layout);
                }else {
                    mAlertDialogBuilder.setTitle(R.string.dict_customizer_dict_description);
                    mAlertDialogBuilder.setSingleChoiceItems(mStringOfDictionaries, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPickedFilterValue = which;
                        }
                    });
                }
                mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDictionaryCustomizer.setDictionary(mDictList.get(mPickedFilterValue));
                        updateUI();
                    }
                });
                mAlertDialogBuilder.setNeutralButton(R.string.add_new_dictionary, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent addDictionary = new Intent(getApplicationContext(), AddCustomDictionaryActivity.class);
                        startActivity(addDictionary);
                    }
                });
                mAlertDialogBuilder.setNegativeButton(this.getString(R.string.cancel), null);
                mAlertDialog = mAlertDialogBuilder.create();
                mAlertDialog.show();
                break;

            case R.id.dict_customizer_dict_dictionary_name_container:
                final EditText dictNameET = new EditText(this);
                mAlertDialogBuilder = new AlertDialog.Builder(this);
                mAlertDialogBuilder.setTitle(R.string.enter_name_of_dictionary);
                mAlertDialogBuilder.setView(dictNameET);
                mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDictionaryCustomizer.setDictionaryName(dictNameET.getText().toString());
                        updateUI();
                    }
                });
                mAlertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
                mAlertDialogBuilder.show();
                break;
            case R.id.dict_customizer_favorite_status_container:
                mAlertDialogBuilder = new AlertDialog.Builder(this);
                mAlertDialogBuilder.setTitle(R.string.favorite_title);
                mCheckedItemOfAlertDialog = mDictionaryCustomizer.getFavorite();
                mAlertDialogBuilder.setSingleChoiceItems(R.array.hide_favorite, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPickedFilterValue = which;
                    }
                });
                mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDictionaryCustomizer.setFavorite(mPickedFilterValue);
                        updateUI();
                    }
                });
                mAlertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
                mAlertDialog = mAlertDialogBuilder.create();
                mAlertDialog.show();
                break;
            case R.id.dict_customizer_learned_status_container:
                mAlertDialogBuilder = new AlertDialog.Builder(this);
                mAlertDialogBuilder.setTitle(R.string.learned_title);

                mCheckedItemOfAlertDialog = mDictionaryCustomizer.getLearned();
                mAlertDialogBuilder.setSingleChoiceItems(R.array.hide_learned, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPickedFilterValue = which;
                    }
                });
                mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDictionaryCustomizer.setLearned(mPickedFilterValue);
                        updateUI();
                    }
                });
                mAlertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
                mAlertDialog = mAlertDialogBuilder.create();
                mAlertDialog.show();
                break;
            case R.id.dict_customizer_description_of_auto_rearrangement:
                Toast.makeText(this, "sdfmsdknfjsdfjsd", Toast.LENGTH_LONG).show();
               // mDictionaryCustomizer.alertDialogDictRearrangement();
                //                mAlertDialogBuilder = new AlertDialog.Builder(this);
                //                mAlertDialogBuilder.setTitle(R.string.sorting_elements);
                //                final String[] array = getResources().getStringArray(R.array.sorting_elements);
                //                mCheckedItemOfAlertDialog = getFavorite();
                //                mAlertDialogBuilder.setSingleChoiceItems(array, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
                //                    @Override
                //                    public void onClick(DialogInterface dialog, int which) {
                //                        mPickedFilterValue = which;
                //                    }
                //                });
                //                mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                //                    @Override
                //                    public void onClick(DialogInterface dialog, int which) {
                //                        //array.c
                //                        setSorting(mPickedFilterValue);
                //                        updateUI();
                //                    }
                //                });
                //                mAlertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
                //                mAlertDialog = mAlertDialogBuilder.create();
                //                mAlertDialog.show();
                break;
            case R.id.dict_customizer_reset_filter_container:
                mAlertDialogBuilder = new AlertDialog.Builder(this);
                mAlertDialogBuilder.setTitle(getResources().getString(R.string.reset_filter_title));
                mAlertDialogBuilder.setMessage(getResources().getString(R.string.description_reset_filter));
                mAlertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoadDefaultConfig ldc = new LoadDefaultConfig(getApplicationContext());
                        switch (getDictionaryPageFromIntent()){
                            case CUSTOM_DICTIONARY_PAGE_1:
                                ldc.loadDefaultCustomDictPageOne();
                                break;
                            case CUSTOM_DICTIONARY_PAGE_2:
                                ldc.loadDefaultCustomDictPageTwo();
                                break;
                            case MAIN_GLOBAL_SETTINGS:
                                ldc.loadDefaultCustomDictSinglePage();
                                break;
                            case CUSTOM_DICTIONARY_LIST:
                                break;
                        }
                        updateUI();
                        applyDataToUI();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.dict_settings_has_been_reset), Toast.LENGTH_SHORT).show();
                    }
                });
                mAlertDialogBuilder.setNegativeButton("No", null);
                mAlertDialogBuilder.show();
                break;
            case R.id.dict_customizer_display_type_container:
                mAlertDialogBuilder = new AlertDialog.Builder(this);
                mAlertDialogBuilder.setTitle(R.string.display_type_title);
                final String[] array = getResources().getStringArray(R.array.display_type);
                mCheckedItemOfAlertDialog = mDictionaryCustomizer.getDisplayType();
                mAlertDialogBuilder.setSingleChoiceItems(R.array.display_type, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPickedFilterValue = which;
                    }
                });
                mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDictionaryCustomizer.setDisplayType(mPickedFilterValue);
                        mDictionaryCustomizer.setFountSizeTranslation(mFountSizeTranslationSeekBar.getProgress());
                        mDictionaryCustomizer.setFountSizeWord(mFountSizeWordSeekBar.getProgress());
                        Log.i(TAG, "onClick: " + mFountSizeWordSeekBar.getProgress() + " : " + mFountSizeTranslationSeekBar.getProgress());
                        //updateUI.updateUI();

                        updateUI();
                    }
                });
                mAlertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
                mAlertDialog = mAlertDialogBuilder.create();
                mAlertDialog.show();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.dict_customizer_drag_and_drop_switch:
                mDragAndDropSwitch.setChecked(isChecked);
                mDictionaryCustomizer.setDragAndDrop(isChecked);
                break;
            case R.id.dict_customizer_use_global_settings_checkbox:
              //  mGlobalSettingsCheckBox.setChecked(isChecked);
                mDictionaryCustomizer.setGlobalSettings(isChecked);
                globalSettingsChange(isChecked);
                break;
        }

    }

                    //    public Boolean getGlobalSettings() {
                    //        mGlobalSettings = Boolean.valueOf(getFromSharedPreferences(this, mDictionaryPage, GLOBAL_SETTINGS));
                    //        return mGlobalSettings;
                    //    }
                    //
                    //    public void setGlobalSettings(Boolean globalSettings) {
                    //        putToSharedPreferences(this, mDictionaryPage, GLOBAL_SETTINGS, String.valueOf(globalSettings));
                    //        mGlobalSettings = globalSettings;
                    //    }
                    //
                    //    public String getDictionaryName() {
                    //        return mDictionary;
                    //    }
                    //
                        public String getDictionaryNameFromIntent(){
                            return getIntent().getStringExtra(DICTIONARY.name());
                        }

                        public PreferenceMode getDictionaryPageFromIntent(){
                            return (PreferenceMode) intent.getSerializableExtra(DICTIONARY_PAGE.name());
                        }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.dict_customizer_word_size_seek_bar:
                mFountSizeWordCounter.setText(String.valueOf(progress));
                break;
            case R.id.dict_customizer_translation_size_seek_bar:
                mFountSizeTranslationCounter.setText(String.valueOf(progress));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()){
            case R.id.dict_customizer_word_size_seek_bar:
                mDictionaryCustomizer.setFountSizeWord(seekBar.getProgress());
                break;
            case R.id.dict_customizer_translation_size_seek_bar:
                mDictionaryCustomizer.setFountSizeTranslation(seekBar.getProgress());
                break;
        }
    }
    //
                    //    public void setDictionaryName(String dictionary) {
                    //        putToSharedPreferences(this, mDictionaryPage, DICTIONARY, dictionary);
                    //        mDictionary = dictionary;
                    //    }
                    //
                    //    public String getDictionaryName() {
                    //        mDictionaryName = getFromSharedPreferences(this, mDictionaryPage, CUSTOM_NAME);
                    //        return mDictionaryName;
                    //    }
                    //
                    //    public void setDictionaryName(String dictionaryName) {
                    //        putToSharedPreferences(this, mDictionaryPage, CUSTOM_NAME, dictionaryName);
                    //        //String path = "custom_dictionary/" + getCurrentUserUID() + "/meta_data";
                    //        //mDatabaseReference
                    //          //      .child(path).setValue(dictionaryName);
                    //          // .updateChildren(hashMap);
                    //        mDictionaryName = dictionaryName;
                    //    }
                    //
                    //    public int getSorting() {
                    //        return mSorting;
                    //    }
                    //
                    //    public void setSorting(int sorting) {
                    //        mSorting = sorting;
                    //    }
                    //
                    //    public int getFavorite() {
                    //        mFavorite = Integer.parseInt(getFromSharedPreferences(this, mDictionaryPage, LEARNED));
                    //        return mFavorite;
                    //    }
                    //
                    //    public void setFavorite(int favorite) {
                    //        putToSharedPreferences(this, mDictionaryPage, LEARNED, String.valueOf(favorite));
                    //        mFavorite = favorite;
                    //    }
                    //
                    //    public int getLearned() {
                    //        mLearned = Integer.parseInt(getFromSharedPreferences(this, mDictionaryPage, LEARNED));
                    //        return mLearned;
                    //    }
                    //
                    //    public void setLearned(int learned) {
                    //        putToSharedPreferences(this, mDictionaryPage, LEARNED, String.valueOf(learned));
                    //        mLearned = learned;
                    //    }
                    //
                    //    public Boolean getDragAndDrop() {
                    //        mDragAndDrop = Boolean.valueOf(getFromSharedPreferences(this, mDictionaryPage, DRAG_AND_DROP));
                    //        return mDragAndDrop;
                    //    }
                    //
                    //    public void setDragAndDrop(Boolean dragAndDrop) {
                    //        putToSharedPreferences(this, mDictionaryPage, DRAG_AND_DROP , String.valueOf(dragAndDrop));
                    //        mDragAndDrop = dragAndDrop;
                    //    }
                    //
                    //    public List<String> getListOfDictionaries() {
                    //        getDictionaryList();
                    //        //Log.i(TAG, "getListOfDictionaries: " +mListOfDictionaries);
                    //        //Log.i(TAG, "getListOfDictionaries: " + mListOfDictionaries);
                    //        return mListOfDictionaries;
                    //    }
                    //
                    //    public void setListOfDictionaries(List<String> listOfDictionaries) {
                    //        mListOfDictionaries = listOfDictionaries;
                    //    }


}
