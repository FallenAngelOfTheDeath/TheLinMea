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
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.grammar.adapter.GrammarCategoryAdapter;
import com.fallenangel.linmea._modulus.grammar.db.GrammarTDBHelper;
import com.fallenangel.linmea._modulus.grammar.db.OnFillListListener;
import com.fallenangel.linmea._modulus.grammar.model.GrammarCategory;
import com.fallenangel.linmea._modulus.grammar.model.GrammarCategoryWrapper;
import com.fallenangel.linmea._modulus.main.supclasses.SuperFragment;
import com.fallenangel.linmea._modulus.non.Constant;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.non.view.GridAutofitLayoutManager;
import com.fallenangel.linmea._modulus.prferences.utils.NetworkUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryGrammarFragment extends SuperFragment implements OnRecyclerViewClickListener, SwipeRefreshLayout.OnRefreshListener {

    @Inject Context context;
    @Inject DatabaseReference dbr;
    private RecyclerView mRecyclerView;
    private GrammarCategoryAdapter mAdapter;
    private List<GrammarCategory> mItems = new ArrayList<>();
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextView;
    private Boolean updating = false;

    public CategoryGrammarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        implementUI(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updUI();
    }

    private void implementUI(View view){
        RelativeLayout mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout);
        RelativeLayout.LayoutParams centerLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        centerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Grammar by category");
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_ans);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getActivity(), 300, true));
        mAdapter = new GrammarCategoryAdapter(mItems, this);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
        mTextView = new TextView(getActivity());
        mTextView.setText(R.string.xd_error);
        mTextView.setVisibility(View.GONE);
        mTextView.setTextSize(24);
        mTextView.setTextColor(Color.LTGRAY);
        mRelativeLayout.addView(mTextView, centerLayoutParams);
        if (!NetworkUtils.isConnected(context)) {
            mTextView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mTextView.setTextSize(24);
            mTextView.setText(R.string.check_internet_connection);
        } else {
            mTextView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mTextView.setText(R.string.xd_error);
        }
    }

    private void updUI(){
        if (!NetworkUtils.isConnected(context)) {
            Snackbar.make(mRecyclerView, R.string.check_internet_connection, Snackbar.LENGTH_LONG).setAction(R.string.retry, v -> updUI()).show();
        } else {
            if (!updating) {
                updating = true;
                mProgressBar.setVisibility(View.VISIBLE);
                mAdapter = new GrammarCategoryAdapter(mItems, this);
                mAdapter.clear();
                mRecyclerView.setAdapter(mAdapter);
                loadCategories();
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void loadCategories() {
        if (!mItems.isEmpty()) mItems.clear();
        GrammarTDBHelper<GrammarCategory> dbHelper =
                new GrammarTDBHelper<GrammarCategory>(dbr, mItems, "/grammar/data/categories/", "/grammar/categories/");
        dbHelper.fillList(new OnFillListListener<GrammarCategory>() {
            @Override
            public void doOnNext(GrammarCategory item) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void doOnCompleted() {
                mProgressBar.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
                updating = false;
                mTextView.setVisibility(View.GONE);
            }

            @Override
            public GrammarCategory itemWrapper(DataSnapshot dataSnapshot) {
                return new GrammarCategoryWrapper().getCategory(dataSnapshot);
            }
        });
    }

    @Override
    public void onItemClicked(View view, int position) {
        Intent intent = new Intent(getActivity(), GrammarsListActivity.class);
        intent.putExtra(Constant.GRAMMAR_CATEGORY, mAdapter.getItems().get(position).getCategory());
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.only_search, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<GrammarCategory> searchResult = searchFilter(mItems, newText);
                mAdapter.setFilter(searchResult);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

        }
        return super.onOptionsItemSelected(item);
    }
    private List<GrammarCategory> searchFilter(List<GrammarCategory> items, String query) {
        query = query.toLowerCase();
        final List<GrammarCategory> filteredList = new ArrayList<>();
        for (GrammarCategory item : items) {
            if (item.getCategory().toLowerCase().contains(query)) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    @Override
    public void onRefresh() {
        updUI();
    }
}
