package com.fallenangel.linmea._linmea.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._linmea.interfaces.OnStartDragListener;
import com.fallenangel.linmea._linmea.model.CustomDictionaryModel;
import com.fallenangel.linmea.interfaces.OnItemTouchHelper;

import java.util.List;

/**
 * Created by NineB on 12/4/2017.
 */

public class LinkedWordsRVAdapter extends AbstractRecyclerViewAdapter<CustomDictionaryModel, LinkedWordsRVAdapter.ViewHolder> {

    public LinkedWordsRVAdapter(Context context, List<CustomDictionaryModel> items, OnRecyclerViewClickListener clickListener, OnStartDragListener dragStartListener, OnItemTouchHelper onItemTouchHelper) {
        super(context, items, clickListener, dragStartListener, onItemTouchHelper);
    }

    @Override
    protected void bindItemData(LinkedWordsRVAdapter.ViewHolder viewHolder, CustomDictionaryModel data, int position) {
        viewHolder.mWord.setText(data.getWord());
        viewHolder.mTranslation.setText(data.getTranslationString());
    }

    @Override
    public LinkedWordsRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.linked_word_item;

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        LinkedWordsRVAdapter.ViewHolder viewHolder = new LinkedWordsRVAdapter.ViewHolder(view, mClickListener);
        return viewHolder;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView mWord, mTranslation;
        public CardView mCardView;
        private OnRecyclerViewClickListener mClickListener;

        public ViewHolder(View itemView, OnRecyclerViewClickListener clickListener) {
            super(itemView);
            this.mClickListener = clickListener;

            mWord = (TextView) itemView.findViewById(R.id.linked_word);
            mTranslation = (TextView) itemView.findViewById(R.id.linked_translation);
            mCardView = (CardView) itemView.findViewById(R.id.linked_card_view);
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
