package com.fallenangel.linmea._linmea.ui.translator;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.adapter.TranslateHistoryAdapter;
import com.fallenangel.linmea._linmea.data.firebase.TranslationWrapper;
import com.fallenangel.linmea._linmea.interfaces.OnRecyclerViewClickListener;
import com.fallenangel.linmea._linmea.model.Translation;
import com.fallenangel.linmea._modulus.auth.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NineB on 1/17/2018.
 */

public class TranslateHistory extends Fragment implements OnRecyclerViewClickListener, ChildEventListener {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private TranslateHistoryAdapter mAdapter;

    private TranslationWrapper translationWrapper =  new TranslationWrapper();

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private String dbHistoryPath = "translator/history/" + User.getCurrentUserUID();

    private List<Translation> mItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        implementUI(view);
        implementRecyclerView(view);
        loadTranslationHistory();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    private void implementRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_ans);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new TranslateHistoryAdapter(mItems, this);
        mAdapter.clear();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void implementUI(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(view.getResources().getString(R.string.translation_history));
    }

    private void loadTranslationHistory(){
       ChildEventListener childEventListener = mDatabaseReference.child(dbHistoryPath).addChildEventListener(this);
    }


    @Override
    public void onItemClicked(View view, int position) {

    }

    @Override
    public void onOptionsClicked(View view, int position) {
        final Translation item = mItems.get(position);

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle(getResources().getString(R.string.delete));
        adb.setMessage(getResources().getString(R.string.delete_mes));
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabaseReference.child(dbHistoryPath).child(item.getUID()).removeValue();
                mAdapter.removeItem(position);
            }
        });
        adb.setNegativeButton("No", null);
        adb.show();
    }

    @Override
    public boolean onItemLongClicked(View view, int position) {
        return false;
    }





    @SuppressLint("ResourceType")
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

//        if (mAdapter.getItemCount() == 0){
//            RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.relative_layout);
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//            TextView textView = new TextView(getActivity());
//            textView.setText("History is empty");
//            textView.setText(getActivity().getResources().getColor(R.color.colorBlack));
//            textView.setLayoutParams( layoutParams);
//            relativeLayout.addView(textView);
//        }

        Translation item = translationWrapper.getTranslationHistory(dataSnapshot);
        mItems.add(item);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
