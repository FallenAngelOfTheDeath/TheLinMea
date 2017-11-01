package com.fallenangel.linmea.interfaces;

import android.support.v7.widget.RecyclerView;

/**
 * Created by NineB on 10/23/2017.
 */

public interface OnItemTouchHelper {
    boolean onMove(int fromPosition, int toPosition);
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    void onItemMoveComplete(RecyclerView.ViewHolder viewHolder);
    void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState);

    //  void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive);
//    int convertToAbsoluteDirection(int flags, int layoutDirection);
  //  void onClearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder);
//    void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive);
}
