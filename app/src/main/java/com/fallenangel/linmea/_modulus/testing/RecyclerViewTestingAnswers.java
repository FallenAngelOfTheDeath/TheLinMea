/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 9:01 PM
 */

package com.fallenangel.linmea._modulus.testing;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.non.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._modulus.testing.models.TestItem;

import java.util.List;

/**
 * Created by NineB on 1/16/2018.
 */

public class RecyclerViewTestingAnswers extends RecyclerView.Adapter<RecyclerViewTestingAnswers.ViewHolder> {

    private OnRecyclerViewClickListener mClickListener;
    private List<TestItem> mItemsList;

    public RecyclerViewTestingAnswers(List<TestItem> mItemsSet, OnRecyclerViewClickListener mClickListener) {
        this.mClickListener = mClickListener;
        this.mItemsList = mItemsSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.answer, parent, false);
        return new ViewHolder(itemView, mClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TestItem item = getItem(position);
        holder.answer.setText(item.getAnswer());
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }

    public TestItem getItem(int position) {
        position = Math.max(0, position);
        return mItemsList.get(position);
    }

    public void setItems(List<TestItem> items) {
        mItemsList = items;
    }

    public void clear (){
        mItemsList.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView answer;
        public CardView cardView;
        private OnRecyclerViewClickListener clickListener;

        public ViewHolder(View itemView, OnRecyclerViewClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;
            cardView = (CardView) itemView.findViewById(R.id.card_view_answer);
            answer = (TextView) itemView.findViewById(R.id.answer);

            itemView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClicked(view, getAdapterPosition ());
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
