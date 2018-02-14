/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.custom_dictionary.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;


public class AddCustomDictionaryActivity extends SuperAppCompatActivity implements View.OnClickListener {

    private EditText mName, mDescription;
    private Spinner mEditable;
    private FloatingActionButton mConfirm;
    private List<String> mItems = new ArrayList<>();
    @Inject DatabaseReference databaseReference;
    @Inject Context mContext;
    private ArrayAdapter<String> spinnerAdapter;
    private String[] trueFalse = {"true", "false"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_dictionary);
        getAppComponent().inject(this);
        implementAdapter();
        implementUI();
        loadData();
    }

    private void implementAdapter (){
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, trueFalse);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void implementUI (){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.add_new_dict);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(this);

        mName = (EditText) findViewById(R.id.name_custom_dictionary);
        mDescription = (EditText) findViewById(R.id.description_custom_dictionary);
        mEditable = (Spinner)findViewById(R.id.editable_spinner);
        mConfirm = (FloatingActionButton) findViewById(R.id.add_dictionary_floating_action_button);
        mConfirm.setOnClickListener(this);

        mEditable.setAdapter(spinnerAdapter);
        mEditable.setSelection(1);
    }

    private void loadData() {
        mItems.clear();
        String path = "custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/";
        databaseReference.child(path).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    mItems.add((String) dataSnapshot.getKey());
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case -1:
                finish();
                break;
            case R.id.add_dictionary_floating_action_button:
                createDictionary(User.getCurrentUserUID(), mName.getText().toString(), mEditable.getSelectedItem().toString(), mDescription.getText().toString());
                break;
        }
    }

    private void createDictionary (String user, String dictionaryName, String editable, String description){
        Map<String, Object> newDict = new HashMap<String, Object>();
        newDict.put("editable", editable);
        newDict.put("name", dictionaryName);
        if (!description.isEmpty())
            newDict.put("description", description);
        newDict.put("size", 0);
        if(!mItems.contains(dictionaryName)) {
            databaseReference.child("custom_dictionary").child(user).child("meta_data").child(dictionaryName).setValue(newDict);
            this.finish();
        } else {
            Toast.makeText(mContext, "This dictionary already exist", Toast.LENGTH_LONG).show();
        }
    }
}
