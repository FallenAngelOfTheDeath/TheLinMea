package com.fallenangel.linmea.dictionaries.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.fallenangel.linmea.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.fallenangel.linmea.profile.UserMetaData.getUserUID;

public class AddCustomDictionaryActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mName, mDescription;
    private Spinner mEditable;
    private FloatingActionButton mConfirm;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    private ArrayAdapter<String> spinnerAdapter;

    private String[] trueFalse = {"true", "false"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_dictionary);
        implementAdapter();
        implementUI();
    }

    private void implementAdapter (){
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, trueFalse);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void implementUI (){
        mName = (EditText) findViewById(R.id.name_custom_dictionary);
        mDescription = (EditText) findViewById(R.id.description_custom_dictionary);
        mEditable = (Spinner)findViewById(R.id.editable_spinner);
        mConfirm = (FloatingActionButton) findViewById(R.id.add_dictionary_floating_action_button);
        mConfirm.setOnClickListener(this);

        mEditable.setAdapter(spinnerAdapter);
        mEditable.setSelection(1);
    }

    @Override
    public void onClick(View v) {
        createDictionary(getUserUID(), mName.getText().toString(), mEditable.getSelectedItem().toString(), mDescription.getText().toString());
    }


    private void createDictionary (String user, String dictionaryName, String editable, String description){
        Map<String, Object> newDict = new HashMap<String, Object>();
        newDict.put("editable", editable);
        if (!description.isEmpty()){
        newDict.put("description", description);
        }
        newDict.put("size", 0);
        databaseReference.child("custom_dictionary").child(user).child("meta_data").child(dictionaryName).setValue(newDict);
    }
}
