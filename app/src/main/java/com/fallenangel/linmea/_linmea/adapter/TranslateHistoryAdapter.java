package com.fallenangel.linmea._linmea.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._linmea.model.Translation;

import java.util.List;

/**
 * Created by NineB on 1/17/2018.
 */

public class TranslateHistoryAdapter extends RecyclerView.Adapter<TranslateHistoryAdapter.ViewHolder> {

    private OnRecyclerViewClickListener mClickListener;
    private List<Translation> mItemsList;

    public TranslateHistoryAdapter(List<Translation> mItems, OnRecyclerViewClickListener mClickListener) {
        this.mClickListener = mClickListener;
        this.mItemsList = mItems;
    }

    @Override
    public TranslateHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.translation_history_item, parent, false);
        return new ViewHolder(itemView, mClickListener);
    }

    public void removeItem(int position) {
        mItemsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mItemsList.size());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Translation item = getItem(position);
        holder.sourceTest.setText(item.getSourceText());
        holder.translatedText.setText(item.getTranslatedText());
    }

    public Translation getItem(int position) {
        position = Math.max(0, position);
        return mItemsList.get(position);
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }

    public void clear (){
        mItemsList.clear();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView delete;
        public TextView sourceTest, translatedText;
        public CardView cardView;
        private OnRecyclerViewClickListener clickListener;

        public ViewHolder(View itemView, OnRecyclerViewClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;

            this.sourceTest = (TextView) itemView.findViewById(R.id.source_text);
            this.translatedText = (TextView) itemView.findViewById(R.id.translated_text);
            this.delete = (ImageView) itemView.findViewById(R.id.delete);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view_tr);

            cardView.setOnClickListener(this);
            delete.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.card_view_tr:
                    if (clickListener != null) {
                        clickListener.onItemClicked(view, getAdapterPosition ());
                    }
                    break;
                case R.id.delete:
                    if (clickListener != null) {
                        clickListener.onOptionsClicked(view, getAdapterPosition ());
                    }
                    break;
            }

        }

        @Override
        public boolean onLongClick(View view) {
            if (clickListener != null) {
                clickListener.onItemLongClicked(view, getAdapterPosition ());
            }
            return false;
        }
    }
}
