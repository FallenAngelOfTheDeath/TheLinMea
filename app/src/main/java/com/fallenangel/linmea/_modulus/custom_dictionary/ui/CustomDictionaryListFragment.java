/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.custom_dictionary.ui;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.custom_dictionary.adapter.DictionariesAdapter;
import com.fallenangel.linmea._modulus.custom_dictionary.data.FireBaseHelperListOfDict;
import com.fallenangel.linmea._modulus.custom_dictionary.data.MyDictionaryModel;
import com.fallenangel.linmea._modulus.main.supclasses.SuperFragment;
import com.fallenangel.linmea._modulus.main_dictionary.OnNewChildEventListener;
import com.fallenangel.linmea._modulus.non.adapter.ItemSwipeHelperCallback;
import com.fallenangel.linmea._modulus.non.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.non.interfaces.OnStartDragListener;
import com.fallenangel.linmea._modulus.non.interfaces.UnderlayButtonClickListener;
import com.fallenangel.linmea._modulus.non.view.UnderlayButton;
import com.fallenangel.linmea._modulus.prferences.DictionaryCustomizer;
import com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey;
import com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode;
import com.fallenangel.linmea._modulus.prferences.utils.NetworkUtils;
import com.fallenangel.linmea._modulus.prferences.utils.PreferenceUtils;
import com.fallenangel.linmea._modulus.testing.TestConfiguratorActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.DICTIONARY;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceKey.DICTIONARY_PAGE;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.CUSTOM_DICTIONARY_PAGE_1;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.CUSTOM_DICTIONARY_PAGE_2;
import static com.fallenangel.linmea._modulus.prferences.enums.PreferenceMode.SINGLE_PAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomDictionaryListFragment extends SuperFragment
        implements OnRecyclerViewClickListener, View.OnClickListener,
        UnderlayButtonClickListener, OnStartDragListener, OnItemTouchHelper {

//    private AlertDialog mAlertDialog;
    private static final String TAG = "CDLF";
    //Helper
    private FireBaseHelperListOfDict mDictFirebaseHelper;
    private ItemTouchHelper mItemTouchHelper;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private DictionariesAdapter mAdapter;
    private EditText etEditDescription;
    private List<MyDictionaryModel> mItems = new ArrayList<>();
    private TextView mNoDictionaries;
    @Inject DatabaseReference databaseReference;
    @Inject Context context;
    @Inject DictionaryCustomizer mDictionaryCustomizer;
    private Boolean statust = false;

    public CustomDictionaryListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getAppComponent().inject(this);
        mDictionaryCustomizer.loadMode(PreferenceMode.CUSTOM_DICTIONARY_LIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custom_dictionary_list, container, false);
        implementView(rootView);
        implementRecyclerView(rootView);
        implementRecyclerViewAdapter();
        implementItemTouchHelper();
        if (User.getCurrentUser() != null){
            loadData();
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        implementOnRecyclerScrollListener();
    }

    private void updateUI(){
        if (!NetworkUtils.isConnected(context)) {
            Snackbar.make(mRecyclerView, R.string.check_internet_connection, Snackbar.LENGTH_LONG).setAction(R.string.retry, v -> updateUI()).show();
        } else {
            loadData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //------------------------------------------------------------------------------------------

    private void implementView (View rootView){
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Dictionaries");
        FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.main_layout);
        FrameLayout.LayoutParams center = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        center.gravity = Gravity.CENTER;
        mNoDictionaries = new TextView(context);
        mNoDictionaries.setTextSize(32);
        mNoDictionaries.setTextColor(Color.LTGRAY);
        mNoDictionaries.setVisibility(View.GONE);
        frameLayout.addView(mNoDictionaries, center);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        if (!NetworkUtils.isConnected(context)) {
            mNoDictionaries.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mNoDictionaries.setTextSize(24);
            mNoDictionaries.setText(R.string.check_internet_connection);
        } else {
            mNoDictionaries.setText(R.string.no_dictionaries);
            mNoDictionaries.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateUI();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        mFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floating_action_button);
        mFloatingActionButton.setOnClickListener(this);
    }

    private void implementRecyclerView(View rootView){
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void implementRecyclerViewAdapter(){
        mAdapter = new DictionariesAdapter(context, mDictionaryCustomizer, mItems, this, this, this);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void implementItemTouchHelper () {
        mItemTouchHelper = new ItemTouchHelper(new ItemSwipeHelperCallback(getActivity(), ItemSwipeHelperCallback.BOTH_SIDE, mRecyclerView, mAdapter) {
            @Override
            public void onCreateRightUnderlayButton(final RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                UnderlayButton mDelete = new UnderlayButton(context, R.drawable.ic_action_delete);
                mDelete.setText("Delete");
                mDelete.setTextColor(Color.WHITE);
                mDelete.setBackgroundColor(Color.parseColor("#FF3C30"));
                mDelete.setId(0);
                mDelete.setOnClickListener(CustomDictionaryListFragment.this);
                underlayButtons.add(mDelete);


                UnderlayButton mEdit = new UnderlayButton(context, R.drawable.ic_action_edit);
                mEdit.setText("Edit");
                mEdit.setTextColor(Color.WHITE);
                mEdit.setBackgroundColor(Color.parseColor("#FF9502"));
                mEdit.setId(1);
                mEdit.setOnClickListener(CustomDictionaryListFragment.this);
                underlayButtons.add(mEdit);
            }

            @Override
            public void onCreateLeftUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                UnderlayButton mTesting = new UnderlayButton(context, R.drawable.ic_action_test_white);
                mTesting.setText("Test");
                mTesting.setTextColor(Color.WHITE);
                mTesting.setBackgroundColor(Color.parseColor("#ae52d4"));
                mTesting.setId(2);
                mTesting.setOnClickListener(CustomDictionaryListFragment.this);
                underlayButtons.add(mTesting);
            }
        });
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void implementOnRecyclerScrollListener(){
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mFloatingActionButton.getVisibility() == View.VISIBLE) {
                    mFloatingActionButton.hide();
                    mFloatingActionButton.setClickable(false);
                } else if (dy < 0 && mFloatingActionButton.getVisibility() != View.VISIBLE) {
                    mFloatingActionButton.show();
                    mFloatingActionButton.setClickable(true);

                }
            }
        });
    }

    private void loadData() {
        rxTimer();
        mItems.clear();
        String path = "custom_dictionary/" + User.getCurrentUserUID() + "/meta_data/";
        mDictFirebaseHelper = new FireBaseHelperListOfDict(databaseReference, path, mItems, mAdapter);
        mDictFirebaseHelper.getList(new OnNewChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                statust = true;
                mProgressBar.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public String getSortedString() {
                return mDictionaryCustomizer.getSortedStringOfUIDS();
            }
        });

    }

    //------------------------------------------------------------------------------------------

    @Override
    public void onItemClicked(View view, int position) {
        final MyDictionaryModel items = mAdapter.getItems(position);
        Intent userDictionaryIntent = new Intent(getActivity(), DictionaryActivity.class);
        userDictionaryIntent.putExtra("DictionaryName", items.getName());
        userDictionaryIntent.putExtra(DICTIONARY_PAGE.name(), SINGLE_PAGE);
        startActivity(userDictionaryIntent);
    }

    @Override
    public void onOptionsClicked(View view, final int position) {
        popupMenu(position, view);
    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        switch (mDictionaryCustomizer.getOptionsMenu()){
            case 0:
                break;
            case 1:
                popupMenu(position, view);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent addDictionary = new Intent(v.getContext(), AddCustomDictionaryActivity.class);
        startActivity(addDictionary);
    }

    public void popupMenu (final int position, final View view){
        final PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.custom_dictionary_list_popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.popup_menu_edit:
                        editDescription(position);
                        break;
                    case R.id.popup_menu_delete:
                        deleteDictionary(position);
                        break;
                    case R.id.popup_menu_testing:
                        startTesting(position);
                        break;

                }
                return true;
            }

        });
        popupMenu.show();
    }


    private void deleteDictionary(final int position){
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle("Delete");
        adb.setMessage("you are sure that you want to remove this a word, it can't be cancelled");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child("custom_dictionary")
                        .child(User.getCurrentUserUID())
                        .child("meta_data")
                        .child(mItems.get(position).getName())
                        .removeValue();
                databaseReference.child("custom_dictionary")
                        .child(User.getCurrentUserUID())
                        .child("dictionaries")
                        .child(mItems.get(position).getName())
                        .removeValue();
                if (mDictionaryCustomizer.getDictionaryByMode(CUSTOM_DICTIONARY_PAGE_1).equals(mItems.get(position).getName()))
                    PreferenceUtils.removePreference(context, CUSTOM_DICTIONARY_PAGE_1, DICTIONARY);
                if (mDictionaryCustomizer.getDictionaryByMode(CUSTOM_DICTIONARY_PAGE_2).equals(mItems.get(position).getName()))
                    PreferenceUtils.removePreference(context, CUSTOM_DICTIONARY_PAGE_2, DICTIONARY);

            }
        });
        adb.setNegativeButton("No", null);
        adb.show();
    }

    private void editDescription(int pos) {
        FrameLayout frameView = new FrameLayout(context);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle(R.string.edit_description);
        adb.setView(frameView);
        adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child("/custom_dictionary/" + User.getCurrentUserUID() +
                        "/meta_data/" + mAdapter.getItems(pos).getName() +
                        "/description").setValue(etEditDescription.getText().toString().trim());
                mAdapter.notifyDataSetChanged();
            }
        });
        adb.setNegativeButton(R.string.cancel, null);
        adb.setNeutralButton(R.string.remove_description, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.child("/custom_dictionary/" + User.getCurrentUserUID() +
                        "/meta_data/" + mAdapter.getItems(pos).getName() +
                        "/description").removeValue();
                mAdapter.notifyDataSetChanged();
            }
        });
        AlertDialog mAlertDialog = adb.create();
        LayoutInflater inflater = mAlertDialog.getLayoutInflater();
        inflater.inflate(R.layout.change_dict_description, frameView);
        etEditDescription = (EditText) frameView.findViewById(R.id.change_description);

        mAlertDialog.show();
    }

    private void startTesting(int position){
        final MyDictionaryModel items = mAdapter.getItems(position);
        Intent startTesting = new Intent(getActivity(), TestConfiguratorActivity.class);
        startTesting.putExtra("DictionaryName", items.getName());
        startActivity(startTesting);
    }

    @Override
    public void onClickUnderlayButton(int pos, int id) {
        switch (id){
            case 0:
                deleteDictionary(pos);
                break;
            case 1:
                editDescription(pos);
                break;
            case 2:
                startTesting(pos);
                break;

        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public boolean onMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

    }

    @Override
    public void onItemMoveComplete(RecyclerView.ViewHolder viewHolder) {
        if (!mAdapter.getSortedString().isEmpty()) {
            Gson gson = new Gson();
            String sortedJson = gson.toJson(mAdapter.getSortedString());
            PreferenceUtils.putPreference(getActivity(), PreferenceMode.CUSTOM_DICTIONARY_LIST, PreferenceKey.STRING_OF_SORTED_UID, sortedJson);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

    }

    private void rxTimer() {
        Observable
                .interval(0, 1, TimeUnit.SECONDS)
                .takeUntil(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong == 10;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        mProgressBar.setVisibility(View.GONE);
                        if (!statust){
                            mNoDictionaries.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .subscribe();
    }
}
