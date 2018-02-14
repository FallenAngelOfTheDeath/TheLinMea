/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 8:20 PM
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
import com.fallenangel.linmea._modulus.non.DictionaryCompare;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.non.interfaces.OnStartDragListener;
import com.fallenangel.linmea._modulus.custom_dictionary.model.CustomDictionaryModel;
import com.fallenangel.linmea._modulus.non.adapter.AbstractRecyclerViewAdapter;
import com.fallenangel.linmea._modulus.prferences.DictionaryCustomizer;
import com.fallenangel.linmea._modulus.non.interfaces.OnItemTouchHelper;
import com.fallenangel.linmea._modulus.non.interfaces.OnItemTouchHelperViewHolder;

import java.util.Collections;
import java.util.List;

/**
 * Created by NineB on 9/18/2017.
 */

public class CustomDictionaryAdapter extends AbstractRecyclerViewAdapter<CustomDictionaryModel, CustomDictionaryAdapter.ViewHolder> {

    private Boolean mDragAndDrop;
    private int mDisplayType, mFountSizeWord, mFountSizeTranslation, mOptionsMenu, mLearnedColor, mSelectedColor;

    private DictionaryCustomizer mDictionaryCustomizer;

    public CustomDictionaryAdapter(Context context, List<CustomDictionaryModel> items, OnRecyclerViewClickListener clickListener, OnStartDragListener dragStartListener, OnItemTouchHelper onItemTouchHelper, DictionaryCustomizer dictionaryCustomizer) {
        super(context, items, clickListener, dragStartListener, onItemTouchHelper);
       // this.mDictionaryCustomizer = dictionaryCustomizer;
        this.mDictionaryCustomizer = dictionaryCustomizer;
        //mDictionaryCustomizer = new DictionaryCustomizer(mContext, mMode);
        mDisplayType = mDictionaryCustomizer.getDisplayType();
        mFountSizeWord = mDictionaryCustomizer.getFountSizeWord();
        mFountSizeTranslation = mDictionaryCustomizer.getFountSizeTranslation();
        mDragAndDrop = mDictionaryCustomizer.getDragAndDrop();
        mOptionsMenu = mDictionaryCustomizer.getOptionsMenu();
        mLearnedColor = mDictionaryCustomizer.getLearnedColor();
        mSelectedColor = mDictionaryCustomizer.getColorOfSelected();
    }

    public CustomDictionaryAdapter(Context context, List<CustomDictionaryModel> items, OnRecyclerViewClickListener clickListener, OnStartDragListener dragStartListener, OnItemTouchHelper onItemTouchHelper, DictionaryCustomizer dictionaryCustomizer, Boolean b) {
        super(context, items, clickListener, dragStartListener, onItemTouchHelper);
       // this.mDictionaryCustomizer = dictionaryCustomizer;
        this.mDictionaryCustomizer = dictionaryCustomizer;
        //mDictionaryCustomizer = new DictionaryCustomizer(mContext, mMode);
        mDisplayType = mDictionaryCustomizer.getDisplayType();
        mFountSizeWord = mDictionaryCustomizer.getFountSizeWord();
        mFountSizeTranslation = mDictionaryCustomizer.getFountSizeTranslation();
        mDragAndDrop = b;
        mOptionsMenu = mDictionaryCustomizer.getOptionsMenu();
        mLearnedColor = mDictionaryCustomizer.getLearnedColor();
        mSelectedColor = mDictionaryCustomizer.getColorOfSelected();
    }

