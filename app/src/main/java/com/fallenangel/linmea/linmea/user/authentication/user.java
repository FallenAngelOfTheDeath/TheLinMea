package com.fallenangel.linmea.linmea.user.authentication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.fallenangel.linmea.linmea.model.UserModel;
import com.fallenangel.linmea.linmea.user.utils.Validations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Created by NineB on 9/29/2017.
 */

public class user {

    public static FirebaseAuth getAuthInstanse () {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getCurrentUser () {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getCurrentUserUID () {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static boolean logIn (final Context context, FirebaseAuth firebaseAuth, String email, String firstPassword, String secondPassword){
        final boolean[] status = {false};
        Boolean bool = false;
        if (!Validations.validateEmail(email)) {
            Toast.makeText(context.getApplicationContext(), "Not a valid email address!", Toast.LENGTH_SHORT).show();
            status[0] = false;
        } else if (!Validations.passwordComparison(firstPassword, secondPassword)) {
            Toast.makeText(context.getApplicationContext(), "Passwords not equals", Toast.LENGTH_SHORT).show();
            status[0] = false;
        } else if (!Validations.validatePassword(firstPassword)) {
            Toast.makeText(context.getApplicationContext(), "Not a valid password!", Toast.LENGTH_SHORT).show();
            status[0] = false;
        } else {
            firebaseAuth.signInWithEmailAndPassword(email,firstPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context.getApplicationContext(), "login is successful", Toast.LENGTH_SHORT).show();
                        status[0] = false;
                    } else {
                        Toast.makeText(context.getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();
                        status[0] = false;
                    }
                }
            });
        }
        return bool = status[0];
    }

    public static void logOut (){
        if (user.getCurrentUser() != null) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
        }
    }

    public static void singUp (){

    }

    public static void resetPasswor (){

    }

    public static void verificadePassword (){

    }

    public static void changePassword () {

    }
    public static void confirmPassword () {

    }

    public static void getMainMetadata(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user").child(getCurrentUserUID()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel user = null;

                String mFirstName = (String) dataSnapshot.child("main_metadata").child("first_name").getValue();
                String mLastName = (String) dataSnapshot.child("main_metadata").child("last_name").getValue();
                String mBiography = (String) dataSnapshot.child("other_metadata").child("biography").getValue();
                Date mBirthday = (Date) dataSnapshot.child("other_metadata").child("birthday").getValue();
                String mCity = (String) dataSnapshot.child("other_metadata").child("city").getValue();
                Date mDateOfCreation = (Date) dataSnapshot.child("other_metadata").child("date_of_creation").getValue();

                user.setFirestName(mFirstName);
                user.setLastName(mLastName);
                user.setBiography(mBiography);
                user.setBiography(mBiography);
                user.setBirthday(mBirthday);
                user.setDateOfCreation(mDateOfCreation);
                user.setCity(mCity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getOtherMetadata(){

    }


}
