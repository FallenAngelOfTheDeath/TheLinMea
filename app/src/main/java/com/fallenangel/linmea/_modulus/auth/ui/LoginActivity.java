package com.fallenangel.linmea._modulus.auth.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.auth.User;
import com.fallenangel.linmea._modulus.auth.View.RxEditText;
import com.fallenangel.linmea._modulus.main.supclasses.SuperAppCompatActivity;
import com.fallenangel.linmea._modulus.main.ui.MainActivity;
import com.fallenangel.linmea._modulus.non.utils.Utils;
import com.fallenangel.linmea.linmea.utils.image.Blur;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.fallenangel.linmea._modulus.auth.utils.ValidationOfAuth.validateEmail;
import static com.fallenangel.linmea._modulus.auth.utils.ValidationOfAuth.validatePassword;

public class LoginActivity extends SuperAppCompatActivity implements View.OnClickListener, OnCompleteListener {

    @Inject public User user;

    private CoordinatorLayout mLayout;
    private ProgressBar mProgressBar;

    private Button loginButton;
    private EditText emailEditText, passwordEditText;
    private TextView mResetPasswordTextView, mSignUpTextView;
    Observable<String> emailObservable, passwordObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getAppComponent().inject(this);

        implementUI();
        verificationObservable();




    }

    private void verificationObservable() {
        passwordObservable = RxEditText.getTextWatcherOnAfterTextChanged(passwordEditText);
        emailObservable = RxEditText.getTextWatcherOnAfterTextChanged(emailEditText);

        emailObservable.exists(s -> {
            if (s.isEmpty())
                return false;
            else
                return true;
        }).doOnNext(aBoolean -> {
            mResetPasswordTextView.setEnabled(aBoolean);
            mResetPasswordTextView.setClickable(aBoolean);
        }).subscribe();
;

        Observable
                .combineLatest(emailObservable, passwordObservable, (s, s2) -> {
                    if (validateEmail(s)){
                        if (!validatePassword(s2)){
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return false;
                    }
                })
                .doOnNext(aBoolean -> loginButton.setEnabled(aBoolean))
                .subscribeOn(Schedulers.computation())
                .subscribe();
    }


    private void implementUI(){
        mLayout = (CoordinatorLayout) findViewById(R.id.login_layout);

        mResetPasswordTextView = (TextView)findViewById(R.id.restore_password_textview);
        mSignUpTextView = (TextView)findViewById(R.id.singup_textview);

        mResetPasswordTextView.setOnClickListener(this);
        mSignUpTextView.setOnClickListener(this);

        loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(this);

        emailEditText = (EditText) findViewById(R.id.username_login);
        passwordEditText = (EditText) findViewById(R.id.password_login);

        mLayout.setBackgroundResource(R.drawable.background);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            Blur.applyBlur(mLayout, LoginActivity.this);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_sign_in);
        mProgressBar.setVisibility(View.GONE);

        loginButton.setEnabled(false);
        mResetPasswordTextView.setEnabled(false);
        mResetPasswordTextView.setClickable(false);

        //emailEditText.setText("speedvagon@spv.com");
        emailEditText.setText("fallenangelofthedeath@gmail.com");
        ///passwordEditText.setText("SpeedVagon1Vasya");
        passwordEditText.setText("947019sdk20");


    }

    @Override
    public void onClick(View v) {
        String email = emailEditText.getText().toString();
        switch (v.getId()){
            case R.id.btn_login:
                Utils.hideKeyboard(this);
                loginButton.setText("");
                mProgressBar.setVisibility(View.VISIBLE);
                user.signInWithEmailAndPassword(email, passwordEditText.getText().toString(), this);
                break;

            case R.id.restore_password_textview:

                    user.sendPasswordResetEmail(email, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful())
                                Snackbar.make(findViewById(android.R.id.content),
                                    LoginActivity.this.getString(R.string.email_has_been_sent),
                                    Snackbar.LENGTH_LONG).show();
                        }
                    });
                break;

            case R.id.singup_textview:
                Intent singUpIntent = new Intent(this, SignUpActivity.class);
                startActivity(singUpIntent);
                finish();
                break;
        }
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (!task.isSuccessful()) {
            mProgressBar.setVisibility(View.GONE);
            loginButton.setText("Sign IN");
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), this.getString(R.string.login_failed), Snackbar.LENGTH_LONG);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            snack.show();
        } else {
            Toast.makeText(getApplicationContext(), this.getString(R.string.login_is_successful), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
