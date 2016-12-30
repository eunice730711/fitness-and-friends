package com.google.firebase.codelab.friendlychat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Loading extends AppCompatActivity {

    // This page: Check if user already register (Loading page...)

    public DatabaseReference mDatabase;
    public static final String ANONYMOUS = "anonymous";
    private String personEmail;
    public UserProfile userProfile;
    private ProfileIO profileIO;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //-------------------------Get Google Account Information
        personEmail = ANONYMOUS;
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        ProfileIO existprofileIO = new ProfileIO(Loading.this);

        // Check users register
        if(existprofileIO.ReadFile() != null)
        {
            // The profile exist on phone
            userProfile = existprofileIO.ReadFile();
            Intent intent = new Intent();
            intent.setClass(Loading.this, Home.class);
            startActivity(intent);
            Loading.this.finish();
        }

        // ELSE: The profile NOT exist on phone --> check whether it is on Firebase
        // Step 1 : get user google information
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            return;
        } else {
            // Get Google account information: Name, gmail and photo
            personEmail = mFirebaseUser.getEmail();
        }

        // Step 2: get the userProfile in firebase by Gmail
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("UserProfile").orderByChild("useremail").equalTo(personEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if( !snapshot.exists()){
                    //User do NOT exist on firebase --> new user --> need register
                    //Toast.makeText(Loading.this, "Cannot find Email", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClass(Loading.this, Register.class);
                    startActivity(intent);
                    Loading.this.finish();
                }
                else{
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        // User do exist on firebase --> get user information on firebase
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        // write user information to file
                        //profileIO = new ProfileIO(Loading.this);
                        //profileIO.WriteFile(userProfile);
                        Intent intent = new Intent();
                        intent.setClass(Loading.this, Home.class);
                        startActivity(intent);
                        Loading.this.finish();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
