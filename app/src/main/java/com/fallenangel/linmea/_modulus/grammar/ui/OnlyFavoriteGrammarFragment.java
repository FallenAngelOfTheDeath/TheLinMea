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
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.grammar.adapter.FullGramAdapter;
import com.fallenangel.linmea._modulus.grammar.db.GrammarTDBHelper;
import com.fallenangel.linmea._modulus.grammar.db.OnDataListener;
import com.fallenangel.linmea._modulus.grammar.db.OnFillListListener;
import com.fallenangel.linmea._modulus.grammar.model.FullGrammarModel;
import com.fallenangel.linmea._modulus.grammar.model.GrammarCategory;
import com.fallenangel.linmea._modulus.grammar.model.GrammarCategoryWrapper;
import com.fallenangel.linmea._modulus.grammar.model.GrammarsList;
import com.fallenangel.linmea._modulus.grammar.model.UserDataGrammar;
import com.fallenangel.linmea._modulus.grammar.model.UserDataGrammarWrapper;
import com.fallenangel.linmea._modulus.main.supclasses.SuperFragment;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.non.view.GridAutofitLayoutManager;
import com.fallenangel.linmea._modulus.prferences.DictionaryCustomizer;
import com.fallenangel.linmea._modulus.prferences.utils.NetworkUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by NineB on 1/18/2018.
 */

public class OnlyFavoriteGrammarFragment extends SuperFragment implements OnRecyclerViewClickListener, SwipeRefreshLayout.OnRefreshListener {

    @Inject Context context;
    @Inject DatabaseReference db;
    @Inject  DictionaryCustomizer mDictionaryCustomizer;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private PopupMenu mPopupMenu;
    private SearchView mSearchView;
    private TextView mTextView;
    private String TAG = "OnlyFavoriteGrammar";
    private RecyclerView mRecyclerView;
    private FullGramAdapter mAdapter;
    private List<FullGrammarModel> mItems = new ArrayList<>();
    private Boolean updating = false;
    //=============================
    private Set<Query> firebaseQueryGrammar = new HashSet<>();
    private Set<String> firebaseQueryUserData = new HashSet<>();

    private Set<UserDataGrammar> userDataSet = new HashSet<>();
    private UserDataGrammarWrapper userDataWrapper = new UserDataGrammarWrapper();

    String basePath = "/grammar/categories/";


    //////////////////////////////////////////////////////////////////
    private List<GrammarCategory> mCategory = new ArrayList<>();
    private Set<String> mGrammar = new HashSet<>();
    private Set<UserDataGrammar> mUserData = new HashSet<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getAppComponent().inject(this);
        final View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        implementUI(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updUI();
    }

