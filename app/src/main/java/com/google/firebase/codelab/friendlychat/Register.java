package com.google.firebase.codelab.friendlychat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class Register extends AppCompatActivity {

    public DatabaseReference mDatabase;

    public static final String ANONYMOUS = "anonymous";
    private Button btn_send, btn_setProfile;
    private EditText txt_userid, txt_username, txt_searchId, txt_usercity, txt_userbirthday, txt_selfintroduction;
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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("Registration");

        //-------------------------Get Google Account Information
        personName = ANONYMOUS;
        personEmail = ANONYMOUS;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();


        // Get google profile image
        //userImageView = (CircleImageView) findViewById(R.id.userImageView);

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
//                Glide.with(FriendProfile.this)
//                        .load(mFirebaseUser.getPhotoUrl())
//                        .into(userImageView);
            }
            else
            {
//                PhotoUrl = "http://bonniesomerville.nz/wp-content/uploads/2015/08/profile-icon.png";
//                userImageView.setImageDrawable(ContextCompat
//                        .getDrawable(FriendProfile.this,
//                                R.drawable.ic_account_circle_black_36dp));
            }
        }
//        txt_usergmail = (TextView) findViewById(R.id.txt_usergmail);
//        txt_googlename = (TextView) findViewById(R.id.txt_googlename);
//
//        txt_usergmail.setText(personEmail);
//        txt_googlename.setText(personName);

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

        btn_setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = txt_userid.getText().toString();
                String username= txt_username.getText().toString();
                String usercity = txt_usercity.getText().toString();
                String userbirthday = txt_userbirthday.getText().toString();
                String usergender = txt_usergender.getText().toString();
                String selfintroduction = txt_selfintroduction.getText().toString();
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                // Throw data to UserProfile
                UserProfile test = new UserProfile(personName, userid,refreshedToken, usercity, userbirthday, usergender, selfintroduction, personEmail, PhotoUrl);

                // Write file in local
                profileIO = new ProfileIO(Register.this);
                profileIO.WriteFile(test);
                // Read user profile from file!
                // UserProfile read = new UserProfile();
                // read = profileIO.ReadFile();

                // Upload data on firebase
                mDatabase.child("UserProfile").push().setValue(test);
                // Move on to the next page(Home)
                Intent intent2 = new Intent();
                intent2.setClass(Register.this, Home.class);
                startActivity(intent2);
                Register.this.finish();
            }
        });

    }

    private void init(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btn_send = (Button) findViewById(R.id.btn_search_send);
        btn_setProfile = (Button) findViewById(R.id.btn_setProfile);
        txt_userid = (EditText) findViewById(R.id.txt_id);
        txt_username = (EditText) findViewById(R.id.txt_username);
        txt_searchId = (EditText) findViewById(R.id.txt_searchId);
        txt_usercity = (EditText) findViewById(R.id.txt_usercity);
        txt_userbirthday = (EditText) findViewById(R.id.txt_userbirthday);
        txt_selfintroduction = (EditText) findViewById(R.id.txt_selfintroduction);
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
