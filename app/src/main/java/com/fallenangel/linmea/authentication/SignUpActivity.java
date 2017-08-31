package com.fallenangel.linmea.authentication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button singupButton;
    private EditText emailEditText, password1EditText, password2EditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        singupButton = (Button) findViewById(R.id.btn_singup);
        singupButton.setOnClickListener(this);

        emailEditText = (EditText) findViewById(R.id.email_singup);
        password1EditText = (EditText) findViewById(R.id.password_singup_1);
        password2EditText = (EditText) findViewById(R.id.password_singup_2);
    }

    @Override
    public void onClick(View v) {
        Utils utils = new Utils();
        utils.hideKeyboard();

        String email = emailEditText.getText().toString();
        String password1 = password1EditText.getText().toString();
        String password2 = password2EditText.getText().toString();

        ValidationOfAuth validate = new ValidationOfAuth();

        if (!validate.validateEmail(email)) {
            Toast.makeText(getApplicationContext(), "Not a valid email address!", Toast.LENGTH_SHORT).show();
        } else if (!validate.validatePassword(password1)) {
            Toast.makeText(getApplicationContext(), "Not a valid password!", Toast.LENGTH_SHORT).show();
        } else  if (!validate.passwordComparison(password1, password2)){
            Log.d(String.valueOf(getText(R.string.LOG_TAG_AUTH)), "password do not match");
            Toast.makeText(getApplicationContext(), "password do not match", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "email and password is valid!", Toast.LENGTH_SHORT).show();
            singup(email, password1);
        }
    }

    private void singup (String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "registration is successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
