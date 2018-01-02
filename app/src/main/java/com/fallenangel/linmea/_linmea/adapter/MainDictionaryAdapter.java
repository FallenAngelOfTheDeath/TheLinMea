package com.fallenangel.linmea._linmea.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._linmea.interfaces.OnStartDragListener;
import com.fallenangel.linmea._linmea.model.MainDictionaryModel;
import com.fallenangel.linmea.interfaces.OnItemTouchHelper;

import java.util.List;

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
}
