package com.fallenangel.linmea._test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.ui.society.ChatRoomActivity;


public class TestFragment extends Fragment implements View.OnClickListener {

    private Button mFirstTestButtom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_test, container, false);

        mFirstTestButtom = (Button) view.findViewById(R.id.test_button);
        mFirstTestButtom.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
       // FirebaseDatabase.getInstance().getReference().child("user").child(User.getCurrentUserUID()).child("other_metadata").child("date_of_creation").setValue(ServerValue.TIMESTAMP);
        Intent newIntent = new Intent(getActivity(), ChatRoomActivity.class);
        startActivity(newIntent);
    }
}
