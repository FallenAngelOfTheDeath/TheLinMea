package com.fallenangel.linmea._linmea.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._linmea.model.CustomDictionaryModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by NineB on 1/6/2018.
 */

public class LinkedWordAdapter extends RecyclerView.Adapter<LinkedWordAdapter.ViewHolder> {

    private HashSet<CustomDictionaryModel> mItems;
    private OnRecyclerViewClickListener mClickListener;
    private List<CustomDictionaryModel> mItemsList;

    public LinkedWordAdapter(HashSet<CustomDictionaryModel> mItems, OnRecyclerViewClickListener clickListener) {
        this.mItems = mItems;
        this.mClickListener = clickListener;
        this.mItemsList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linked_word_item, parent, false);
        return new ViewHolder(itemView, mClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        if (!mItemsList.isEmpty()) {
            final CustomDictionaryModel item = getItem(position);
            holder.mWord.setText(item.getWord());
            holder.mTranslation.setText(item.getTranslationString());
     //   }




//        Iterator iterator = mItems.iterator();
//      //  while(iterator.hasNext()){
//            CustomDictionaryModel item = (CustomDictionaryModel) iterator.next();
//            CustomDictionaryModel item2 = (CustomDictionaryModel) iterator.next();
//            holder.mWord.setText(item.getNewWord());
//            holder.mTranslation.setText(item.getTranslationString());
//            Log.i("gggggggggggggggg", "onBindViewHolder: " + item2.getNewWord());
//       // }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public CustomDictionaryModel getItem(int position) {
        position = Math.max(0, position);

        for (CustomDictionaryModel subset : mItems) {
            mItemsList.add(subset);
        }
       // CollectionConverter.sort(mItemsList, new DictionaryCompare.WordCompare());
        return mItemsList.get(position);
    }
//
//    public CustomDictionaryModel getItem(int position) {
//        position = Math.max(0, position);
//        for (int i = 0; i < position; i++) {
//            Iterator iterator = mItems.iterator();
//            while(iterator.hasNext()){
//                if (i == position)
//                    return (CustomDictionaryModel) iterator.next();
//            }
//        }
//        return null;
//    }

    public void setItems(HashSet<CustomDictionaryModel> items) {
        mItems = items;
    }

    public void clear (){
        mItems.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {

        public TextView mWord, mTranslation;
        public CardView mCardView;
        private OnRecyclerViewClickListener mClickListener;

        public ViewHolder(View itemView, OnRecyclerViewClickListener clickListener) {
            super(itemView);
            this.mClickListener = clickListener;
            mWord = (TextView) itemView.findViewById(R.id.linked_word);
            mTranslation = (TextView) itemView.findViewById(R.id.linked_translation);
            mCardView = (CardView) itemView.findViewById(R.id.linked_card_view);

            itemView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClicked(v, getAdapterPosition ());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemLongClicked(v, getAdapterPosition ());
            }
            return false;
        }
    }
}
