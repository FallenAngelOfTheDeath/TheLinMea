/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.main_dictionary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.non.adapter.AbstractRecyclerViewAdapter;
import com.fallenangel.linmea._modulus.non.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.non.interfaces.OnStartDragListener;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by NineB on 12/9/2017.
 */

public class MainDictionaryAdapter extends AbstractRecyclerViewAdapter<MainDictionaryModel, MainDictionaryAdapter.ViewHolder> {

    public MainDictionaryAdapter(Context context, List items, OnRecyclerViewClickListener clickListener, OnStartDragListener dragStartListener, OnItemTouchHelper onItemTouchHelper) {
        super(context, items, clickListener, dragStartListener, onItemTouchHelper);
    }

    @Override
    protected void bindItemData(MainDictionaryAdapter.ViewHolder viewHolder, MainDictionaryModel data, int position) {
        viewHolder.mWord.setText(data.getWord());
        viewHolder.mTranslation.setText(data.getTranslation());
    }

    @Override
    public MainDictionaryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.extended_list_item, parent, false);
        MainDictionaryAdapter.ViewHolder viewHolder = new MainDictionaryAdapter.ViewHolder(view, mClickListener);
        return viewHolder;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mWord, mTranslation;
        public ImageView mOptions, mStatus, mHandle;
        private OnRecyclerViewClickListener mClickListener;

        public ViewHolder(View view, OnRecyclerViewClickListener clickListener) {
            super(view);
            this.mClickListener = clickListener;

            mStatus = (ImageView) itemView.findViewById(R.id.extended_list_item_status_image_view);
            mOptions = (ImageView) itemView.findViewById(R.id.extended_list_item_options_menu);
            mHandle = (ImageView) itemView.findViewById(R.id.extended_list_item_handle);

            mStatus.setVisibility(View.GONE);
            mOptions.setVisibility(View.GONE);
            mHandle.setVisibility(View.GONE);

            mWord = (TextView) itemView.findViewById(R.id.extended_list_item_word_text_view);
            mTranslation = (TextView) itemView.findViewById(R.id.extended_list_item_translation_text_view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.extended_list_item_options_menu:
                    if (mClickListener != null) {
                        mClickListener.onOptionsClicked(v, getAdapterPosition ());
                    }
                    break;
                case R.id.extended_list_item_card_view:
                    if (mClickListener != null) {
                        mClickListener.onItemClicked(v, getAdapterPosition ());
                    }
                    break;
            }
        }
    }

    public void sortByWords(Boolean reverse){

        rx.Observable
                .from(mItems)
                .toSortedList(new Func2<MainDictionaryModel, MainDictionaryModel, Integer>() {
                    @Override
                    public Integer call(MainDictionaryModel o1, MainDictionaryModel o2) {
                        if (reverse) {
                            return -o1.getWord().toLowerCase().compareTo(o2.getWord().toLowerCase());
                        } else {
                            return o1.getWord().toLowerCase().compareTo(o2.getWord().toLowerCase());
                        }
                    }
                })
                .doOnNext(new Action1<List<MainDictionaryModel>>() {
                    @Override
                    public void call(List<MainDictionaryModel> mainDictionaryModels) {
                        mItems.clear();
                        mItems.addAll(mainDictionaryModels);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        notifyDataSetChanged();
                    }
                })
                .subscribe();
    }

    public void sortByTranslation(Boolean reverse){

        rx.Observable
                .from(mItems)
                .toSortedList(new Func2<MainDictionaryModel, MainDictionaryModel, Integer>() {
                    @Override
                    public Integer call(MainDictionaryModel o1, MainDictionaryModel o2) {
                        if (reverse) {
                            return -o1.getTranslation().toLowerCase().compareTo(o2.getTranslation().toLowerCase());
                        } else {
                            return o1.getTranslation().toLowerCase().compareTo(o2.getTranslation().toLowerCase());
                        }
                    }
                })
                .doOnNext(new Action1<List<MainDictionaryModel>>() {
                    @Override
                    public void call(List<MainDictionaryModel> items) {
                        mItems.clear();
                        mItems.addAll(items);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        notifyDataSetChanged();
                    }
                })
                .subscribe();
    }

    public void sorting(int sortingParam, OnSorting onSorting){
        onSorting.onStartSorting();
        Observable
                .from(mItems)
                .toSortedList(new Func2<MainDictionaryModel, MainDictionaryModel, Integer>() {
                    @Override
                    public Integer call(MainDictionaryModel o1, MainDictionaryModel o2) {
                        if (sortingParam == 0)
                            return o1.getWord().toLowerCase().compareTo(o2.getWord().toLowerCase());
                        else
                        if (sortingParam == 1)
                            return -o1.getWord().toLowerCase().compareTo(o2.getWord().toLowerCase());
                        else
                        if (sortingParam == 2)
                            return o1.getTranslation().toLowerCase().compareTo(o2.getTranslation().toLowerCase());
                        else
                        if (sortingParam == 3)
                            return -o1.getTranslation().toLowerCase().compareTo(o2.getTranslation().toLowerCase());
                        else
                            return o1.getWord().toLowerCase().compareTo(o2.getWord().toLowerCase());
                    }
                })
                .doOnNext(new Action1<List<MainDictionaryModel>>() {
                    @Override
                    public void call(List<MainDictionaryModel> items) {
                        mItems.clear();
                        mItems.addAll(items);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        notifyDataSetChanged();
                        onSorting.onCompleteSorting();
                    }
                })
                .subscribe();
    }
}
