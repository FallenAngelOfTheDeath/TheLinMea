package com.fallenangel.linmea._modulus.auth.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.non.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class UserProfileActivity extends SuperAppCompatActivity implements View.OnClickListener, OnCompleteListener {

    private final static String TAG = "UserProfileActivity";


    @Inject public User user;
    @Inject public Context context;

    private AlertDialog.Builder adb;
    private AlertDialog mAlertDialog;

    private Toolbar mToolbar;
    private Button mDeleteUser, mLogOut;
    private EditText mName, mEmail, mEmailVerificatin, mPassword, mUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getAppComponent().inject(this);
        implementUI();
        if (!user.isNull()) updateUI();


    }



    private void implementUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setTitle(user.getCurrUser().getDisplayName());
        setSupportActionBar(mToolbar);

        mName = (EditText) findViewById(R.id.user_name);
        mEmail = (EditText) findViewById(R.id.email);
        mEmailVerificatin = (EditText) findViewById(R.id.email_verification);
        mPassword = (EditText) findViewById(R.id.password);
        mUID = (EditText) findViewById(R.id.uid);
    }

    private void updateUI (){
        mName.setText(user.getCurrUser().getDisplayName());
        mEmail.setText(user.getCurrUser().getEmail());
        mEmailVerificatin.setText(user.getCurrUser().isEmailVerified() ? "true" : "click here to send verification email");
        mPassword.setText("0123456789");
        mUID.setText(user.getCurrUserUid());

        mDeleteUser = (Button) findViewById(R.id.delete_account);
        mLogOut = (Button) findViewById(R.id.log_out);

        mDeleteUser.setOnClickListener(this);
        mLogOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (Constant.DEBUG == 1) Log.d(TAG, "onClick: " + view.getId());
        switch (view.getId()){
            case -1:
                finish();
                break;
            case R.id.delete_account:
                adb = new AlertDialog.Builder(context);
                adb.setTitle("Delete account?");
                adb.setMessage("Are you sure? This can not be undone!");
                adb.setPositiveButton("Yes", (dialog, which) -> {
                    user.deleteUser(this);
                });
                adb.setNegativeButton("No", null);
                mAlertDialog = adb.create();
                mAlertDialog.show();
                break;
            case R.id.log_out:
                adb = new AlertDialog.Builder(context);
                adb.setTitle("Log OUT");
                adb.setMessage("are you sure?");
                adb.setPositiveButton("Yes", (dialog, which) -> {
                    user.signOut();
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
                adb.setNegativeButton("No", null);
                mAlertDialog = adb.create();
                mAlertDialog.show();
                break;
        }
        finish();
    }

    @Override
    public void onComplete(@NonNull Task task) {
        Intent intent = new Intent(context, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}
