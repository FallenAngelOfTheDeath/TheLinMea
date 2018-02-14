/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.main_dictionary;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.custom_dictionary.ui.AddCustomDictionaryActivity;
import com.fallenangel.linmea._modulus.custom_dictionary.ui.BaseDetailActivity;
import com.fallenangel.linmea._modulus.main.supclasses.SuperFragment;
import com.fallenangel.linmea._modulus.non.Constant;
import com.fallenangel.linmea._modulus.non.adapter.ItemSwipeHelperCallback;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.non.interfaces.UnderlayButtonClickListener;
import com.fallenangel.linmea._modulus.non.view.UnderlayButton;
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

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.fallenangel.linmea._modulus.auth.User.getCurrentUserUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainDictionaryFragment extends SuperFragment implements OnRecyclerViewClickListener, UnderlayButtonClickListener, View.OnClickListener {

    @Inject
    Context mContext;

    @Inject DatabaseReference databaseReference;
    private ProgressBar mProgressBar;
    private TextView tvLoading;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFAB;
    private MainDictionaryAdapter mAdapter;
    private FirebaseHelperMainDict mMainDBHelper;
    private ItemTouchHelper mItemTouchHelper;
    private ItemSwipeHelperCallback itemSwipeHelperCallback;
    private List<MainDictionaryModel> mItems = new ArrayList<>();
    private SearchView mSearchView;
    private int mCheckedItemOfAlertDialog, mPickedFilterValue;
    private Set<String> mListOfDictionaries = new HashSet<>();
    private AlertDialog.Builder mAlertDialogBuilder;
    private AlertDialog mAlertDialog;
    String[] mStringOfDictionaries;
    private Boolean updating = false;
    private Subscription tttr;
    private Query dicitonariesQuery;
    private ChildEventListener dictionariesListener;

    public MainDictionaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        implementUI(rootView);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MainDictionaryAdapter(getActivity(), mItems, this, null, null);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
        implementItemTouchHelper();
        loadDataToRV();
        tttr();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateUI(){
        if (!NetworkUtils.isConnected(mContext)) {
            Snackbar.make(mRecyclerView, R.string.check_internet_connection, Snackbar.LENGTH_LONG).setAction(R.string.retry, v -> updateUI()).show();
        } else {
            if (!updating) {
                updating = true;
                mProgressBar.setVisibility(View.VISIBLE);
                tvLoading.setText(R.string.loading);
                mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                mAdapter = new MainDictionaryAdapter(getActivity(), mItems, this, null, null);
                mAdapter.clear();
                mRecyclerView.setAdapter(mAdapter);
                mItemTouchHelper.attachToRecyclerView(mRecyclerView);
                loadDataToRV();
            }
        }
    }

    private void implementUI(View rootView){
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.main_dictionary);
        RelativeLayout mRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.relative_layout);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        tvLoading = new TextView(mContext);
        RelativeLayout.LayoutParams layoutParamsTextView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsTextView.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layoutParamsTextView.addRule(RelativeLayout.BELOW, mProgressBar.getId());
        tvLoading.setTextSize(24);
        tvLoading.setTextColor(Color.GRAY);
        mRelativeLayout.addView(tvLoading, layoutParamsTextView);
        mFAB = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button);
        mFAB.setImageResource(R.drawable.ic_filter_list);
        mFAB.setOnClickListener(this);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateUI();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (!NetworkUtils.isConnected(mContext)) {
            tvLoading.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            tvLoading.setTextSize(24);
            tvLoading.setText(R.string.check_internet_connection);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            tvLoading.setText(R.string.loading);
        }
    }

    private void loadDataToRV() {
        mProgressBar.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
        FirebaseHelperMainDict db =
                new FirebaseHelperMainDict(mContext, databaseReference, mItems, "main_dictionary/main/");
        db.rxListFiller(new OnChildAddEventListener() {
            @Override
            public void doOnNext(Object item) {

                // tvLoading.setText(mAdapter.getItemCount() + " of 31678");
            }

            @Override
            public void doOnCompleted() {
                updating = false;
                tvLoading.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }
        }, new OnStartSorting() {
            @Override
            public int onStartSorting() {
                return MainDictionaryPreference.getRearrangement(mContext);
            }
        });

    }

    @Override
    public void onItemClicked(View view, int position) {
        Intent viewIntent = new Intent(mContext, ViewMainWordActivity.class);
        viewIntent.putExtra(Constant.MAIN_WORD, mAdapter.getItem(position).getWord());
        viewIntent.putExtra(Constant.MAIN_TRANSLATION, mAdapter.getItem(position).getTranslation());
        startActivity(viewIntent);
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
        inflater.inflate(R.menu.main_dictionaary_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<MainDictionaryModel> searchResult = searchFilter(mItems, newText);
                mAdapter.setFilter(searchResult);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.scroll_to:
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Scroll to");
                adb.setItems(R.array.scroll_to, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scrollTo(i);
                    }
                });
                adb.setNegativeButton(R.string.cancel, null);
                adb.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<MainDictionaryModel> searchFilter(List<MainDictionaryModel> items, String query) {
//        mSharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
//        int searchBy = Integer.parseInt(mSharedPreferences.getString("searchBy", "0"));
//
//        dc
        String text;
        query = query.toLowerCase();
        final List<MainDictionaryModel> filteredList = new ArrayList<>();
        for (MainDictionaryModel item : items) {
            switch (DictionaryCustomizer.getSearchByStatic(getActivity())){
                case 0:
                    text = item.getWord().toLowerCase();
                    if (text.contains(query)) {
                        filteredList.add(item);
                    }
                    break;
                case 1:
                    text = item.getTranslation().toLowerCase();
                    if (text.contains(query)) {
                        filteredList.add(item);
                    }
                    break;
            }
        }
        return filteredList;
    }

    private void implementItemTouchHelper() {
        mItemTouchHelper = new ItemTouchHelper( itemSwipeHelperCallback = new ItemSwipeHelperCallback(mContext, ItemSwipeHelperCallback.BOTH_SIDE, mRecyclerView, mAdapter) {
            @Override
            public void onCreateRightUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                UnderlayButton mShare = new UnderlayButton(mContext, R.drawable.ic_action_share);
                mShare.setText("Share");
                mShare.setTextColor(Color.WHITE);
                mShare.setBackgroundColor(Color.parseColor("#ae52d4"));
                mShare.setId(0);
                mShare.setOnClickListener(MainDictionaryFragment.this);
                underlayButtons.add(mShare);
            }

            @Override
            public void onCreateLeftUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                UnderlayButton mAddToCustom = new UnderlayButton(mContext, R.drawable.ic_action_lib_add);
                mAddToCustom.setText("Copy TO");
                mAddToCustom.setTextColor(Color.WHITE);
                mAddToCustom.setBackgroundColor(Color.parseColor("#6ab7ff"));
                mAddToCustom.setId(1);
                mAddToCustom.setOnClickListener(MainDictionaryFragment.this);
                underlayButtons.add(mAddToCustom);
            }
        });
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onClickUnderlayButton(int pos, int id) {
        switch (id){
            case 0:
                doShare(pos);
                break;
            case 1:
                copyTo(pos);
                break;
        }
    }

    public void doShare(int position) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Word: " + mAdapter.getItems(position).getWord()  +
                        "\nTranslation: " + mAdapter.getItems(position).getTranslation());
        startActivity(intent);
    }

    private void copyTo(int pos){

        getDictionaryList();
        mAlertDialogBuilder = new AlertDialog.Builder(getActivity());

        if (mListOfDictionaries.isEmpty()){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View text_view_layout = inflater.inflate(R.layout.text_view_alert_dialog_dictionary, null);
            mAlertDialogBuilder.setView(text_view_layout);
        }else {
            mAlertDialogBuilder.setTitle(R.string.dict_customizer_dict_description);
            mAlertDialogBuilder.setSingleChoiceItems(mStringOfDictionaries, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPickedFilterValue = which;
                }
            });
        }
        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent copyToIntent = new Intent(mContext, BaseDetailActivity.class);
                copyToIntent.putExtra("DictionaryName", mStringOfDictionaries[mPickedFilterValue]);
                copyToIntent.putExtra("WORD", mAdapter.getItem(pos).getWord());
                copyToIntent.putExtra("TRANSLATION", mAdapter.getItem(pos).getTranslation());
                copyToIntent.putExtra("Mod", "AddMOD");
                startActivity(copyToIntent);

            }
        });
        mAlertDialogBuilder.setNeutralButton(R.string.add_new_dictionary, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent addDictionary = new Intent(mContext, AddCustomDictionaryActivity.class);
                startActivity(addDictionary);
            }
        });
        mAlertDialogBuilder.setNegativeButton(this.getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    public void sortingFilterDialog(){
        mAlertDialogBuilder = new AlertDialog.Builder(getActivity());
        mAlertDialogBuilder.setTitle(R.string.sorting_title);
        mCheckedItemOfAlertDialog = MainDictionaryPreference.getRearrangement(mContext);
        mAlertDialogBuilder.setSingleChoiceItems(R.array.sorting_elements, mCheckedItemOfAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPickedFilterValue = which;
            }
        });
        mAlertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainDictionaryPreference.setRearrangement(mContext, mPickedFilterValue);
                applyFilter(mPickedFilterValue);
            }
        });
        mAlertDialogBuilder.setNegativeButton(getString(R.string.cancel), null);
        mAlertDialog = mAlertDialogBuilder.create();
        mAlertDialog.show();
    }

    private void applyFilter(int param){
        mAdapter.sorting(param, new OnSorting() {
            @Override
            public void onStartSorting() {
                FrameLayout frameView = new FrameLayout(mContext);
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle(getResources().getString(R.string.resorting_elements));
                adb.setView(frameView);
                mAlertDialog = adb.create();
                mAlertDialog.setCancelable(false);
                LayoutInflater inflater = mAlertDialog.getLayoutInflater();
                inflater.inflate(R.layout.waiting, frameView);
                mAlertDialog.show();
            }

            @Override
            public void onCompleteSorting() {
                mAlertDialog.cancel();
            }
        });
        mAdapter.notifyDataSetChanged();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.floating_action_button:
                sortingFilterDialog();
                break;
        }
    }
    private void tttr (){
        tttr = getDictionaryList().doOnNext(new Action1<String>() {
            @Override
            public void call(String s) {
                mListOfDictionaries.add(s);
                mStringOfDictionaries = mListOfDictionaries.toArray(new String[0]);
            }
        }).subscribeOn(Schedulers.newThread()).subscribe();
    }

    private Observable<DataSnapshot> observableFirebase(String path){
        PublishSubject<DataSnapshot> subject = PublishSubject.create();
        final int[] counter = {0};
        Query query = databaseReference.child(path);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                subject.onNext(dataSnapshot);
                counter[0]++;
                //  subject.delay(1, TimeUnit.SECONDS);
                Log.i("tttttttr", "onChildAdded: " + counter[0] + " : " + mAdapter.getItemCount());
                if (counter[0] >= 31679){
                    subject.onCompleted();
                    query.removeEventListener(this);
                    subject.onCompleted();
                }
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

    private Observable<String> getDictionaryList(){
        PublishSubject<String> subject = PublishSubject.create();
        String path = "custom_dictionary/" + getCurrentUserUID() + "/meta_data/";
        dictionariesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                subject.onNext(dataSnapshot.getKey());
              //  subject.delay(1, TimeUnit.SECONDS);
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
              //  Log.i("tttr", "onCancelled: ");
            }
        };

        dicitonariesQuery = databaseReference.child(path);
        dicitonariesQuery.addChildEventListener(dictionariesListener);

        return subject;
    }

    private void scrollTo(int to){
        switch(to){
            case 0:
                mRecyclerView.scrollToPosition(0);
                break;
            case 1:
                mRecyclerView.scrollToPosition(Math.round(mAdapter.getItemCount()/10));
                break;
            case 2:
                mRecyclerView.scrollToPosition(Math.round(mAdapter.getItemCount()/5));
                break;
            case 3:
                mRecyclerView.scrollToPosition(Math.round((mAdapter.getItemCount()/5) + (mAdapter.getItemCount()/10)));
                break;
            case 4:
                mRecyclerView.scrollToPosition(Math.round((mAdapter.getItemCount()/5) * 2));
                break;
            case 5:
                mRecyclerView.scrollToPosition(Math.round(mAdapter.getItemCount()/2));
                break;
            case 6:
                mRecyclerView.scrollToPosition(Math.round((mAdapter.getItemCount()/2) + (mAdapter.getItemCount()/10)));
                break;
            case 7:
                mRecyclerView.scrollToPosition(Math.round((mAdapter.getItemCount()/2) + (mAdapter.getItemCount()/5)));
                break;
            case 8:
                mRecyclerView.scrollToPosition(Math.round((mAdapter.getItemCount()/2) + ((mAdapter.getItemCount()/5) + (mAdapter.getItemCount()/10))));
                break;
            case 9:
                mRecyclerView.scrollToPosition(Math.round((mAdapter.getItemCount()/2) + ((mAdapter.getItemCount()/5) * 2)));
                break;
            case 10:
                mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            tttr.unsubscribe();
            dicitonariesQuery.removeEventListener(dictionariesListener);
        } catch (Exception e){
            System.out.println(e);
        }

    }
}
