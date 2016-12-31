package com.google.firebase.codelab.friendlychat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;


public class Register extends AppCompatActivity {

    public DatabaseReference mDatabase;

    public static final String ANONYMOUS = "anonymous";
    private Button btn_send, btn_setProfile;
    private EditText txt_userid, txt_username, txt_searchId, txt_usercity, txt_userbirthday, txt_selfintroduction, txt_userheight, txt_userweight;
    private TextView txt_usergender;
    private String personEmail, personId, PhotoUrl, personName;
    private RadioGroup usergender;
    private RadioButton checkedRadioButton;
    public UserProfile userProfile;
    private String refreshedToken;
    private ProfileIO profileIO;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//        myToolbar.setTitle("Registration");

        //-------------------------Get Google Account Information
        personName = ANONYMOUS;
        personEmail = ANONYMOUS;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();


        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            return;
        } else {
            // Get Google account information: Name, gmail and photo
            personName = mFirebaseUser.getDisplayName();
            personEmail = mFirebaseUser.getEmail();
            if (mFirebaseUser.getPhotoUrl() != null) {
                PhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
        //-----------------------google profile information finish
        init();

        usergender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);

                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    // Changes the textview's text to "Checked: example radiobutton text"
                    txt_usergender.setText(checkedRadioButton.getText());
                }
            }
        });
        Log.i("DEBUG ", "1");

        btn_setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = txt_userid.getText().toString();
                //String username= txt_username.getText().toString();
                String usercity = txt_usercity.getText().toString();
                String userbirthday = txt_userbirthday.getText().toString();
                String usergender = txt_usergender.getText().toString();
                String userheight = txt_userheight.getText().toString();
                String userweight = txt_userweight.getText().toString();
                String selfintroduction = txt_selfintroduction.getText().toString();
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();


                Log.i("DEBUG ", "2");

                // Throw data to UserProfile
                final UserProfile test = new UserProfile(personName, userid,refreshedToken, usercity, userbirthday, usergender, selfintroduction, personEmail, PhotoUrl, "", userheight, userweight);

                // Upload data on firebase
                mDatabase.child("UserProfile").push().setValue(test);

                mDatabase.child("UserProfile")
                        .orderByChild("userid")
                        .addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                String ref = "";

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Object o  = dataSnapshot.getValue(Object.class);
                                    String userid = ((HashMap<String,String>)o).get("userid");
                                    if( userid.equals(userid)) {
                                        ref = dataSnapshot.getRef().toString();
                                        break;
                                    }
                                }
                                //Log.e(TAG, ref);

                                // Write file in local
                                test.setUserref(ref);
                                profileIO = new ProfileIO(Register.this);
                                profileIO.WriteFile(test);
                                // Read user profile from file!
                                // UserProfile read = new UserProfile();
                                // read = profileIO.ReadFile();


                                // Move on to the next page(Home)
                                Intent intent2 = new Intent();
                                intent2.setClass(Register.this, Home.class);
                                startActivity(intent2);
                                Register.this.finish();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
            }
        });

    }

    private void init(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //btn_send = (Button) findViewById(R.id.btn_search_send);
        btn_setProfile = (Button) findViewById(R.id.btn_setProfile);
        txt_userid = (EditText) findViewById(R.id.txt_id);
        //txt_username = (EditText) findViewById(R.id.txt_username);
        //txt_searchId = (EditText) findViewById(R.id.txt_searchId);
        txt_usercity = (EditText) findViewById(R.id.txt_usercity);
        txt_userbirthday = (EditText) findViewById(R.id.txt_userbirthday);
        txt_selfintroduction = (EditText) findViewById(R.id.txt_selfintroduction);
        txt_userheight = (EditText) findViewById(R.id.txt_userheight);
        txt_userweight = (EditText) findViewById(R.id.txt_userweight);
        usergender = (RadioGroup) findViewById(R.id.usergender);
        txt_usergender = (TextView) findViewById(R.id.showgender);
        checkedRadioButton = (RadioButton)usergender.findViewById(usergender.getCheckedRadioButtonId());
        refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //getUserProfile();
    }

//    private  void getUserProfile(){
//        // get the userProfile by instanceId
//        mDatabase.child("UserProfile").orderByChild("instanceid").equalTo(refreshedToken).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    userProfile = dataSnapshot.getValue(UserProfile.class);
//                    Log.d("userid",userProfile.getUserid());
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }


}
