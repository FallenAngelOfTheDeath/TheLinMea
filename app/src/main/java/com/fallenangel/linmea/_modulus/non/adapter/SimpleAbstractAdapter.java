package com.fallenangel.linmea._modulus.non.adapter;

import android.support.v7.widget.RecyclerView;

import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._linmea.model.BaseModel;

import java.util.List;

/**
 * Created by NineB on 1/17/2018.
 */

public abstract class SimpleAbstractAdapter <T extends BaseModel, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private static final String TAG = "SimpleAbstractAdapter";

    public OnRecyclerViewClickListener mClickListener;
    protected List<T> mItemsList;

    public SimpleAbstractAdapter(List<T> itemList, OnRecyclerViewClickListener clickListener) {
        this.mClickListener = clickListener;
        this.mItemsList = itemList;
    }

    public void onBindViewHolder(VH holder, int position){
        final T item = getItem(position);
        bindItemData(holder, item, position);
    }

    protected abstract void bindItemData(VH viewHolder, T data, int position);

    public T getItem(int position) {
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

}