    private void implementUI(View view) {
        RelativeLayout mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Favorite grammar");
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        RelativeLayout.LayoutParams centerLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        centerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        centerLayoutParams.addRule(RelativeLayout.BELOW, R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);
        mTextView = new TextView(getActivity());
        mTextView.setText(R.string.no_fav_grammar);
        mTextView.setTextSize(24);
        mTextView.setTextColor(Color.LTGRAY);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_ans);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getActivity(), 300, true));
        mAdapter = new FullGramAdapter(context, mItems, this, mDictionaryCustomizer);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
        mRelativeLayout.addView(mTextView, centerLayoutParams);

        if (!NetworkUtils.isConnected(context)) {
            mTextView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mTextView.setTextSize(24);
            mTextView.setText(R.string.check_internet_connection);
        } else {
            mTextView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mTextView.setText(R.string.no_fav_grammar);
        }
    }

    private void loadCategories() {
        if (!mCategory.isEmpty()) mCategory.clear();
        GrammarTDBHelper<GrammarCategory> dbHelper =
                new GrammarTDBHelper<GrammarCategory>(db, mCategory, "/grammar/data/categories/", "/grammar/categories/");
        dbHelper.fillList(new OnFillListListener<GrammarCategory>() {
            @Override
            public void doOnNext(GrammarCategory item) {

            }

            @Override
            public void doOnCompleted() {
                rxFirebaseQuery();
            }

            @Override
            public GrammarCategory itemWrapper(DataSnapshot dataSnapshot) {
                return new GrammarCategoryWrapper().getCategory(dataSnapshot);
            }
        });
    }


        private void rxFirebaseQuery(){
            final AtomicInteger[] stopCounter = {new AtomicInteger(0)};
              Observable.from(mCategory)
                      .doOnNext(new Action1<GrammarCategory>() {
                          @Override
                          public void call(GrammarCategory grammarCategory) {
                              userDataQuery(grammarCategory);
                              grammarQuery(stopCounter, grammarCategory);
                          }
                      })
                      .subscribe();
        }

        private void userDataQuery(GrammarCategory grammarCategory){
            GrammarTDBHelper<UserDataGrammar> dbHelper =
                    new GrammarTDBHelper<UserDataGrammar>(db, mUserData, "/grammar/user_data/" + User.getCurrentUserUID() + "/" + grammarCategory.getCategory());
            dbHelper.dataListenerForSet(new OnDataListener<UserDataGrammar>() {
                @Override
                public void onDataAdded(DataSnapshot dataSnapshot) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onDataChanged(DataSnapshot dataSnapshot) {

                }

                @Override
                public UserDataGrammar itemWrapper(DataSnapshot dataSnapshot) {
                    return new UserDataGrammarWrapper().getUserDataGrammar(dataSnapshot, grammarCategory.getCategory());
                }
            });
        }

        private void grammarQuery(AtomicInteger[] stopCounter, GrammarCategory grammarCategory){
            GrammarTDBHelper<GrammarsList> dbHelper =
                    new GrammarTDBHelper<GrammarsList>();
            dbHelper.sizeLoader(db, "/grammar/data/" + grammarCategory.getCategory())
                    .doOnNext(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            dbHelper.loader(db, "/grammar/categories/" + grammarCategory.getCategory(), integer, true)
                                    .doOnNext(new Action1<DataSnapshot>() {
                                        @Override
                                        public void call(DataSnapshot dataSnapshot) {
                                            mGrammar.add(dataSnapshot.getKey());
                                        }
                                    })
                                    .doOnCompleted(new Action0() {
                                        @Override
                                        public void call() {
                                            stopCounter[0].incrementAndGet();
                                            if(stopCounter[0].get() == 4){
                                                mergeData();
                                            }
                                        }
                                    })
                                    .subscribe();
                        }
                    }).subscribe();
        }

    private void mergeData(){
            Observable.from(mGrammar)
                    .doOnNext(new Action1<String>() {
                        @Override
                        public void call(String grammar) {
                            Log.i(TAG, "call: " +grammar);
                            FullGrammarModel gram = new FullGrammarModel();
                            for (UserDataGrammar userData:mUserData) {
                                if (grammar.equals(userData.getGrammarName())){
//                    Log.i(TAG, "mergerData1: " + userData.getGrammarName() + " : " + userData.getGrammarCategory() + " : " + userData.getCategory() + " : " + userData.getFavorite() + " : " + userData.getLearned());
                                    gram.setFavorite(userData.getFavorite());
                                    gram.setCategory(userData.getCategory());
                                    gram.setGrammar(grammar);
                                    gram.setLearned(userData.getLearned());
                                    if (gram.getFavorite() != null)
                                        if (gram.getFavorite()) {
                                            mTextView.setVisibility(View.GONE);
                                            mItems.add(gram);
                                        }
                                }
                            }
                        }
                    })
                    .doOnCompleted(() -> {
                updating = false;
                if (mItems.isEmpty()){
                    mTextView.setVisibility(View.VISIBLE);
                    mTextView.setText(R.string.no_fav_grammar);
                }
                mProgressBar.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }).subscribe();
        }

    private Observable<DataSnapshot> firebaseRxLoader(){
        final PublishSubject<DataSnapshot> subject = PublishSubject.create();
        db.child(basePath).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                subject.onNext(dataSnapshot);
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
        return subject;
    }

    private void loadQueryRx(){
        firebaseRxLoader()
                .doOnNext(new Action1<DataSnapshot>() {
                    @Override
                    public void call(DataSnapshot dataSnapshot) {
                        firebaseQueryGrammar.add(db.child(basePath + dataSnapshot.getKey()));
                        firebaseQueryUserData.add(dataSnapshot.getKey());
                        loadGrammar();
                        loadUserData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }



    private void updUI(){
        if (!NetworkUtils.isConnected(context)) {
            Snackbar.make(mRecyclerView, R.string.check_internet_connection, Snackbar.LENGTH_LONG).setAction(R.string.retry, v -> updUI()).show();
        } else {
            if (!updating) {
                updating = true;
                if (!mAdapter.getItems().isEmpty()) {
                    mUserData.clear();
                    mGrammar.clear();
                    mItems.clear();
                }
                mProgressBar.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.GONE);
                mAdapter = new FullGramAdapter(context, mItems, this, mDictionaryCustomizer);
                mAdapter.clear();
                mRecyclerView.setAdapter(mAdapter);
                loadCategories();
                mAdapter.notifyDataSetChanged();
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private Set<String> grammarStrSet = new HashSet<>();
    private void loadGrammar(){
        Observable.from(firebaseQueryGrammar)
                .doOnNext(query -> query
                        .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        grammarStrSet.add(dataSnapshot.getKey());
                        mergerData(grammarStrSet, userDataSet);
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
                }))
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private void loadUserData(){
        rx.Observable.from(firebaseQueryUserData)
                .doOnNext(new Action1<String>() {

                    @Override
                    public void call(String query) {
                        db.child("/grammar/user_data/" + User.getCurrentUserUID() + "/" +

                                query).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                UserDataGrammar item = userDataWrapper.getUserDataGrammar(dataSnapshot, query);
                                userDataSet.add(item);
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
                })
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }



    private void mergerData(Set<String> grammarSet, Set<UserDataGrammar> userDataSet){
        Set<FullGrammarModel> tmp = new HashSet<>();
        for (String grammar:grammarSet) {
            FullGrammarModel gram = new FullGrammarModel();
            //gram.setCategory(grammar);
            gram.setGrammar(grammar);
//            Log.i(TAG, "mergerData0: " + grammar);
            for (UserDataGrammar userData:userDataSet) {
                if (grammar.equals(userData.getGrammarName())){
//                    Log.i(TAG, "mergerData1: " + userData.getGrammarName() + " : " + userData.getGrammarCategory() + " : " + userData.getCategory() + " : " + userData.getFavorite() + " : " + userData.getLearned());
                    gram.setFavorite(userData.getFavorite());
                    gram.setCategory(userData.getCategory());
                    gram.setLearned(userData.getLearned());
                    if (gram.getFavorite() != null)
                    if (gram.getFavorite()) {
                        tmp.add(gram);
                        mTextView.setVisibility(View.GONE);
                    }
                }
            }

        }
        if (!mItems.isEmpty()) mItems.clear();
        mItems.addAll(tmp);
        if (mAdapter.getItems().isEmpty()){
            mTextView.setVisibility(View.VISIBLE);
        }
        if (tmp.isEmpty()){
            mTextView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mTextView.setText(R.string.no_fav_grammar);
        }

        mProgressBar.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();

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
        switch (mDictionaryCustomizer.getOptionsMenu()){
            case 0:
                break;
            case 1:
                openPopupMenu(view, position);
                break;
        }
        return false;
    }

    private void openPopupMenu(final View view, final int position){
        mPopupMenu = new PopupMenu(context, view);
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

    private void openDetailView(final int position){
        Intent intent = new Intent(getActivity(), DetailGrammarActivity.class);
        intent.putExtra("GRAMMAR_NAME", mAdapter.getItems().get(position).getGrammar());
        intent.putExtra("GRAMMAR_CATEGORY", mAdapter.getItems().get(position).getCategory());
        startActivity(intent);
//        Log.i(TAG, "Position: " + position + ", Category: " + mItems.get(position).getCategory() + ", Grammar: " + mItems.get(position).getGrammar());
    }


    public void changeLearned (final int position){
        String to = User.getCurrentUserUID() + "/" + mItems.get(position).getCategory() + "/" + mItems.get(position).getGrammar();
        if (mAdapter.getItems().isEmpty()) {
            UserDataGrammar.changeLearned(to, true);
        } else {
            Boolean learned = mAdapter.getItem(position).getLearned();
            if (learned == null)
                learned = false;
            if (learned){
                UserDataGrammar.changeLearned(to, false);
                mAdapter.changeLearned(position, false);
            } else {
                UserDataGrammar.changeLearned(to, true);
                mAdapter.changeLearned(position, true);
            }
        }
    }

    public void changeFavorite (final int position){
        String to = User.getCurrentUserUID() + "/" + mItems.get(position).getCategory() + "/" + mItems.get(position).getGrammar();
        if (mItems.isEmpty()) {
            UserDataGrammar.changeLearned(to, true);
        } else {
            Boolean favorite = (mItems.get(position).getFavorite());
            if (favorite == null)
                favorite = false;
            if (favorite){
                UserDataGrammar.changeFavorite(to, false);
                mAdapter.changeFavorite(position, false);
            } else {
                UserDataGrammar.changeFavorite(to, true);
                mAdapter.changeFavorite(position, true);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.only_search, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<FullGrammarModel> searchResult = searchFilter(mItems, newText);
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

    private List<FullGrammarModel> searchFilter(List<FullGrammarModel> items, String query) {
        query = query.toLowerCase();
        final List<FullGrammarModel> filteredList = new ArrayList<>();
        for (FullGrammarModel item : items) {
            if (item.getGrammar().toLowerCase().contains(query)) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    @Override
    public void onRefresh() {
        updUI();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
