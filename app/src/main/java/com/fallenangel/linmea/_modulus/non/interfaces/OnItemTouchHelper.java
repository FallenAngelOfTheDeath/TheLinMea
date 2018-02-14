/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/27/18 11:41 PM
 */

package com.fallenangel.linmea._modulus.non.interfaces;

import android.support.v7.widget.RecyclerView;

/**
 * Created by NineB on 10/23/2017.
 */

public interface OnItemTouchHelper {
    boolean onMove(int fromPosition, int toPosition);
    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    void onItemMoveComplete(RecyclerView.ViewHolder viewHolder);
    void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState);
}
