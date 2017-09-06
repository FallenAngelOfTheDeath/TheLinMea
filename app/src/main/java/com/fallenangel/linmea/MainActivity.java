package com.fallenangel.linmea;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fallenangel.linmea.profile.ChangeProfileDataActivity;
import com.fallenangel.linmea.authentication.LoginActivity;
import com.fallenangel.linmea.profile.ProfileActivity;
import com.fallenangel.linmea.authentication.SignUpActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonLogin, buttonSingup, buttonProfile, buttonUpdateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLogin = (Button)findViewById(R.id.test_login_btn);
        buttonSingup = (Button)findViewById(R.id.test_singup_btn);
        buttonProfile = (Button)findViewById(R.id.profile_btn);
        buttonUpdateProfile = (Button)findViewById(R.id.update_profile_btn);

        buttonLogin.setOnClickListener(this);
        buttonSingup.setOnClickListener(this);
        buttonProfile.setOnClickListener(this);
        buttonUpdateProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.test_login_btn:
                Intent logInIntent = new Intent(this, LoginActivity.class);
                startActivity(logInIntent);
                break;
            case R.id.test_singup_btn:
                Intent singUpIntent = new Intent(this, SignUpActivity.class);
                startActivity(singUpIntent);
                break;
            case R.id.profile_btn:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileIntent);
                break;
            case R.id.update_profile_btn:
                Intent updateProfileIntent = new Intent(this, ChangeProfileDataActivity.class);
                startActivity(updateProfileIntent);
                break;
            default:
                break;
        }
    }
}
