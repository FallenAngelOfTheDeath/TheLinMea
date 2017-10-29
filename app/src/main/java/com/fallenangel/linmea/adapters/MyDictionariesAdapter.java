package com.fallenangel.linmea.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.interfaces.OnItemTouch;
import com.fallenangel.linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea.interfaces.OnStartDragListener;
import com.fallenangel.linmea.linmea.adapter.AbstractRecyclerViewAdapter;
import com.fallenangel.linmea.model.MyDictionaryModel;

import java.util.List;

/**
 * Created by NineB on 9/22/2017.
 */

public class MyDictionariesAdapter extends AbstractRecyclerViewAdapter<MyDictionaryModel, MyDictionariesAdapter.ViewHolder> {

    public MyDictionariesAdapter(Context context, List<MyDictionaryModel> items, OnRecyclerViewClickListener clickListener, OnStartDragListener dragStartListener, OnItemTouch onItemTouch) {
        super(context, items, clickListener, dragStartListener, onItemTouch);
    }

    @Override
    protected void bindItemData(ViewHolder viewHolder, MyDictionaryModel data, int position) {
        MyDictionaryModel dictionary = mItems.get(position);
        viewHolder.mName.setText(dictionary.getName());
        viewHolder.mDescription.setText(dictionary.getDescription());
        if (dictionary.getDescription() == null){
            viewHolder.mDescription.setVisibility(View.GONE);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dictionaies_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mClickListener);
        return viewHolder;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView mName, mDescription;
        public ImageView mOptions;
        public CardView mCardView;

        private OnRecyclerViewClickListener mClickListener;

        public ViewHolder(View view, OnRecyclerViewClickListener clickListener) {
            super(view);
            this.mClickListener = clickListener;

            mName = (TextView) itemView.findViewById(R.id.dictionary_name_text_view);
            mDescription = (TextView) itemView.findViewById(R.id.dictionary_description_text_view);
            mOptions = (ImageView) itemView.findViewById(R.id.item_options_menu);
            mCardView = (CardView) itemView.findViewById(R.id.my_dictionaries_item_card_view);

            mOptions.setOnClickListener(this);
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
    }
}
