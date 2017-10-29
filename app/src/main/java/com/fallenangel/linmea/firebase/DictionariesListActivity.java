package com.fallenangel.linmea.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.adapters.MyDictionariesAdapter;
import com.fallenangel.linmea.interfaces.OnChildListener;
import com.fallenangel.linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea.model.MyDictionaryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DictionariesListActivity extends AppCompatActivity implements View.OnClickListener, OnRecyclerViewClickListener {

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private MyDictionariesAdapter mAdapter;

    private FBhelper mFireBaseHelper;

    ArrayList<MyDictionaryModel> mDictionary = new ArrayList<MyDictionaryModel>();


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dicrionaries_list);

        implementUI();
        implementFBData();
    }

    public void implementUI (){
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.custom_dictionary_floating_action_button);
        mRecyclerView = (RecyclerView) findViewById(R.id.custom_dictionary_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFloatingActionButton.setOnClickListener(this);
    }

    public void implementFBData(){
        mFireBaseHelper = new FBhelper(databaseReference, mDictionary);

        if (mAdapter == null) {
            mAdapter = new MyDictionariesAdapter(DictionariesListActivity.this, mFireBaseHelper.getFirebaseData(new OnChildListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s, int index) {
                    updateUI(mDictionary);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s, int index) {
                    updateUI(mDictionary);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot, int index) {
                    updateUI(mDictionary);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s, int index) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            }), DictionariesListActivity.this, null, null);

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

        } else {
            mAdapter.setItems(mFireBaseHelper.getFirebaseData(null));
            mAdapter.notifyDataSetChanged();
        }
    }

    public void updateUI (ArrayList<MyDictionaryModel> mItems) {
        if (mAdapter == null) {
            mAdapter = new MyDictionariesAdapter(DictionariesListActivity.this, mItems, DictionariesListActivity.this, null, null);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setItems(mItems);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClicked(View view, int position) {
        final MyDictionaryModel items = mAdapter.getItems(position);

        Intent userDictionaryIntent = new Intent(this, FireBaseActivity.class);
        userDictionaryIntent.putExtra("DictionaryUID", items.getName());
        startActivity(userDictionaryIntent);

    }

    @Override
    public void onOptionsClicked(View view, int position) {
        Toast.makeText(this,"On Option Clicked " + position, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        Toast.makeText(this,"On Item Long Clicked " + position, Toast.LENGTH_LONG).show();
        return true;
    }

}
