package com.fallenangel.linmea._linmea.interfaces;

import android.view.View;

/**
 * Created by NineB on 4/16/2017.
 */

public interface OnRecyclerViewClickListener {
    void onItemClicked(View view, int position);
    void onOptionsClicked(View view, int position);
    boolean onItemLongClicked(View view, int position);
}
