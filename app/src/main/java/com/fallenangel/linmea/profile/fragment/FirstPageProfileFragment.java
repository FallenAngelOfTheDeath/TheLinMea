package com.fallenangel.linmea.profile.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.linmea.model.UserModel;
import com.fallenangel.linmea.linmea.user.authentication.user;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstPageProfileFragment extends Fragment {

    private TextView mFirstNameTextView, mLastNameTextView, mNickNameTextView,
            mEmailTextView, mDataProfileCreated, mBirthday, mBiography, mCity;

    UserModel userModel;

    private UserModel mUserModel;

    public FirstPageProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first_page_profile, container, false);


        implementUI(rootView);
        Log.i("ghmjvhjkvgnukmvhu", "onCreateView: " + user.getCurrentUser().getDisplayName());

        mNickNameTextView.setText(getString(R.string.nickname_profile) + " " + user.getCurrentUser().getDisplayName());
        mEmailTextView.setText(getString(R.string.email_profile) + " " + user.getCurrentUser().getEmail());

        if (user.getCurrentUser() != null){

            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("user").child(user.getCurrentUserUID()).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    UserModel userM = new UserModel();

                    String mFirstName2 = (String) dataSnapshot.child("main_metadata").child("first_name").getValue();
                    String mLastName2 = (String) dataSnapshot.child("main_metadata").child("last_name").getValue();
                    String mBiography2 = (String) dataSnapshot.child("other_metadata").child("biography").getValue();
//                    Date mBirthday2 = (Date) dataSnapshot.child("other_metadata").child("birthday").getValue();
                    String mCity2 = (String) dataSnapshot.child("other_metadata").child("city").getValue();
//                    Date mDateOfCreation2 = (Date) dataSnapshot.child("other_metadata").child("date_of_creation").getValue();

                    userM.setFirestName(mFirstName2);
                    userM.setLastName(mLastName2);
                    userM.setBiography(mBiography2);
                    userM.setCity(mCity2);

                    mFirstNameTextView.setText(getString(R.string.first_name_profile) + " " + mFirstName2);
                    mLastNameTextView.setText(getString(R.string.last_name_profile) + " " + mLastName2);
                    //     mDataProfileCreated.setText(getString(R.string.date_profile_created) + " " + mDateOfCreation2.toString());
                    //     mBirthday.setText(getString(R.string.birthday_profile) + " " + mBirthday2);
                    mBiography.setText(getString(R.string.biography_profile) + " " + mBiography2);
                    mCity.setText(getString(R.string.city_profile) + " " + mCity2);








//                    user.setFirestName(mFirstName);
//                    user.setLastName(mLastName);
//                    user.setBiography(mBiography);
//                    user.setBiography(mBiography);
//                    user.setBirthday(mBirthday);
//                    user.setDateOfCreation(mDateOfCreation);
//                    user.setCity(mCity);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });





        }









        return rootView;
    }

    private void implementUI (View view) {
        mNickNameTextView = (TextView)  view.findViewById(R.id.nick_name_profile);
        mFirstNameTextView = (TextView)  view.findViewById(R.id.first_name_profile);
        mLastNameTextView = (TextView)  view.findViewById(R.id.last_name_profile);
        mEmailTextView = (TextView)  view.findViewById(R.id.email_profile);
        mDataProfileCreated = (TextView)  view.findViewById(R.id.date_profile_created_profile);
        mCity = (TextView)  view.findViewById(R.id.city_profile);
        mBiography = (TextView)  view.findViewById(R.id.biography_profile);
        mBirthday = (TextView)  view.findViewById(R.id.birthday_profile);



    }



}
