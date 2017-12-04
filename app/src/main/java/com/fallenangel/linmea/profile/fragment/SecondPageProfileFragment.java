package com.fallenangel.linmea.profile.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.linmea.user.authentication.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondPageProfileFragment extends Fragment implements View.OnClickListener {

    private TextView mRestorePassword, mConfirmEmail,  mConfirmEmailAction;

    public SecondPageProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_second_page_profile, container, false);

        implementUI(rootView);

        return rootView;
    }

    private void implementUI(View v) {
        mConfirmEmail = (TextView) v.findViewById(R.id.confirm_email_profile);
        mConfirmEmailAction = (TextView) v.findViewById(R.id.confirm_email_action_profile);
        mRestorePassword = (TextView) v.findViewById(R.id.restore_password_profile);

        mConfirmEmailAction.setOnClickListener(this);
        mRestorePassword.setOnClickListener(this);

        if (User.getCurrentUser().isEmailVerified() == true) {
            mConfirmEmail.setText(getString(R.string.user_email_verification) + " " + User.getCurrentUser().isEmailVerified());
            mConfirmEmailAction.setVisibility(View.GONE);
        } else {
            mConfirmEmail.setText(getString(R.string.user_email_verification) + " " + User.getCurrentUser().isEmailVerified());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm_email_action_profile:
                if (User.getCurrentUser().isEmailVerified() == false){
                    User.getCurrentUser().sendEmailVerification();
                    Snackbar snackbar = Snackbar
                            .make(mConfirmEmailAction, getActivity().getString(R.string.email_has_been_sent), Snackbar.LENGTH_SHORT);
                    snackbar.setDuration(30000);
                    snackbar.show();
                } else {

                }
                break;
            case R.id.restore_password_profile:
                break;
        }
    }
}
