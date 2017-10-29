package com.fallenangel.linmea.linmea.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
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
import com.fallenangel.linmea.interfaces.OnItemTouch;
import com.fallenangel.linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea.interfaces.OnStartDragListener;
import com.fallenangel.linmea.linmea.collection.DictionaryCompare;
import com.fallenangel.linmea.model.CustomDictionaryModel;

import java.util.Collections;
import java.util.List;

/**
 * Created by NineB on 9/18/2017.
 */

public class CustomDictionaryAdapter extends AbstractRecyclerViewAdapter<CustomDictionaryModel, CustomDictionaryAdapter.ViewHolder> {

    public CustomDictionaryAdapter(Context context, List<CustomDictionaryModel> items, OnRecyclerViewClickListener clickListener, OnStartDragListener dragStartListener, OnItemTouch onItemTouch) {
        super(context, items, clickListener, dragStartListener, onItemTouch);
    }
    String displayTypeKey = "displayType";
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    String displayTypeN = sharedPref.getString(displayTypeKey, "0");
    int displayType = Integer.parseInt(displayTypeN);

    int fountSizeWord = sharedPref.getInt("fountSizeWord", 18);
    int fountSizeTranslation = sharedPref.getInt("fountSizeTranslation", 16);

    @Override
    protected void bindItemData(final ViewHolder viewHolder, CustomDictionaryModel data, int position) {

       // CustomDictionaryModel item = mItems.get(position);
        switch (displayType){
            case 0:
                viewHolder.mWord.setText(data.getWord());
                viewHolder.mTranslation.setText(data.getTranslationString());
                viewHolder.mWord.setTextSize(fountSizeWord);
                viewHolder.mTranslation.setTextSize(fountSizeTranslation);

                break;
            case 1:
                viewHolder.mWord.setText(data.getWord());
                viewHolder.mWord.setTextSize(fountSizeWord);
                break;
            case 2:
                viewHolder.mTranslation.setText(data.getTranslationString());
                viewHolder.mTranslation.setTextSize(fountSizeTranslation);
                break;
        }

//        viewHolder.mHandle.setOnDragListener(this);

        viewHolder.mHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) ==
                        MotionEvent.ACTION_DOWN) {
//                    ClipData data = ClipData.newPlainText("", "");
//                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
//                            v);
//                    v.startDrag(data, shadowBuilder, v, 0);
                    mDragStartListener.onStartDrag(viewHolder);
                }
                return false;
            }
        });

        if (data.getFavorite() == true){
            viewHolder.mStatus.setImageResource(R.drawable.ic_favorite);
        } else {
            viewHolder.mStatus.setImageResource(R.drawable.ic_school);

        }

        if (mSelectedItems.size() > 0){
            viewHolder.mCardView
                    .setBackgroundColor(mSelectedItems.get(position) ? 0x9934B5E4
                            : Color.WHITE);
            if (data.getStatus() == true && !mSelectedItems.get(position)){
                viewHolder.mCardView
                        .setBackgroundColor(mItems.get(position).getStatus() ? Color.TRANSPARENT
                                : Color.WHITE);
            }
        } else {
            viewHolder.mCardView
                    .setBackgroundColor(mItems.get(position).getStatus() ? Color.TRANSPARENT
                            : Color.WHITE);
        }



    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.extended_list_item;
        switch (displayType){
            case 0:
                layout = R.layout.extended_list_item;
                break;
            case 1:
                layout = R.layout.word_list_item;
                break;
            case 2:
                layout = R.layout.translation_list_item;
                break;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view, mClickListener);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView mWord, mTranslation;
        public ImageView mOptions, mStatus, mHandle;
        public CardView mCardView;

        private OnRecyclerViewClickListener mClickListener;

        public ViewHolder(View view, OnRecyclerViewClickListener clickListener) {
            super(view);
            this.mClickListener = clickListener;

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
            String displayTypeN = sharedPref.getString("displayType", "0");
            int displayType = Integer.parseInt(displayTypeN);

            switch (displayType){
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
            }

            mStatus = (ImageView) itemView.findViewById(R.id.extended_list_item_status_image_view);
            mOptions = (ImageView) itemView.findViewById(R.id.extended_list_item_options_menu);
            mHandle = (ImageView) itemView.findViewById(R.id.extended_list_item_handle);
            mCardView = (CardView) itemView.findViewById(R.id.extended_list_item_card_view);

            mOptions.setOnClickListener(this);
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


    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        super.onItemMove(fromPosition, toPosition);
        return mOnItemTouch.onItemMove(fromPosition, toPosition);
    }

    @Override
    public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        super.onItemSwiped(viewHolder, direction, position);
        mOnItemTouch.onItemSwiped(viewHolder, direction, position);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        mOnItemTouch.onSelectedChanged(viewHolder, actionState);
    }

    public void orderBy (Context context, List<CustomDictionaryModel> mItems) {

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        final String orderBy = SP.getString("orderBy", "0");
        int orderByN = Integer.parseInt(orderBy);

        switch (orderByN) {
            case 0:
                break;
            case 1:
                Collections.reverse(mItems);
                break;
            case 2:
                Collections.sort(mItems, new DictionaryCompare.WordCompare());
                break;
            case 3:
                Collections.sort(mItems, new DictionaryCompare.WordCompare());
                Collections.reverse(mItems);
                break;
            case 4:
                Collections.sort(mItems, new DictionaryCompare.TranslationCompare());
                break;
            case 5:
                Collections.sort(mItems, new DictionaryCompare.TranslationCompare());
                Collections.reverse(mItems);
                break;
            default:
                break;
        }
    }

}
