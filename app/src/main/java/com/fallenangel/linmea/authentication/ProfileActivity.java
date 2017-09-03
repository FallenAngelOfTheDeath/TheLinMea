package com.fallenangel.linmea.authentication;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView mNameTV, mEmailTV, mUidTV, mEmailVerificationTV, mConfirmEmailTextView, mDataCreatedTextView, mSingInTimeTextView;
    private FloatingActionButton mFABProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseUser user = mAuth.getInstance().getCurrentUser();

        mNameTV = (TextView)findViewById(R.id.user_name_profile_status);
        mEmailTV = (TextView)findViewById(R.id.email_profile_status);
        mUidTV = (TextView)findViewById(R.id.uid_profile_status);
        mEmailVerificationTV = (TextView)findViewById(R.id.email_verification_status);
        mDataCreatedTextView = (TextView)findViewById(R.id.data_profile_created);
        mSingInTimeTextView = (TextView)findViewById(R.id.data_last_singin);

        mConfirmEmailTextView = (TextView)findViewById(R.id.confirm_email_textview);
        mConfirmEmailTextView.setOnClickListener(this);

        mFABProfile = (FloatingActionButton)findViewById(R.id.fab_profile);
        mFABProfile.setOnClickListener(this);

        userData(user);
    }


    private void userData (FirebaseUser user){

        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();


            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();

            mNameTV.setText(name);
            mEmailTV.setText(email);
            mUidTV.setText(uid);
            mEmailVerificationTV.setText(String.valueOf(emailVerified));
        }
    }


    @Override
    public void onClick(View v) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        switch (v.getId()){
            case R.id.fab_profile:
                String userUID = user.getUid();
                if (user != null) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.signOut();
                    Log.i(String.valueOf(getText(R.string.LOG_TAG_AUTH)), R.string.user + " " + userUID + " has logged out!" );
                }
                break;
            case R.id.confirm_email_textview:
                user.sendEmailVerification();
                break;
            default:
                break;
        }
    }
}
