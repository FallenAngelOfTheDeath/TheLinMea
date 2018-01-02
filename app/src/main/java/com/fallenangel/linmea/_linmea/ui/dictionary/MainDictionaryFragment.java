package com.fallenangel.linmea._linmea.ui.dictionary;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.adapter.MainDictionaryAdapter;
import com.fallenangel.linmea._linmea.data.firebase.FirebaseHelperMainDict;
import com.fallenangel.linmea._linmea.interfaces.OnChildListener;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._linmea.model.MainDictionaryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainDictionaryFragment extends Fragment implements OnRecyclerViewClickListener {

    private RecyclerView mRecyclerView;
    private MainDictionaryAdapter mAdapter;

    private List<MainDictionaryModel> mItems = new ArrayList<>();

    public MainDictionaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter = new MainDictionaryAdapter(getActivity(), mItems, this, null, null);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
        loadDataToRV();
    }

    private void loadDataToRV() {
        new FirebaseHelperMainDict(getActivity(), mItems, "main_dictionary/main/").getItemsList(new OnChildListener<MainDictionaryModel>() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s, int index) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s, int index) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot, int index) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s, int index) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public MainDictionaryModel getItem(DataSnapshot dataSnapshot) {
                return null;
            }

            @Override
            public String getSortedString() {
                return null;
            }
        });
    }

    @Override
    public void onItemClicked(View view, int position) {

    }

    @Override
    public void onOptionsClicked(View view, int position) {

    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        return false;
    }
}
