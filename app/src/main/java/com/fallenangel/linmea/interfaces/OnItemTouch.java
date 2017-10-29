package com.fallenangel.linmea.interfaces;

import android.support.v7.widget.RecyclerView;

/**
 * Created by NineB on 10/23/2017.
 */

public interface OnItemTouch {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    void onSelectedChanged (RecyclerView.ViewHolder viewHolder, int actionState);
}
