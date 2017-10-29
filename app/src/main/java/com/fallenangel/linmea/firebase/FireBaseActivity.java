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
import com.fallenangel.linmea.database.FirebaseWrapper;
import com.fallenangel.linmea.database.firebase.FireBaseHelper;
import com.fallenangel.linmea.interfaces.OnChildListener;
import com.fallenangel.linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea.linmea.adapter.CustomDictionaryAdapter;
import com.fallenangel.linmea.model.CustomDictionaryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FireBaseActivity extends AppCompatActivity implements OnRecyclerViewClickListener, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private CustomDictionaryAdapter mAdapter;

    private FireBaseHelper mFireBaseHelper;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();



    ArrayList<CustomDictionaryModel> mItems = new ArrayList<CustomDictionaryModel>();
    FirebaseWrapper mFirebaseWrapper = new FirebaseWrapper();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base);



            mFloatingActionButton = (FloatingActionButton) findViewById(R.id.custom_dictionary_floating_action_button);
            mRecyclerView = (RecyclerView) findViewById(R.id.custom_dictionary_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFloatingActionButton.setOnClickListener(this);

        Intent intent = getIntent();

                                mFireBaseHelper = new FireBaseHelper(databaseReference, mItems, intent.getStringExtra("DictionaryUID"));

                                if (mAdapter == null) {
                                    mAdapter = new CustomDictionaryAdapter(FireBaseActivity.this, mFireBaseHelper.getFirebaseData(new OnChildListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s, int index) {
                                        //    updateUI(mItems);
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s, int index) {
                                        //    updateUI(mItems);
                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot, int index) {
                                           // updateUI(mItems);
                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s, int index) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    }), FireBaseActivity.this, null, null);
                                    mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                                } else {
                                    mAdapter.setItems(mFireBaseHelper.getFirebaseData(null));
                                    mAdapter.notifyDataSetChanged();
                                }

    }


    @Override
    public void onItemClicked(View view, int position) {
        Toast.makeText(this,"On Item Clicked " + position, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onOptionsClicked(View view, int position) {
        Toast.makeText(this,"On Option Clicked " + position, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        Toast.makeText(this,"On Item Long Clicked " + position, Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent AddWordIntent = new Intent(this, AddWordCustomDictionaryActivity.class);
        startActivity(AddWordIntent);

    }

    public void updateUI (ArrayList<CustomDictionaryModel> mItems) {
        if (mAdapter == null) {
            mAdapter = new CustomDictionaryAdapter(FireBaseActivity.this, mItems, FireBaseActivity.this, null, null);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setItems(mItems);
            mAdapter.notifyDataSetChanged();
        }
    }
}
