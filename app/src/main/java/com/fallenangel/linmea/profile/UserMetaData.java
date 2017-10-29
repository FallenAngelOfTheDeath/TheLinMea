package com.fallenangel.linmea.profile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by NineB on 9/12/2017.
 */

public class UserMetaData {

    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference reference = mFirebaseStorage.getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    public static FirebaseUser getCurrentUser (){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getUserUID (){
        if (getCurrentUser() == null){
            return null;
        }
        return getCurrentUser().getUid();
    }

//    public static void getMD5ImagesFromFireBase (String name, final OnResultListener<String> result){
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Avatars/" + name + ".jpg");
//        storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
//            @Override
//            public void onSuccess(StorageMetadata storageMetadata) {
//                result.onSuccess(storageMetadata.getMd5Hash());
//            }
//        }).addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
//            @Override
//            public void onComplete(@NonNull Task<StorageMetadata> task) {
//                result.onComplete(task.getResult().getMd5Hash());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }

//    public static void saveCurrentFireMD5OfImageFromServer (final Activity activity, String uid, final String key) {
//        getMD5ImagesFromFireBase(uid, new OnResultListener<String>() {
//            @Override
//            public String onComplete(String result) {
//                SharedPreferencesUtils.putToSharedPreferences(activity, key,getUserUID(), result);
//                Log.i(activity.getString(R.string.LOG_TAG_USER_PROFILE),key + " : " + result);
//                return result;
//            }
//
//            @Override
//            public String onSuccess(String result) {
//                return result;
//            }
//
//            @Override
//            public void onFailure(Throwable error) {
//
//            }
//        });
//    }
}
