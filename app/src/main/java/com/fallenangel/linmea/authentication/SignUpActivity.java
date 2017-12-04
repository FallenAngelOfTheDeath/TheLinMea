package com.fallenangel.linmea.authentication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._linmea.ui.MainActivity;
import com.fallenangel.linmea.linmea.user.authentication.User;
import com.fallenangel.linmea.linmea.utils.image.Blur;
import com.fallenangel.linmea.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import static com.fallenangel.linmea.linmea.user.authentication.User.getCurrentUser;
import static com.fallenangel.linmea.linmea.user.authentication.User.getCurrentUserUID;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference  mDatabaseReference = FirebaseDatabase.getInstance().getReference();


    private LinearLayout mLayout;

    private Button singupButton;
    private EditText emailEditText, password1EditText, password2EditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        implementUI();

    }
    private void implementUI(){
        mLayout = (LinearLayout) findViewById(R.id.sign_up_layout);

        singupButton = (Button) findViewById(R.id.btn_singup);
        singupButton.setOnClickListener(this);

        emailEditText = (EditText) findViewById(R.id.email_singup);
        password1EditText = (EditText) findViewById(R.id.password_singup_1);
        password2EditText = (EditText) findViewById(R.id.password_singup_2);

        mLayout.setBackgroundResource(R.drawable.background);
        Blur.applyBlur(mLayout, SignUpActivity.this);
    }



    @Override
    public void onClick(View v) {



        Utils.hideKeyboard(this);

        String email = emailEditText.getText().toString();
        String password1 = password1EditText.getText().toString();
        String password2 = password2EditText.getText().toString();

        ValidationOfAuth validate = new ValidationOfAuth();

        if (!validate.validateEmail(email)) {
            Log.i(String.valueOf(getText(R.string.LOG_TAG_AUTH)), this.getString(R.string.incorrectemail));
            Toast.makeText(getApplicationContext(), this.getString(R.string.incorrectemail), Toast.LENGTH_SHORT).show();
        } else {
            if (!validate.validatePassword(password1) | !validate.passwordComparison(password1, password2)) {
                Log.i(String.valueOf(getText(R.string.LOG_TAG_AUTH)), this.getString(R.string.incorrectpassword));
                Toast.makeText(getApplicationContext(), this.getString(R.string.incorrectpassword), Toast.LENGTH_SHORT).show();
            } else {
                registration(email, password1);
                Log.i(String.valueOf(getText(R.string.LOG_TAG_AUTH)), this.getString(R.string.registrationissuccessful));
                Toast.makeText(getApplicationContext(), this.getString(R.string.registrationissuccessful), Toast.LENGTH_SHORT).show();

                AlertDialog.Builder adb = new AlertDialog.Builder(SignUpActivity.this);
                adb.setTitle("Limited account");
                adb.setMessage("Your account is limited, you can add information about yourself and confirm your account now or at any time");
                adb.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        putMainMetadata();
                    }
                });
                adb.setNegativeButton("Skip, not now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                adb.show();

            }
        }
    }

    private void registration (final String email, final String password){
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

    private void putMainMetadata () {
        Log.i("0000000", "putMainMetadata: " + User.getCurrentUser().getUid()+" : "+ User.getCurrentUser().getProviderData());
        mDatabaseReference.child("user").child(User.getCurrentUserUID()).child("other_metadata").child("date_of_creation").setValue(ServerValue.TIMESTAMP);
        if (getCurrentUserUID() != null) {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(emailEditText.getText().toString().substring(0, emailEditText.getText().toString().indexOf("@"))).build();

            getCurrentUser().updateProfile(profileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseUser newUserData = mAuth.getCurrentUser();
                            if (task.isSuccessful()) {
                                Log.d(getString(R.string.LOG_TAG_AUTH), "User name after update is " + newUserData.getDisplayName());
                            } else {
                                Log.d(getString(R.string.LOG_TAG_AUTH), "User data is not updated");
                            }

                        }
                    });

                }
            });
        }

    }

}