/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/28/18 3:06 AM
 */

package com.fallenangel.linmea._modulus.custom_dictionary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.non.interfaces.OnStartDragListener;
import com.fallenangel.linmea._modulus.custom_dictionary.data.MyDictionaryModel;
import com.fallenangel.linmea._modulus.non.adapter.AbstractRecyclerViewAdapter;
import com.fallenangel.linmea._modulus.prferences.DictionaryCustomizer;
import com.fallenangel.linmea._modulus.non.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea._modulus.non.interfaces.OnItemTouchHelperViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NineB on 9/22/2017.
 */

public class DictionariesAdapter extends AbstractRecyclerViewAdapter<MyDictionaryModel, DictionariesAdapter.ViewHolder> {

    private DictionaryCustomizer mDC;

    public DictionariesAdapter(Context context, DictionaryCustomizer dc, List<MyDictionaryModel> items, OnRecyclerViewClickListener clickListener, OnStartDragListener dragStartListener, OnItemTouchHelper onItemTouchHelper) {
        super(context, items, clickListener, dragStartListener, onItemTouchHelper);
        this.mDC = dc;
    }

    @Override
    protected void bindItemData(ViewHolder viewHolder, MyDictionaryModel data, int position) {
        MyDictionaryModel dictionary = mItems.get(position);
        viewHolder.mName.setText(dictionary.getName());
        viewHolder.mDescription.setText(dictionary.getDescription());
        if (dictionary.getDescription() == null){
            viewHolder.mDescription.setVisibility(View.GONE);
        }
        viewHolder.mHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(viewHolder);
                }
                return false;
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dictionaies_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mDC, mClickListener);
        return viewHolder;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

    }

    @Override
    public void setItems(List<MyDictionaryModel> items, String sortedString) {
        List<MyDictionaryModel> sortedList = new ArrayList<>();
        if (!sortedString.isEmpty()) {
            Gson gson = new Gson();
            List<String> listOfSortedUIDS = gson.fromJson(sortedString, new TypeToken<List<String>>() {}.getType());
            int sortingListSize = listOfSortedUIDS.size();
            if (listOfSortedUIDS != null && listOfSortedUIDS.size() > 0) {
                for (int i = 0; i < sortingListSize; i++) {
                    int enteredItemsSize = items.size();
                    for (int j = 0; j < enteredItemsSize; j++) {
                        if (listOfSortedUIDS.get(i).equals(items.get(j).getName())) {
                            sortedList.add(items.get(j));
                            break;
                        }
                    }
                }
                sortedString = "";
                mItems = sortedList;
            } else {
                mItems = items;
            }
        } else {
            mItems = items;
        }
    }

    @Override
    public boolean onMove(int fromPosition, int toPosition) {
        moveItem(fromPosition, toPosition);
        mSortedString.clear();
        for (MyDictionaryModel item: mItems){
            mSortedString.add(item.getName());
        }

        if (mSelectedItems.size()>0){
            mSelectedItems.delete(fromPosition);
            mSelectedItems.put(toPosition, !mSelectedItems.get(toPosition));
        }

        //mMovedItems.put(mItems.get(toPosition).getUID() + "/id", toPosition);
        //mMovedItems.put(mItems.get(fromPosition).getUID() + "/id", fromPosition);
        return true;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener, OnItemTouchHelperViewHolder {

        public TextView mName, mDescription;
        public ImageView mOptions, mHandle;
        public CardView mCardView;

        private OnRecyclerViewClickListener mClickListener;

        public ViewHolder(View view, DictionaryCustomizer mDC, OnRecyclerViewClickListener clickListener) {
            super(view);
            this.mClickListener = clickListener;

            mName = (TextView) itemView.findViewById(R.id.dictionary_name_text_view);
            mDescription = (TextView) itemView.findViewById(R.id.dictionary_description_text_view);
            mOptions = (ImageView) itemView.findViewById(R.id.item_options_menu);
            mCardView = (CardView) itemView.findViewById(R.id.my_dictionaries_item_card_view);
            mHandle = (ImageView) itemView.findViewById(R.id.dictionaries_handle);

            switch (mDC.getOptionsMenu()){
                case 0:
                    mOptions.setOnClickListener(this);
                    mOptions.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    mOptions.setOnClickListener(null);
                    mOptions.setVisibility(View.GONE);
                    break;
            }
            mCardView.setOnClickListener(this);
            mCardView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.item_options_menu:
                    if (mClickListener != null) {
                        mClickListener.onOptionsClicked(v, getAdapterPosition ());
                    }
                    break;
                case R.id.my_dictionaries_item_card_view:
                    if (mClickListener != null) {
                        mClickListener.onItemClicked(v, getAdapterPosition ());
                    }
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mClickListener != null) {
                return mClickListener.onItemLongClicked(v, getAdapterPosition ());
            }
            return true;
        }


        @Override
        public void onItemSelected(RecyclerView.ViewHolder viewHolder) {
            mCardView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            mCardView.setBackgroundColor(Color.WHITE);
        }
    }



    @Override
    public void onItemMoveComplete(RecyclerView.ViewHolder viewHolder) {
        super.onItemMoveComplete(viewHolder);
        mOnItemTouchHelper.onItemMoveComplete(viewHolder);
    }
}
