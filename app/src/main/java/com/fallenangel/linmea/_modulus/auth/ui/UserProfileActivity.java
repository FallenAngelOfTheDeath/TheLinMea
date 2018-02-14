/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.auth.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.auth.View.RxEditText;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.non.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func3;

import static com.fallenangel.linmea._modulus.auth.utils.ValidationOfAuth.passwordComparison;
import static com.fallenangel.linmea._modulus.auth.utils.ValidationOfAuth.validateEmail;
import static com.fallenangel.linmea._modulus.auth.utils.ValidationOfAuth.validatePassword;

public class UserProfileActivity extends SuperAppCompatActivity implements View.OnClickListener, OnCompleteListener, User.OnCompleteListenerWithCredit {

    private final static String TAG = "UserProfileActivity";


    @Inject User user;
    @Inject Context context;

    private AlertDialog.Builder adb;
    private AlertDialog mAlertDialog;

    private Toolbar mToolbar;
    private Button mDeleteUser, mLogOut;
    private EditText mName, mEmail, mEmailVerificatin, mPassword, mUID, etNewEmail;
    private ImageView ivEditName, ivEditEmail, ivEditPassword;
    private RelativeLayout tipEmailVer;

    private Boolean editNameStatus = false;
    private Boolean editEmailStatus = false;

    private Boolean passwordBool = false;

