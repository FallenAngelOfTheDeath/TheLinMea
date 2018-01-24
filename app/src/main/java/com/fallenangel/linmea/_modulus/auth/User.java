package com.fallenangel.linmea._modulus.auth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.fallenangel.linmea._linmea.model.UserModel;
import com.fallenangel.linmea.linmea.user.utils.Validations;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Created by NineB on 9/29/2017.
 */
public class User {

    private FirebaseAuth mAuth;


    public User(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    //static User instance;

//    public static User getInstance(){
//        return instance;
//    }
//
//    public synchronized static User getInstance(){
//        if (instance == null){
//            instance = new User(FirebaseAuth.getInstance());
//        }
//        return instance;
//    }


//
//    public User() {
//    }
//
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

    public Boolean updateUserName(String name){
        final Boolean[] bool = {false};
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        getCurrUser()
                .updateProfile(profileUpdates)
                .addOnCompleteListener((OnCompleteListener) task -> {
                    if (task.isSuccessful())
                        bool[0] = true;
                    else
                        bool[0] = false;
                });
        return bool[0];
    }

    public Boolean updateUserEmail(String email){
        final Boolean[] bool = {false};
        getCurrUser()
                .updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            bool[0] = true;
                        else
                            bool[0] = false;
                    }
                });
        return bool[0];
    }

    public Boolean sendEmailVerification(){
        final Boolean[] bool = {false};
        getCurrUser()
                .sendEmailVerification()
                .addOnCompleteListener((OnCompleteListener) task -> {
                    if (task.isSuccessful())
                        bool[0] = true;
                    else
                        bool[0] = false;
                });
        return bool[0];
    }

    public Boolean updatePassword(String newPassword){
        final Boolean[] bool = {false};
        getCurrUser()
                .updatePassword(newPassword)
                .addOnCompleteListener((OnCompleteListener) task -> {
                    if (task.isSuccessful())
                        bool[0] = true;
                    else
                        bool[0] = false;
                });
        return bool[0];
    }

    public void sendPasswordResetEmail(String email, OnCompleteListener mOnCompleteListener){
       mAuth
           .sendPasswordResetEmail(email)
           .addOnCompleteListener(task -> mOnCompleteListener.onComplete(task));
    }

    public void deleteUser(OnCompleteListener mOnCompleteListener){
        getCurrUser().delete().addOnCompleteListener(task -> mOnCompleteListener.onComplete(task));
    }


    public interface OnUserComplete{
        void onComplete(@NonNull Task task);
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

    public static void logIn(){

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













//    public void singing (String email, String password){
//        FirebaseAuth.getInstance();
//
//        Observable<FirebaseAuth.>
//
//
//
//
//        final Thread[] thread = new Thread[1];
//       FirebaseAuth.getInstance() = mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//
//                    if (User.getCurrentUser() != null){
//                        thread[0] = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                UpdateUserData.getUserDataFromFire(LoginActivity.this);
//                            }
//                        });
//                    }
//
//                    Toast.makeText(getApplicationContext(), "login is successful", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//    }







}
