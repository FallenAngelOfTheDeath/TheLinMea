/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.grammar.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.grammar.model.GrammarItem;
import com.fallenangel.linmea._modulus.grammar.model.GrammarItemWrapper;
import com.fallenangel.linmea._modulus.grammar.model.UserDataGrammar;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

public class DetailGrammarActivity extends SuperAppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Inject
    Context mContext;
    @Inject
    DatabaseReference mDB;

    private Toolbar mToolbar;

    private ImageView ivFavorite;
    private CheckBox cbLearned;
    private TextView tvTitle, tvDescription, tvExamples;
    private String mCategory, mName;

    private GrammarItem item;
    private Boolean mFavorite, mLearned;
    private String examples = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_grammar);
        getAppComponent().inject(this);
        getFromIntent();
        loadGrammarData();
        implementUi();
    }

    private void implementUi() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setTitle(mName);

        ivFavorite = (ImageView) findViewById(R.id.favorite_image_view);
        cbLearned = (CheckBox) findViewById(R.id.learned_check_box);
        tvTitle = (TextView) findViewById(R.id.grammar_title);
        tvDescription = (TextView) findViewById(R.id.grammar_description);
        tvExamples = (TextView) findViewById(R.id.grammar_examples);

        ivFavorite.setOnClickListener(this);
        cbLearned.setOnCheckedChangeListener(this);
    }

    private void updateUi(Boolean mLearned, Boolean mFavorite){
        examples = "";
        tvTitle.setText(mName);
        tvDescription.setText(item.getmDescription());
        if (item.getmExamples() != null)
        for (String example:item.getmExamples()) {
            examples = examples + example + "\n\n";
            tvExamples.setText(examples.trim());
        }
        if (mLearned != null)
            cbLearned.setChecked(mLearned);
        if (mFavorite != null)
            ivFavorite.setImageDrawable(mFavorite ? mContext.getResources().getDrawable(R.drawable.ic_favorite) :  mContext.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        else
            ivFavorite.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
    }

    private void loadGrammarData(){
        mDB.child("/grammar/categories/" + mCategory + "/" + mName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                item = new GrammarItemWrapper().getGrammar(dataSnapshot);
                loadUserData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void loadUserData(){
        mDB.child("/grammar/user_data/" + User.getCurrentUserUID() + "/" + mCategory + "/" + mName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFavorite = (Boolean) dataSnapshot.child("favorite").getValue();
                mLearned = (Boolean) dataSnapshot.child("learned").getValue();
                updateUi(mLearned, mFavorite);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case -1:
                finish();
                break;
            case R.id.favorite_image_view:
                changeFavorite();
               // updateUi(mLearned, mFavorite);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        String to = User.getCurrentUserUID() + "/" + mCategory + "/" + mName;
        UserDataGrammar.changeLearned(to, b);
    }

    private void getFromIntent(){
        Intent intent = getIntent();
        mName = intent.getStringExtra("GRAMMAR_NAME");
        mCategory = intent.getStringExtra("GRAMMAR_CATEGORY");
    }

    public void changeLearned (){
        String to = User.getCurrentUserUID() + "/" + mCategory + "/" + mName;
        if (mLearned == null) {
            UserDataGrammar.changeLearned(to, true);
            mLearned = true;
        } else {
            if (mLearned == null)
                mLearned = false;
            if (mLearned){
                UserDataGrammar.changeLearned(to, false);
                mLearned = false;
            } else {
                UserDataGrammar.changeLearned(to, true);
                mLearned = true;
            }
        }
        updateUi(mLearned, mFavorite);
    }

    public void changeFavorite (){
        String to = User.getCurrentUserUID() + "/" + mCategory + "/" + mName;
        if (mFavorite == null) {
            UserDataGrammar.changeFavorite(to, true);
            mFavorite = true;
        } else {
            if (mFavorite == null)
                mFavorite = false;
            if (mFavorite){
                UserDataGrammar.changeFavorite(to, false);
                mFavorite = false;
            } else {
                UserDataGrammar.changeFavorite(to, true);
                mFavorite = true;
            }
        }
        updateUi(mLearned, mFavorite);
    }
}