    View dialoglayout;
    EditText oldPassword, edFPassword, edSPassword, etDeleteAcc;
    TextView tvConfirm, tvCancel;
    rx.Observable<String> fObservablePassword, sObservablePassword, oldObservablePassword, observablePassword, observableEmail;
    FrameLayout frameView;
    LayoutInflater inflater;
    Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getAppComponent().inject(this);
        Utils.hideKeyboard(this);
        implementUI();
        if (!user.isNull()) updateUI();
    }

    private void implementUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setTitle(user.getCurrUser().getDisplayName());

        mName = (EditText) findViewById(R.id.user_name);
        mEmail = (EditText) findViewById(R.id.email);
        mEmailVerificatin = (EditText) findViewById(R.id.email_verification);
        mPassword = (EditText) findViewById(R.id.password);
        mUID = (EditText) findViewById(R.id.uid);
        tipEmailVer = (RelativeLayout) findViewById(R.id.email_verification_layout);

        ivEditName = (ImageView) findViewById(R.id.edit_name);
        ivEditEmail = (ImageView) findViewById(R.id.edit_email);
        ivEditPassword = (ImageView) findViewById(R.id.edit_password);

        mDeleteUser = (Button) findViewById(R.id.delete_account);
        mLogOut = (Button) findViewById(R.id.log_out);

        mDeleteUser.setOnClickListener(this);
        mLogOut.setOnClickListener(this);
        tipEmailVer.setOnClickListener(this);
        ivEditPassword.setOnClickListener(this);
        ivEditEmail.setOnClickListener(this);
        ivEditName.setOnClickListener(this);


        mName.setEnabled(false);
        // mName.setFocusable(false);

        mEmail.setEnabled(false);
        mEmailVerificatin.setEnabled(false);
        mEmailVerificatin.setClickable(true);
        mPassword.setEnabled(false);
        mUID.setEnabled(false);
    }

    private void updateUI (){
        mName.setText(user.getCurrUser().getDisplayName());
        mEmail.setText(user.getCurrUser().getEmail());
        mEmailVerificatin.setText(user.getCurrUser().isEmailVerified() ? "true" : "click here to send verification email");
        mPassword.setText("0123456789");
        mUID.setText(user.getCurrUserUid());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case -1:
                close();
                break;
            case R.id.email_verification_layout:
                if (!user.getCurrUser().isEmailVerified()) {
                    user.sendEmailVerification();
                    Snackbar.make(mDeleteUser, "Email has been send", Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.delete_account:
                deleteAccDialog();
                break;
            case R.id.confirm_password:
                confirmDeleteAcc();
                break;
            case R.id.log_out:
                logOutDialog();
                break;
            case R.id.edit_name:
                editName();
                break;
            case R.id.edit_email:
                changeEmailDialog();
                break;
            case R.id.confirm_email:
                confirmEmail();
                break;
            case R.id.edit_password:
                changePasswordDialog();
                break;
            case R.id.cancel:
                mAlertDialog.cancel();
                break;
            case R.id.confirm:
                if (passwordBool){
                    user.updatePassword(oldPassword.getText().toString(), edFPassword.getText().toString(), this);
                } else {
                    Snackbar.make(mDeleteUser, "Not valid password", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onComplete(@NonNull Task<Void> task, @NonNull Task<Void> creditTask) {
        if (task.isSuccessful()) {
            Snackbar.make(mDeleteUser, "Task is successful", Snackbar.LENGTH_LONG).show();
            passwordBool = false;
            subscription.unsubscribe();
            mAlertDialog.cancel();
        } else {
            Snackbar.make(frameView, task.getException().toString(), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreditError(@NonNull Task<Void> creditTask) {
        if(!creditTask.isSuccessful()) {
            try {
                throw creditTask.getException();
            } catch(FirebaseAuthWeakPasswordException e) {
                Log.e(TAG, "onCreditError: ", e);
            } catch(FirebaseAuthInvalidCredentialsException e) {
                Log.e(TAG, "onCreditError: ", e);
            } catch(FirebaseAuthUserCollisionException e) {
                Log.e(TAG, "onCreditError: ", e);

            } catch(Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }


        Log.e(TAG, "onCreditError: ", creditTask.getException());
        Snackbar.make(frameView, creditTask.getException().toString(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()) {
            Toast.makeText(context, "Task is successful", Toast.LENGTH_LONG).show();
            passwordBool = false;
            subscription.unsubscribe();
            mAlertDialog.cancel();
        } else {
            Snackbar.make(frameView, task.getException().toString(), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        close();
    }

    private void close(){
        if (!editEmailStatus && !editNameStatus){
            finish();
        } else {
            Snackbar.make(mDeleteUser, "Changes will not be saved.", Snackbar.LENGTH_LONG).setAction("Confirm", view -> finish()).show();
        }
    }

    private void changePasswordDialog(){
        frameView = new FrameLayout(context);
        adb = new AlertDialog.Builder(this);
        adb.setTitle(getResources().getString(R.string.change_password));
        adb.setView(frameView);
        mAlertDialog = adb.create();
        inflater = mAlertDialog.getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.change_password, frameView);
        tvCancel = (TextView) dialoglayout.findViewById(R.id.cancel);
        tvConfirm = (TextView) dialoglayout.findViewById(R.id.confirm);
        oldPassword = (EditText) dialoglayout.findViewById(R.id.old_password);
        edFPassword = (EditText) dialoglayout.findViewById(R.id.first_password);
        edSPassword = (EditText) dialoglayout.findViewById(R.id.second_password);
        fObservablePassword = RxEditText.getTextWatcherOnAfterTextChanged(edFPassword);
        sObservablePassword = RxEditText.getTextWatcherOnAfterTextChanged(edSPassword);
        oldObservablePassword = RxEditText.getTextWatcherOnAfterTextChanged(oldPassword);
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        mAlertDialog.show();
        subscription = Observable.combineLatest(fObservablePassword, sObservablePassword, oldObservablePassword, new Func3<String, String, String, Boolean>() {
                    @Override
                    public Boolean call(String s, String s2, String s3) {
                        if (validatePassword(s3)) {
                            if (!validatePassword(s) & !validatePassword(s2) & !passwordComparison(s, s2))
                                return false;
                            else
                                return true;
                        } else {
                            return false;
                        }
                    }
                })
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        passwordBool = aBoolean;
                    }
                })
                .subscribe();
    }

    private void confirmEmail(){
        if (passwordBool){
            if (user.getCurrUser().getEmail() != null)
                user.updateUserEmail(etDeleteAcc.getText().toString(), etNewEmail.getText().toString(), user.getCurrUser().getEmail(), this);
            else
                Snackbar.make(dialoglayout, R.string.tray_later, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(dialoglayout, R.string.failed, Snackbar.LENGTH_LONG).show();
        }
    }

    private void changeEmailDialog(){
        frameView = new FrameLayout(context);
        adb = new AlertDialog.Builder(this);
        adb.setView(frameView);
        mAlertDialog = adb.create();
        inflater = mAlertDialog.getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.change_email, frameView);
        tvCancel = (TextView) dialoglayout.findViewById(R.id.cancel);
        tvCancel.setOnClickListener(this);
        tvConfirm = (TextView) dialoglayout.findViewById(R.id.confirm_email);
        tvConfirm.setOnClickListener(this);
        etDeleteAcc = (EditText) dialoglayout.findViewById(R.id.password_to_confirm_email);
        etNewEmail = (EditText) dialoglayout.findViewById(R.id.new_email);
        observablePassword = RxEditText.getTextWatcherOnAfterTextChanged(etDeleteAcc);
        observableEmail = RxEditText.getTextWatcherOnAfterTextChanged(etNewEmail);
        mAlertDialog.show();
        subscription = Observable.combineLatest(observableEmail, observablePassword, (s, s2) -> {
                    if (validateEmail(s) && validatePassword(s2))
                        return true;
                    else
                        return false;
                }).doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        passwordBool = aBoolean;
                    }
                }).subscribe();
    }

    private void confirmDeleteAcc(){
        if (passwordBool){
            if (user.getCurrUser().getEmail() != null)
                user.deleteUser(user.getCurrUser().getEmail(), etDeleteAcc.getText().toString(), new User.OnCompleteListenerWithCredit() {
                @Override
                public void onComplete(@NonNull Task<Void> task, @NonNull Task<Void> creditTask) {
                    if (task.isSuccessful()) {
                        if (task.getException() != null)
                            Snackbar.make(mDeleteUser, creditTask.getException().toString(), Snackbar.LENGTH_LONG).show();
                        passwordBool = false;
                        subscription.unsubscribe();
                        mAlertDialog.cancel();
                        Intent signUP = new Intent(context, SignUpActivity.class);
                        startActivity(signUP);
                        finish();
                    } else {
                        Snackbar.make(dialoglayout, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCreditError(@NonNull Task<Void> creditTask) {
                    Snackbar.make(dialoglayout, creditTask.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
            else
                Snackbar.make(dialoglayout, R.string.tray_later, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(mDeleteUser, "Not valid password", Snackbar.LENGTH_LONG).show();
        }
    }

    private void deleteAccDialog(){
        frameView = new FrameLayout(context);

        adb = new AlertDialog.Builder(this);
        adb.setTitle("Delete account?");
        adb.setView(frameView);

        mAlertDialog = adb.create();
        inflater = mAlertDialog.getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.password_to_confirm, frameView);
        tvCancel = (TextView) dialoglayout.findViewById(R.id.cancel);
        tvCancel.setOnClickListener(this);
        TextView confirm = (TextView) dialoglayout.findViewById(R.id.confirm_password);
        confirm.setOnClickListener(this);
        etDeleteAcc = (EditText) dialoglayout.findViewById(R.id.password_to_confirm);
        observablePassword = RxEditText.getTextWatcherOnAfterTextChanged(etDeleteAcc);
        subscription = observablePassword.exists(s -> {
            if (!validatePassword(s))
                return false;
            else
                return true;
        })
                .doOnNext(aBoolean -> passwordBool = aBoolean)
                .subscribe();
        mAlertDialog.show();
    }

    private void editName(){
        if (editNameStatus){
            if (mName.length() <= 0) {
                Snackbar.make(mName, "Field is empty", Snackbar.LENGTH_LONG).show();
            } else {
                user.updateUserName(mName.getText().toString().trim(), task -> {
                    if (task.isSuccessful()) {
                        Snackbar.make(mName, "Username has been changed", Snackbar.LENGTH_LONG).show();
                        mToolbar.setTitle(mName.getText().toString().trim());
                        editNameStatus = false;
                        mName.setEnabled(false);
                        mName.invalidate();
                        mName.requestFocus();
                        ivEditName.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_edit_black));
                        ivEditName.invalidate();
                    } else {
                        Snackbar.make(mEmail, "Task failed", Snackbar.LENGTH_LONG).show();
                    }});
            }
        } else {
            editNameStatus = true;
            mName.setEnabled(true);
            mName.invalidate();
            mName.requestFocus();
            ivEditName.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done_black));
            ivEditName.invalidate();
        }
    }

    private void logOutDialog(){
        adb = new AlertDialog.Builder(this);
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
    }
}
