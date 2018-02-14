/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 9:01 PM
 */

package com.fallenangel.linmea._modulus.non.interfaces;

import android.support.v7.widget.RecyclerView;

/**
 * Created by NineB on 10/30/2017.
 */

public interface OnItemTouchHelperViewHolder {
        void onItemSelected(RecyclerView.ViewHolder viewHolder);
        void onItemClear(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder);
}