    @Override
    protected void bindItemData(final ViewHolder viewHolder, CustomDictionaryModel data, int position) {
        switch (mDisplayType){
            //Card item view

            case 0:
                viewHolder.mWord.setText(data.getWord());
                viewHolder.mTranslation.setText(data.getTranslationString());
                viewHolder.mWord.setTextSize(mFountSizeWord);
                viewHolder.mTranslation.setTextSize(mFountSizeTranslation);
                break;
            case 1:
                viewHolder.mWord.setText(data.getWord());
                viewHolder.mWord.setTextSize(mFountSizeWord);
                break;
            case 2:
                viewHolder.mTranslation.setText(data.getTranslationString());
                viewHolder.mTranslation.setTextSize(mFountSizeTranslation);
                break;

            //List item view
            case 3:
                viewHolder.mWord.setText(data.getWord() + " - " + data.getTranslationString());
//                viewHolder.mTranslation.setText(data.getTranslationString());
                viewHolder.mWord.setTextSize(mFountSizeWord);
//                viewHolder.mTranslation.setTextSize(mFountSizeTranslation);
                break;
            case 4:
                viewHolder.mWord.setText(data.getWord());
                viewHolder.mWord.setTextSize(mFountSizeWord);
                break;
            case 5:
                viewHolder.mWord.setText(data.getTranslationString());
                viewHolder.mWord.setTextSize(mFountSizeWord);
                break;
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



            if (data.getFavorite() != null && data.getFavorite() == true) {
                viewHolder.mStatus.setImageResource(R.drawable.ic_favorite);
            } else {
                viewHolder.mStatus.setImageResource(R.drawable.ic_school);
            }
        changingBackgroundColors(viewHolder.mCardView, data, position);
    }

    private void changingBackgroundColors(CardView mCardView, CustomDictionaryModel data, int position){
        if (mSelectedItems.size() > 0){
            mCardView
                    .setBackgroundColor(mSelectedItems.get(position) ? mSelectedColor//0x9934B5E4
                            : Color.WHITE);


            if (data.getStatus() != null && data.getStatus() == true && !mSelectedItems.get(position)){
                mCardView
                        .setBackgroundColor(mItems.get(position).getStatus() ? mLearnedColor
                                : Color.WHITE);

//                viewHolder.itemView.setBackgroundColor(mItems.get(position).getStatus() ? mContext.getResources().getColor(R.color.background_recyclerview)
//                        : Color.WHITE);
            }
        } else {
            if (data.getStatus() != null)
                mCardView
                        .setBackgroundColor(mItems.get(position).getStatus() ? mLearnedColor
                                : Color.WHITE);
            else
                mCardView.setBackgroundColor(Color.WHITE);
//            viewHolder.itemView.setBackgroundColor(mItems.get(position).getStatus() ? mContext.getResources().getColor(R.color.background_recyclerview)
//                    : Color.WHITE);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.extended_list_item;
        switch (mDisplayType){
            case 0:
                layout = R.layout.extended_list_item;
                break;
            case 1:
                layout = R.layout.word_list_item;
                break;
            case 2:
                layout = R.layout.translation_list_item;
                break;
            case 3:
                layout = R.layout.simple_item;
                break;
            case 4:
                layout = R.layout.simple_item;
                break;
            case 5:
                layout = R.layout.simple_item;
                break;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view, mClickListener, mContext);
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener, OnItemTouchHelperViewHolder {

        public TextView mWord, mTranslation;
        public ImageView mOptions, mStatus, mHandle;
        public CardView mCardView;
        private Context mContext;
        private OnRecyclerViewClickListener mClickListener;

        public ViewHolder(View view, OnRecyclerViewClickListener clickListener, Context context) {
            super(view);
            this.mClickListener = clickListener;
            this.mContext = context;
//            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
//            String displayTypeN = sharedPref.getString("displayType", "0");
//            int displayType = Integer.parseInt(displayTypeN);

            switch (mDisplayType){
                case 0:
                    mWord = (TextView) itemView.findViewById(R.id.extended_list_item_word_text_view);
                    mTranslation = (TextView) itemView.findViewById(R.id.extended_list_item_translation_text_view);
                    break;
                case 1:
                    mWord = (TextView) itemView.findViewById(R.id.extended_list_item_word_text_view);
                    break;
                case 2:
                    mTranslation = (TextView) itemView.findViewById(R.id.extended_list_item_translation_text_view);
                    break;
                case 3:
                    mWord = (TextView) itemView.findViewById(R.id.extended_list_item_word_text_view);
                    mTranslation = (TextView) itemView.findViewById(R.id.extended_list_item_translation_text_view);
                    break;
                case 4:
                    mWord = (TextView) itemView.findViewById(R.id.extended_list_item_word_text_view);
                    break;
                case 5:
                    mWord = (TextView) itemView.findViewById(R.id.extended_list_item_word_text_view);
                    break;
            }

            mStatus = (ImageView) itemView.findViewById(R.id.extended_list_item_status_image_view);
            mOptions = (ImageView) itemView.findViewById(R.id.extended_list_item_options_menu);
            mHandle = (ImageView) itemView.findViewById(R.id.extended_list_item_handle);
            mCardView = (CardView) itemView.findViewById(R.id.extended_list_item_card_view);


            if (mDragAndDrop){
                mHandle.setVisibility(View.VISIBLE);
                mHandle.setClickable(true);
            } else {
                mHandle.setVisibility(View.GONE);
                mHandle.setClickable(false);
            }


            switch (mOptionsMenu){
                case 0:
                    mOptions.setVisibility(View.VISIBLE);
                    mOptions.setOnClickListener(this);
                    mOptions.setClickable(true);
                    break;
                case 1:
                    mOptions.setVisibility(View.GONE);
                    mOptions.setClickable(false);
                    break;
            }



            mCardView.setOnClickListener(this);
            mCardView.setOnLongClickListener(this);
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
            final CustomDictionaryModel item = getItem(viewHolder.getAdapterPosition());
            if (viewHolder.getAdapterPosition() != -1)
            changingBackgroundColors(mCardView, item, viewHolder.getAdapterPosition());
        }
    }

    @Override
    public boolean onMove(int fromPosition, int toPosition) {
        super.onMove(fromPosition, toPosition);
        return mOnItemTouchHelper.onMove(fromPosition, toPosition);
    }


    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

    }

    @Override
    public void onItemMoveComplete(RecyclerView.ViewHolder viewHolder) {
        super.onItemMoveComplete(viewHolder);
        mOnItemTouchHelper.onItemMoveComplete(viewHolder);
    }



    public void sortByWords(Boolean reverse){
        Collections.sort(mItems, new DictionaryCompare.WordCompare());
        if (reverse){
            Collections.reverse(mItems);
        }
  //      if (!mItems.isEmpty()) mItems.clear();
//        setItems(items);
//        notifyDataSetChanged();
    }

    public void sortByTranslation(Boolean reverse){
        Collections.sort(mItems, new DictionaryCompare.TranslationCompare());
        if (reverse){
            Collections.reverse(mItems);
        }
  //      if (!mItems.isEmpty()) mItems.clear();
 //       setItems(items);
       // notifyDataSetChanged();
    }

    public List<String> getUpdatedUidString(){
        if (!mSortedString.isEmpty())
            mSortedString.clear();
        for (CustomDictionaryModel item:mItems) {
            mSortedString.add(item.getUID());
        }
        return mSortedString;
    }

    public void changeFav(int pos){
        mItems.get(pos).setFavorite(mItems.get(pos).getFavorite() ? false : true);
        notifyDataSetChanged();
    }

    public void changeLearn(int pos){
        mItems.get(pos).setStatus(mItems.get(pos).getStatus() ? false : true);
        notifyDataSetChanged();
    }

    public void dragAndDropSwitcher(Boolean b){
        mDragAndDrop = b;
    }

}
