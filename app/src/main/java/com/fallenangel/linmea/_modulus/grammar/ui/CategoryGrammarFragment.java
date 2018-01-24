package com.fallenangel.linmea._modulus.grammar.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.non.Constant;
import com.fallenangel.linmea._modulus.non.view.GridAutofitLayoutManager;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.grammar.adapter.GrammarCategoryAdapter;
import com.fallenangel.linmea._modulus.grammar.db.GrammarTDBHelper;
import com.fallenangel.linmea._modulus.grammar.model.GrammarCategory;
import com.fallenangel.linmea._modulus.grammar.model.GrammarCategoryWrapper;
import com.fallenangel.linmea._modulus.non.interfaces.OnSimpleChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryGrammarFragment extends Fragment implements OnRecyclerViewClickListener, OnSimpleChildEventListener {

    private RecyclerView mRecyclerView;

    private GrammarCategoryAdapter mAdapter;

    private List<GrammarCategory> mItems = new ArrayList<>();

    private GrammarTDBHelper<GrammarCategory> dbHelper;

    private GrammarCategoryWrapper wrapper = new GrammarCategoryWrapper();


    public CategoryGrammarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.recycler_view, container, false);

        implementUI(rootView);

        loadCategories();

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        dbHelper.removeChildEventListener();
    }

    private void implementUI(View view){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_ans);
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getActivity(), 300, true));

        mAdapter = new GrammarCategoryAdapter(mItems, this);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
    }


    private void loadCategories() {
        String path = "/grammar/categories/";
        dbHelper = new GrammarTDBHelper<GrammarCategory>(mItems, this);
        dbHelper.loadData(path);
        dbHelper.keepSynced(true);
    }



    @Override
    public void onItemClicked(View view, int position) {
        Intent intent = new Intent(getActivity(), GrammarsListActivity.class);
        intent.putExtra(Constant.GRAMMAR_CATEGORY, mItems.get(position).getCategory());
        startActivity(intent);
    }

    @Override
    public void onOptionsClicked(View view, int position) {

    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        return false;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public GrammarCategory getItem(DataSnapshot dataSnapshot) {
        return wrapper.getCategory(dataSnapshot);
    }

    private Set<GrammarCategory> convertListToSet(List<GrammarCategory> inputList){
        Set<GrammarCategory> set = new HashSet<>();
        set.addAll(inputList);
        return set;
    }
}
