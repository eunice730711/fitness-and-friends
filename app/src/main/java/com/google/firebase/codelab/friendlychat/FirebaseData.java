package com.google.firebase.codelab.friendlychat;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.id;

/**
 * Created by user on 2016/12/9.
 */

public class FirebaseData {
    public DatabaseReference mDatabase;
    public FirebaseData(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        }

    //用callback interface
    public interface Callback {
        //這個是找完firebase後才會做的函式
        void getProfile(UserProfile userProfile);
    }
    public void getProfileById(String id ,final Callback callback) {

        mDatabase.child("UserProfile").orderByChild("userid").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserProfile profile = dataSnapshot.getValue(UserProfile.class);
                    callback.getProfile(profile);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
}
