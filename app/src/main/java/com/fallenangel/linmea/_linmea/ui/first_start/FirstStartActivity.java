package com.fallenangel.linmea._linmea.ui.first_start;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fallenangel.linmea.R;
import com.fallenangel.linmea._modulus.main.ui.MainActivity;
import com.fallenangel.linmea._modulus.non.utils.LoadDefaultConfig;
import com.fallenangel.linmea._modulus.auth.ui.LoginActivity;
import com.fallenangel.linmea._modulus.auth.ui.SignUpActivity;
import com.fallenangel.linmea.linmea.utils.image.Blur;

public class FirstStartActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mSingUP, mSingIN, mAnonymously, mHello;
    private RelativeLayout mRelativeLayout;
    private LoadDefaultConfig mLoadDefaultConfig = new LoadDefaultConfig(this);

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start);
        mLoadDefaultConfig.loadDefaultCustomDictPageOne();
        mLoadDefaultConfig.loadDefaultCustomDictPageTwo();
        mLoadDefaultConfig.loadDefaultCustomDictSinglePage();
        mLoadDefaultConfig.loadDefaultMainSettings();
        implementUI();
    }

    private void implementUI(){
        mRelativeLayout = (RelativeLayout) findViewById(R.id.first_start_layout);
        mSingUP = (TextView) findViewById(R.id.first_start_sing_up);
        mSingIN = (TextView) findViewById(R.id.first_start_sing_in);
        mAnonymously = (TextView) findViewById(R.id.first_start_anonymously);
        mHello = (TextView) findViewById(R.id.hello);

        mSingUP.setOnClickListener(this);
        mSingIN.setOnClickListener(this);
        mAnonymously.setOnClickListener(this);

        mRelativeLayout.setBackgroundResource(R.drawable.background);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            Blur.applyBlur(mRelativeLayout, FirstStartActivity.this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.first_start_sing_in:
                Intent singInIntent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(singInIntent);
                finish();
                break;
            case R.id.first_start_sing_up:
                Intent singUpIntent = new Intent(v.getContext(), SignUpActivity.class);
                startActivity(singUpIntent);
                finish();
                break;
            case R.id.first_start_anonymously:
                Intent anonymouslyIntent = new Intent(v.getContext(), MainActivity.class);
                startActivity(anonymouslyIntent);
                finish();
                break;
        }
    }

}
