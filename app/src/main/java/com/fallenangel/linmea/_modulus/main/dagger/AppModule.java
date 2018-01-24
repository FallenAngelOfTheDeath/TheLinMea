package com.fallenangel.linmea._modulus.main.dagger;

import android.content.Context;

import com.fallenangel.linmea._linmea.model.DictionaryCustomizer;
import com.fallenangel.linmea._modulus.auth.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by NineB on 1/18/2018.
 */

@Module
public class AppModule {

    @Provides
    User providesUser(FirebaseAuth firebaseAuth){
        return new User(firebaseAuth);
    }

    @Provides
    DictionaryCustomizer providesDictionaryCustomizer(Context context){
        return new DictionaryCustomizer(context);
    }

    @Provides
    @Singleton
    FirebaseAuth providesAuth(){
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    DatabaseReference providesDatabaseReference(){
        return FirebaseDatabase.getInstance().getReference();
    }
}
