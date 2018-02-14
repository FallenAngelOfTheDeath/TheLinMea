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
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.grammar.adapter.GrammarsListAdapter;
import com.fallenangel.linmea._modulus.grammar.db.GrammarTDBHelper;
import com.fallenangel.linmea._modulus.grammar.db.OnDataListener;
import com.fallenangel.linmea._modulus.grammar.db.OnFillListListener;
import com.fallenangel.linmea._modulus.grammar.model.GrammarListWrapper;
import com.fallenangel.linmea._modulus.grammar.model.GrammarsList;
import com.fallenangel.linmea._modulus.grammar.model.UserDataGrammar;
import com.fallenangel.linmea._modulus.grammar.model.UserDataGrammarWrapper;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.non.Constant;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.non.view.GridAutofitLayoutManager;
import com.fallenangel.linmea._modulus.prferences.DictionaryCustomizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GrammarsListActivity extends SuperAppCompatActivity implements OnRecyclerViewClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "GrammarsListActivity";
    @Inject Context context;
    @Inject DictionaryCustomizer dc;
    @Inject DatabaseReference dbr;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PopupMenu mPopupMenu;
    private GrammarsListAdapter mAdapter;
    private List<GrammarsList> mItems = new ArrayList<>();
    private List<UserDataGrammar> mUserData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammars_list);
        getAppComponent().inject(this);
        implementUI();
    }

    private void loadUData(){
        GrammarTDBHelper gUDTDBHelper =
                new GrammarTDBHelper(dbr, mUserData, "/grammar/user_data/" + User.getCurrentUserUID() + "/" + getGrammarCategory());
        gUDTDBHelper.dataListener(new OnDataListener<UserDataGrammar>() {
                                      @Override
                                      public void onDataAdded(DataSnapshot dataSnapshot) {
                                          mAdapter.notifyDataSetChanged();
                                      }

                                      @Override
                                      public void onDataChanged(DataSnapshot dataSnapshot) {
                                            mAdapter.notifyDataSetChanged();
                                            mAdapter.notifyItemRangeChanged(0, mUserData.size());
                                      }

                                      @Override
                                      public UserDataGrammar itemWrapper(DataSnapshot dataSnapshot) {
                                          return new UserDataGrammarWrapper().getUserDataGrammar(dataSnapshot, getGrammarCategory());
                                      }
                                  });
    }

    private void loadGData(){
        GrammarTDBHelper<GrammarsList> gTDBHelper =
                new GrammarTDBHelper<GrammarsList>(dbr, mItems, "/grammar/data/" + getGrammarCategory(), "/grammar/categories/" + getGrammarCategory());
        gTDBHelper.fillList(new OnFillListListener<GrammarsList>() {
            @Override
            public void doOnNext(GrammarsList item) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void doOnCompleted() {
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public GrammarsList itemWrapper(DataSnapshot dataSnapshot) {
                return new GrammarListWrapper().getGrammar(dataSnapshot, getGrammarCategory());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updUI();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updUI(){
        mProgressBar.setVisibility(View.VISIBLE);
        mAdapter = new GrammarsListAdapter(this, mItems, mUserData, this, dc);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
        loadUData();
        loadGData();
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private String getGrammarCategory(){
        Intent intent = getIntent();
        return intent.getStringExtra(Constant.GRAMMAR_CATEGORY);
    }

    private void implementUI(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getGrammarCategory());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_ans);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(this, 300, true));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemClicked(View view, int position) {
        openDetailView(position);
    }

    @Override
    public void onOptionsClicked(View view, int position) {
        openPopupMenu(view, position);
    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        switch (dc.getOptionsMenu()){
            case 0:
                break;
            case 1:
                openPopupMenu(view, position);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case -1:
                finish();
                break;
        }
    }

    public void changeLearned (final int position){
        mAdapter.changeLearned(position);
    }

    public void changeFavorite (final int position){
        mAdapter.changeFavorite(position);
    }

    private void openDetailView(int position) {
        Intent intent = new Intent(this, DetailGrammarActivity.class);
        intent.putExtra("GRAMMAR_NAME", mAdapter.getItems().get(position).getGrammarName());
        intent.putExtra("GRAMMAR_CATEGORY", mAdapter.getItems().get(position).getGrammarCategory());
        startActivity(intent);
    }

    private void openPopupMenu(final View view, final int position){
        mPopupMenu = new PopupMenu(this, view);
        mPopupMenu.inflate(R.menu.popum_grammar);
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.popup_detail_view:
                        openDetailView(position);
                        break;
                    case R.id.popup_favorite:
                        changeFavorite(position);
                        break;
                    case R.id.popup_learned:
                        changeLearned(position);
                        break;
                }
                return false;
            }
        });
        mPopupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grammar_option_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<GrammarsList> searchResult = searchFilter(mItems, newText);
                mAdapter.setFilter(searchResult);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.show_hide_learned:
                hideLearn();
                break;
            case R.id.show_hide_favorite:
                hideFav();
                break;
            case R.id.reset_filter:
                updUI();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<GrammarsList> searchFilter(List<GrammarsList> items, String query) {
        query = query.toLowerCase();
        final List<GrammarsList> filteredList = new ArrayList<>();
        for (GrammarsList item : items) {
            if (item.getGrammarName().toLowerCase().contains(query)) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    private void hideFav() {
        List<GrammarsList> tmp = new ArrayList<>();
        tmp.addAll(mAdapter.getItems());
        List<UserDataGrammar> tmpd = mAdapter.getItemsData();
        Observable.from(mAdapter.getItems())
                .onBackpressureBuffer()
                .filter(new Func1<GrammarsList, Boolean>() {
                    @Override
                    public Boolean call(GrammarsList grammar) {
                        if (!mAdapter.getItemsData().isEmpty())
                            for (UserDataGrammar data:mAdapter.getItemsData()) {
                                if (grammar.getGrammarName().equals(data.getGrammarName())){
                                    Boolean tmpbool = data.getFavorite();
                                    if (tmpbool == null) return false;
                                    if (tmpbool){
                                        tmpd.remove(data);
                                        return true;
                                    }
                                }
                            }
                        return false;
                    }
                })
                .doOnNext(new Action1<GrammarsList>() {
                    @Override
                    public void call(GrammarsList grammar) {
                            tmp.remove(grammar);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        mAdapter = new GrammarsListAdapter(context, tmp, tmpd, GrammarsListActivity.this, dc);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "call: ", throwable);
                    }
                })
                .subscribe();

    }

    private void hideLearn() {
        List<GrammarsList> tmpl = new ArrayList<>();
        tmpl.addAll(mAdapter.getItems());
        List<UserDataGrammar> tmpld = mAdapter.getItemsData();
        Observable.from(mAdapter.getItems())
                .filter(new Func1<GrammarsList, Boolean>() {
                    @Override
                    public Boolean call(GrammarsList grammar) {
                        if (!mAdapter.getItemsData().isEmpty())
                            for (UserDataGrammar data:mAdapter.getItemsData()) {
                                if (grammar.getGrammarName().equals(data.getGrammarName())){
                                    Boolean tmpbool = data.getLearned();
                                    if (tmpbool == null) return false;
                                    if (tmpbool){
                                        tmpld.remove(data);
                                        return true;
                                    }
                                }
                            }
                        return false;
                    }
                })

                .doOnNext(new Action1<GrammarsList>() {
                    @Override
                    public void call(GrammarsList grammar) {
                        tmpl.remove(grammar);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        mAdapter = new GrammarsListAdapter(context, tmpl, tmpld, GrammarsListActivity.this, dc);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "OnError: ", throwable);
                    }
                })
                .subscribe();
    }

    @Override
    public void onRefresh() {
        updUI();
    }
}
