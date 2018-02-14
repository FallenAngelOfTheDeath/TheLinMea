/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.auth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.fallenangel.linmea._modulus.non.utils.Validations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by NineB on 9/29/2017.
 */
public class User {

    private FirebaseAuth mAuth;


    public User(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public FirebaseUser getCurrUser () {
        return mAuth.getCurrentUser();
    }

    public String getCurrUserUid () {
        return mAuth.getCurrentUser().getUid();
    }

    public Boolean isNull(){
        if (getCurrUser() != null)
            return false;
        else
            return true;
    }

    public void createUserWithEmailAndPassword(String email, String password, OnCompleteListener mOnCompleteListener){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mOnCompleteListener.onComplete(task);
                    }
                });
    }

    public void signOut (){
        if (getCurrUser() != null) {
            mAuth.signOut();
        }
    }

    public void signInWithEmailAndPassword(String email, String password, OnCompleteListener mOnCompleteListener){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mOnCompleteListener.onComplete(task);
            }
        });

    }

    public void updateUserName(String name, OnCompleteListener mOnCompleteListener){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        getCurrUser()
                .updateProfile(profileUpdates)
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mOnCompleteListener.onComplete(task);
                    }
                });
    }

    public void updateUserEmail(String password, String email, String currentEmail, OnCompleteListenerWithCredit onCompleteListenerWithCredit){
        AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);
        getCurrUser().reauthenticate(credential).addOnCompleteListener(creditTask -> {
            if (creditTask.isSuccessful())
                getCurrUser().updateEmail(email).addOnCompleteListener(task -> onCompleteListenerWithCredit.onComplete(task, creditTask));
            else
                onCompleteListenerWithCredit.onCreditError(creditTask);
        });
    }

    public void sendEmailVerification(){
        getCurrUser()
                .sendEmailVerification();
    }

    public void updatePassword(String oldPassword, String newPassword, OnCompleteListener mOnCompleteListener){
        AuthCredential credential = EmailAuthProvider.getCredential(getCurrUser().getEmail(), oldPassword);
        getCurrUser().reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                getCurrUser()
                        .updatePassword(newPassword)
                        .addOnCompleteListener(mOnCompleteListener);
        });

    }

    public void sendPasswordResetEmail(String email, OnCompleteListener mOnCompleteListener){
       mAuth
           .sendPasswordResetEmail(email)
           .addOnCompleteListener(task -> mOnCompleteListener.onComplete(task));
    }

    public void deleteUser(String currentEmail, String password, OnCompleteListenerWithCredit onCompleteListenerWithCredit){
        AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, password);
        getCurrUser().reauthenticate(credential).addOnCompleteListener(creditTask -> {
            if (creditTask.isSuccessful())
                getCurrUser().delete().addOnCompleteListener(task -> onCompleteListenerWithCredit.onComplete(task, creditTask));
            else
                onCompleteListenerWithCredit.onCreditError(creditTask);
        });
    }

    public interface OnCompleteListenerWithCredit{
        void onComplete(@NonNull Task<Void> task, @NonNull Task<Void> creditTask);
        void onCreditError(@NonNull Task<Void> creditTask);
    }












    public static FirebaseAuth getAuthInstanse () {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseUser getCurrentUser () {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getCurrentUserUID () {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static void logOut (){
        if (User.getCurrentUser() != null) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
        }
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

}
