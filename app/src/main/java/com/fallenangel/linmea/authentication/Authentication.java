package com.fallenangel.linmea.authentication;

import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by NineB on 9/2/2017.
 */

public abstract class Authentication extends AppCompatActivity {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    protected abstract FirebaseUser UpdateUI (FirebaseUser user);

    private void updUI(FirebaseUser user){
        mAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseUser user = mAuth.getCurrentUser();
                UpdateUI (user);
            }
        });
    }

}
