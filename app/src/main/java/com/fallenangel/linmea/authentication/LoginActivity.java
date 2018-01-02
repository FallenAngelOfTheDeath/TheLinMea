package com.fallenangel.linmea.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.service.UpdateUserData;
import com.fallenangel.linmea._linmea.ui.MainActivity;
import com.fallenangel.linmea.linmea.user.authentication.User;
import com.fallenangel.linmea.linmea.utils.image.Blur;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private CoordinatorLayout mLayout;
    private ProgressBar mProgressBar;

    private Button loginButton;
    private EditText emailEditText, passwordEditText;
    private TextView mResetPasswordTextView, mSignUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        implementUI();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(String.valueOf(getText(R.string.LOG_TAG_AUTH)), "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(String.valueOf(getText(R.string.LOG_TAG_AUTH)), "onAuthStateChanged:signed_out");
                }
            }
        };
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

        emailEditText.setText("fallenangelofthedeath@gmail.com");
        passwordEditText.setText("947019sdk20");

    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        ValidationOfAuth validate = new ValidationOfAuth();

        switch (v.getId()){
            case R.id.btn_login:
                loginButton.setText("");
                mProgressBar.setVisibility(View.VISIBLE);
                hideKeyboard();
                if (!validate.validateEmail(email)) {
                    Toast.makeText(getApplicationContext(), "Not a valid email address!", Toast.LENGTH_SHORT).show();
                } else if (!validate.validatePassword(password)) {
                    Toast.makeText(getApplicationContext(), "Not a valid password!", Toast.LENGTH_SHORT).show();
                } else {
                    singing(email, password);
                }

                break;

            case R.id.restore_password_textview:
                if (email.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter a valid email address!", Toast.LENGTH_SHORT).show();
                } else if (!validate.validateEmail(email)){
                    Toast.makeText(getApplicationContext(), "Not a valid email address!", Toast.LENGTH_SHORT).show();
                } else mAuth.sendPasswordResetEmail(email);
                break;

            case R.id.singup_textview:
                Intent singUpIntent = new Intent(this, SignUpActivity.class);
                startActivity(singUpIntent);
                break;
        }
    }


    public void singing (String email, String password){
        final Thread[] thread = new Thread[1];
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    if (User.getCurrentUser() != null){
                        thread[0] = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                UpdateUserData.getUserDataFromFire(LoginActivity.this);
                            }
                        });
                    }

                    Toast.makeText(getApplicationContext(), "login is successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}
