package com.fallenangel.linmea;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fallenangel.linmea.authentication.LoginActivity;
import com.fallenangel.linmea.authentication.SignUpActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonLogin, buttonSingup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLogin = (Button)findViewById(R.id.test_login_btn);
        buttonSingup = (Button)findViewById(R.id.test_singup_btn);

        buttonLogin.setOnClickListener(this);
        buttonSingup.setOnClickListener(this);
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
            default:
                break;
        }
    }
}
