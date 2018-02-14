/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/28/18 7:28 PM
 */

package com.fallenangel.linmea._modulus.translator;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.main.supclasses.SuperFragment;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by NineB on 1/17/2018.
 */

public class TranslateHistory extends SuperFragment implements OnRecyclerViewClickListener, ChildEventListener {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private TranslateHistoryAdapter mAdapter;
    private TranslationWrapper translationWrapper =  new TranslationWrapper();
    private TextView mTextView;
    @Inject DatabaseReference mDatabaseReference;
    @Inject Context context;
    private String dbHistoryPath = "translator/history/" + User.getCurrentUserUID();

    private List<Translation> mItems = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        implementUI(view);
        implementRecyclerView(view);
        loadTranslationHistory();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    private void implementRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_ans);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new TranslateHistoryAdapter(mItems, this);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void implementUI(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(view.getResources().getString(R.string.translation_history));
        RelativeLayout.LayoutParams centerLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        centerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        RelativeLayout mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout);
        mTextView = new TextView(getActivity());
        mTextView.setText("NO HISTORY");
        mTextView.setTextSize(32);
        mRelativeLayout.addView(mTextView, centerLayoutParams);
    }

    private void loadTranslationHistory(){
       ChildEventListener childEventListener = mDatabaseReference.child(dbHistoryPath).addChildEventListener(this);
    }


    @Override
    public void onItemClicked(View view, int position) {

    }

    @Override
    public void onOptionsClicked(View view, int position) {
        final Translation item = mItems.get(position);

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle(getResources().getString(R.string.delete));
        adb.setMessage(getResources().getString(R.string.delete_mes));
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabaseReference.child(dbHistoryPath).child(item.getUID()).removeValue();
                mAdapter.removeItem(position);
            }
        });
        adb.setNegativeButton("No", null);
        adb.show();
    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.translator_history, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_history:
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle(getResources().getString(R.string.delete_history));
                adb.setMessage(getResources().getString(R.string.delete_mes_hist));
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabaseReference.child(dbHistoryPath).removeValue();
                        mAdapter.clear();
                        mAdapter.notifyDataSetChanged();
                        mTextView.setVisibility(View.VISIBLE);
                    }
                });
                adb.setNegativeButton("No", null);
                adb.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        mTextView.setVisibility(View.GONE);
        Translation item = translationWrapper.getTranslationHistory(dataSnapshot);
        mItems.add(item);
        mAdapter.notifyDataSetChanged();
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
}
