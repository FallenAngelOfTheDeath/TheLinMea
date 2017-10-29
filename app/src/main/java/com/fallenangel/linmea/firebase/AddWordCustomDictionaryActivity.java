package com.fallenangel.linmea.firebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.database.FirebaseWrapper;
import com.fallenangel.linmea.model.CustomDictionaryModel;
import com.fallenangel.linmea.profile.UserMetaData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.fallenangel.linmea.profile.UserMetaData.getUserUID;

public class AddWordCustomDictionaryActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mWord, mTranslation, mDescription;
    private Spinner mStatus, mFavorite;
    private FloatingActionButton mConfirm;

    private ArrayAdapter<Boolean> spinnerAdapter;
    private ArrayList<String> mTranslationList;
    private String mTranslations;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    Bundle extras;

    Boolean[] data = {true, false};


    CustomDictionaryModel mModel = new CustomDictionaryModel();
    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseWrapper mFirebaseWrapper = new FirebaseWrapper();

    String wordUID;
    String dictionaryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word_custom_dictionary);

        extras = getIntent().getExtras();

        implementAdapter();
        implementUI();


        wordUID = (String) extras.getString("WordUID");
        dictionaryName = (String) extras.getString("DictionaryName");

        if (wordUID != null) {
            implementFromIntentData();
        }
    }

    private void implementFromIntentData () {

            DatabaseReference dbr =  mDatabaseReference.child("custom_dictionary")
                    .child(UserMetaData.getUserUID())
                    .child("dictionaries")
                    .child(dictionaryName)
                    .child(wordUID);

           dbr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mWord.setText(dataSnapshot.child("word").getValue().toString());

                    Object description = dataSnapshot.child("description").getValue();
                    if (description != null){
                        mDescription.setText(description.toString());
                    }

                    mTranslationList = (ArrayList<String>) dataSnapshot.child("translation").getValue();

                    if (mTranslationList != null && !mTranslationList.isEmpty()) {
                        mTranslations = "";
                        for (int i = 0; i < mTranslationList.size() ; i++) {
                            mTranslations = mTranslations + ", " + mTranslationList.get(i);
                        }
                        mTranslations = mTranslations.replaceFirst(", ", "");
                    }

                    mTranslation.setText(mTranslations);

                    Boolean statusBool = (Boolean) dataSnapshot.child("status").getValue();
                    Boolean favoriteBool = (Boolean) dataSnapshot.child("favorite").getValue();

                     int status, favorite;
                     if (statusBool.equals(true)){
                         status = 0;
                     } else {
                         status = 1;
                     }
                     if (favoriteBool.equals(true)){
                         favorite = 0;
                     } else {
                         favorite = 1;
                     }

                    mStatus.setSelection(status);
                    mFavorite.setSelection(favorite);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }


    private void implementAdapter (){
        spinnerAdapter = new ArrayAdapter<Boolean>(this, android.R.layout.simple_spinner_item, data);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void implementUI (){
        mWord = (EditText)findViewById(R.id.word_cd_ed);
        mTranslation = (EditText)findViewById(R.id.translation_cd_ed);
        mDescription = (EditText)findViewById(R.id.description_cd_ed);

        mConfirm = (FloatingActionButton) findViewById(R.id.add_word_custom_dictionary_floating_action_button);

        mStatus = (Spinner)findViewById(R.id.status_cd_spinner);
        mFavorite = (Spinner)findViewById(R.id.fav_cd_spinner);

        mConfirm.setOnClickListener(this);

        mStatus.setAdapter(spinnerAdapter);
        mFavorite.setAdapter(spinnerAdapter);

        mStatus.setSelection(1);
        mFavorite.setSelection(1);
    }

    @Override
    public void onClick(View v) {

        if (wordUID == null){
            addNewWord();
        } else {
            changeWord();
        }

        if (closeAfterAdded() == true){
            onBackPressed();
        } else {
            Toast.makeText(this, R.string.ward_has_been_added, Toast.LENGTH_SHORT).show();
        }

    }

    private void changeWord () {

        String[] translationsList = (mTranslation.getText().toString()).split(", ");
        ArrayList<String> translationList = new ArrayList<String>(Arrays.asList(translationsList));

        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("word", mWord.getText().toString());
        hashMap.put("translation", translationList);
        hashMap.put("description", mDescription.getText().toString());
        hashMap.put("favorite", mFavorite.getSelectedItem().toString());
        hashMap.put("status", mStatus.getSelectedItem().toString());


        databaseReference
                .child("custom_dictionary")
                .child(getUserUID())
                .child("dictionaries")
                .child(getDictionaryName())
                .child(wordUID).updateChildren(hashMap);
                //.setValue(item);

    }

    private void addNewWord () {

        String[] translationsList = (mTranslation.getText().toString()).split(", ");
        ArrayList<String> translationList = new ArrayList<String>(Arrays.asList(translationsList));


//        Map<String, CustomDictionaryModel> hashMap = new HashMap<String, CustomDictionaryModel>();
//        // hashMap.put("word", mWord.getText().toString())

        CustomDictionaryModel item = new CustomDictionaryModel(
//                mWord.getText().toString(),
//                translationList,
//                mDescription.getText().toString(),
//                mFavorite.getSelectedItem().toString(),
//                mStatus.getSelectedItem().toString()
        );

        item.setWord(mWord.getText().toString());
        item.setTranslation(translationList);
        item.setDescription(mDescription.getText().toString());
        item.setFavorite((Boolean) mFavorite.getSelectedItem());
        item.setStatus((Boolean) mStatus.getSelectedItem());
        //item.setUID();


        String key = databaseReference
                .child("custom_dictionary")
                .child(getUserUID())
                .child("dictionaries")
                .child(getDictionaryName())
                .push()
                .getKey();

        databaseReference
                .child("custom_dictionary")
                .child(getUserUID())
                .child("dictionaries")
                .child(getDictionaryName())
                .child(key)
                .setValue(item);

        item.setUID(key);

    }


    private String getDictionaryName(){
        String dictionaryName;
        Intent intent = getIntent();
        dictionaryName = intent.getStringExtra("DictionaryName");
        return  dictionaryName;
    }

    private Boolean closeAfterAdded (){
        String close = "closeAfterAdd";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean closeAfterAdded = sharedPref.getBoolean(close, true);
        return closeAfterAdded;
    }

    public void saveWordToFB (){

    }

    private void putToFireBase (String user, String dictionaryName, String word, String key, String value) {
        databaseReference.child("custom_dictionary").child(user).child("dictionaries").child(dictionaryName).child(word).child(key).setValue(value);
    }

    public void putTranslation (String user, String dictionaryName, String word, String translations){
        String[] translationsList = (translations).split(", ");
        for (int i = 0; i < translationsList.length; i++) {
            String translation = translationsList[i];
            Log.i("trash", "Word [" + i + "] : " + translation);
            databaseReference.child("custom_dictionary").child(user).child("dictionaries").child(dictionaryName).push().child("Translation").child(String.valueOf(i)).setValue(translation);
        }
    }
}