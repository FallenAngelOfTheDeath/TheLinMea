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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.auth.View.RxEditText;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.main.ui.MainActivity;
import com.fallenangel.linmea._modulus.non.utils.SharedPreferencesUtils;
import com.fallenangel.linmea._modulus.non.utils.Utils;
import com.fallenangel.linmea._modulus.non.utils.Blur;
import com.fallenangel.linmea._modulus.prferences.utils.LoadDefaultConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

import static com.fallenangel.linmea._modulus.auth.utils.ValidationOfAuth.passwordComparison;
import static com.fallenangel.linmea._modulus.auth.utils.ValidationOfAuth.validateEmail;
import static com.fallenangel.linmea._modulus.auth.utils.ValidationOfAuth.validatePassword;

public class SignUpActivity extends SuperAppCompatActivity implements View.OnClickListener, OnCompleteListener {

    @Inject public User user;
    @Inject
    Context mContex;

    private LinearLayout mLayout;

    private Button signupButton;
    private ProgressBar pbSignUp;
    private EditText emailEditText, password1EditText, password2EditText;
    private TextView tvSignIn;

    Observable<String> emailObservable, fPasswordObservable, sPasswordObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getAppComponent().inject(this);
        implementUI();
        verificationObservable();
    }

    private void implementUI(){
        mLayout = (LinearLayout) findViewById(R.id.sign_up_layout);

        signupButton = (Button) findViewById(R.id.btn_singup);
        signupButton.setOnClickListener(this);

        emailEditText = (EditText) findViewById(R.id.email_singup);
        password1EditText = (EditText) findViewById(R.id.password_singup_1);
        password2EditText = (EditText) findViewById(R.id.password_singup_2);

        mLayout.setBackgroundResource(R.drawable.background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            Blur.applyBlur(mLayout, SignUpActivity.this);

        tvSignIn = (TextView) findViewById(R.id.sign_in_tv);
        pbSignUp = (ProgressBar) findViewById(R.id.progress_bar_sign_up);

        tvSignIn.setOnClickListener(this);

        signupButton.setEnabled(false);
    }

    private void verificationObservable(){
        fPasswordObservable = RxEditText.getTextWatcherOnAfterTextChanged(password1EditText);
        sPasswordObservable = RxEditText.getTextWatcherOnAfterTextChanged(password2EditText);
        emailObservable = RxEditText.getTextWatcherOnAfterTextChanged(emailEditText);

        Observable
                .combineLatest(emailObservable, fPasswordObservable, sPasswordObservable, new Func3<String, String, String, Boolean>() {
                    @Override
                    public Boolean call(String s, String s2, String s3) {
                        if (validateEmail(s)){
                            if (!validatePassword(s2) & !validatePassword(s3) & !passwordComparison(s2, s3)){
                                return false;
                            } else {
                                return true;
                            }
                        } else {
                            return false;
                        }
                    }
                })
                .doOnNext(aBoolean -> signupButton.setEnabled(aBoolean))
                .observeOn(Schedulers.computation())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe();
        // .subscribe(aBoolean -> signupButton.setEnabled(aBoolean));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in_tv:
                Intent signIn = new Intent(this, LoginActivity.class);
                startActivity(signIn);
                finish();
                break;
            case R.id.btn_singup:
                Utils.hideKeyboard(this);
                pbSignUp.setVisibility(View.VISIBLE);
                signupButton.setText("");
                user.createUserWithEmailAndPassword(emailEditText.getText().toString(),
                        password1EditText.getText().toString(), this);
                break;
        }
    }


    @Override
    public void onComplete(@NonNull Task task) {
        if (!task.isSuccessful()) {
            pbSignUp.setVisibility(View.GONE);
            signupButton.setText("Sign UP");
            Snackbar snack = Snackbar
                    .make(findViewById(android.R.id.content),
                            this.getString(R.string.user_allready_exist),
                            Snackbar.LENGTH_LONG);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            snack.show();
        } else {
            String email = emailEditText.getText().toString();
            user.sendEmailVerification();
            user.updateUserName(email.substring(0, email.lastIndexOf('@')), new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Toast.makeText(getApplicationContext(), getString(R.string.registrationissuccessful), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            if (SharedPreferencesUtils.getBollFromSharedPreferences(mContex, "FIRST", "START")) {
                LoadDefaultConfig mLoadDefaultConfig = new LoadDefaultConfig(mContex);
                mLoadDefaultConfig.createSampleCustomDictionary();
            }

        }
    }
}