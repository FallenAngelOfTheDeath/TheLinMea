package com.fallenangel.linmea.interfaces;

import android.support.v7.widget.RecyclerView;

/**
 * Created by NineB on 10/30/2017.
 */

public interface OnItemTouchHelperViewHolder {
        void onItemSelected(RecyclerView.ViewHolder viewHolder);
        void onItemClear(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder);
}
