/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 9:01 PM
 */

package com.fallenangel.linmea._modulus.custom_dictionary.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.custom_dictionary.adapter.CustomDictionaryAdapter;
import com.fallenangel.linmea._modulus.custom_dictionary.ui.CustomDictionaryFragment;

public class CDFToolbarActionMode implements ActionMode.Callback {

    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private Context mContext;
    private CustomDictionaryAdapter mAdapter;
    private CustomDictionaryFragment mCustomDictionaryFragment;
    private Boolean alertDialog = false;

    public CDFToolbarActionMode(Context context, CustomDictionaryAdapter adapter, CustomDictionaryFragment fragment) {
        this.mContext = context;
        this.mAdapter = adapter;
        this.mCustomDictionaryFragment = fragment;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.findItem(R.id.action_mode_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.action_mode_favorite).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.action_mode_learned_status).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
        selectedItems = mAdapter.getSelectedIds();
        switch (item.getItemId()) {
            case R.id.action_mode_delete:
                alertDialog = true;
                final AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                adb.setTitle("Delete");
                adb.setMessage("you are sure that you want to delete this a words, it can't be cancelled");
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCustomDictionaryFragment.deleteSelectedItems();
                        destroyActionMode();
                        mode.finish();
                    }
                });
                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        destroyActionMode();
                        mode.finish();
                    }
                });
                adb.setNeutralButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        alertDialog = false;
                    }
                });
                adb.show();
                break;
            case R.id.action_mode_favorite:
                alertDialog = true;
                final AlertDialog.Builder adbF = new AlertDialog.Builder(mContext);
                adbF.setTitle("Delete");
                adbF.setMessage("you are sure that you want to change favorite status for this a words?");
                adbF.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCustomDictionaryFragment.changeSelectedItemsFavoriteStatus();
                        destroyActionMode();
                        mode.finish();
                    }
                });
                adbF.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        destroyActionMode();
                        mode.finish();
                    }
                });
                adbF.setNeutralButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        alertDialog = false;
                    }
                });
                adbF.show();
                break;
            case R.id.action_mode_learned_status:
                alertDialog = true;
                final AlertDialog.Builder adbL = new AlertDialog.Builder(mContext);
                adbL.setTitle("Delete");
                adbL.setMessage("you are sure that you want to change learned status for this a words?");
                adbL.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCustomDictionaryFragment.changeSelectedItemsLearnedStatus();
                        destroyActionMode();
                        mode.finish();
                    }
                });
                adbL.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        destroyActionMode();
                        mode.finish();
                    }
                });
                adbL.setNeutralButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        alertDialog = false;
                    }
                });
                adbL.show();
                break;
        }
        return false;
    }

    private void destroyActionMode(){
        mAdapter.removeSelection();
        mCustomDictionaryFragment.setNullToActionMode();
        alertDialog = false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if (!alertDialog){
            destroyActionMode();
        }
    }
}
