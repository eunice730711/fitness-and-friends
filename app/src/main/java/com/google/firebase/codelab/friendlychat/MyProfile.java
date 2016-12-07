package com.google.firebase.codelab.friendlychat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyProfile extends AppCompatActivity {
    //Google account information
    //private AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
    public DatabaseReference mDatabase;

    private static final String TAG = "MyProfile";
    public static final String ANONYMOUS = "anonymous";
    public static final String PROFILE_CHILD = "UserProfile";
    private static final int RC_SIGN_IN = 9001;

    private TextView txt_usergmail, txt_googlename, txt_usercity, txt_userbirthday, txt_userid, txt_usergender, txt_selfintroduction;
    private CircleImageView userImageView;
    private String refreshedToken;

//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        //--------------------------Get firebase information

        txt_usercity = (TextView) findViewById(R.id.txt_usercity);
        txt_userid = (TextView) findViewById(R.id.txt_userid);
        txt_usergender = (TextView) findViewById(R.id.txt_usergender);
        txt_userbirthday = (TextView) findViewById(R.id.txt_userbirthday);
        txt_selfintroduction = (TextView) findViewById(R.id.txt_selfintroduction);
        txt_usergmail = (TextView) findViewById(R.id.txt_usergmail);
        txt_googlename = (TextView) findViewById(R.id.txt_googlename);
        userImageView = (CircleImageView) findViewById(R.id.userImageView);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        refreshedToken = FirebaseInstanceId.getInstance().getToken();


        // get the userProfile by instanceId
        mDatabase.child("UserProfile").orderByChild("instanceid").equalTo(refreshedToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    //User information
                    txt_usercity.setText(userProfile.getUsercity());
                    txt_userbirthday.setText(userProfile.getUserbirthday());
                    txt_usergender.setText(userProfile.getUsergender());
                    txt_userid.setText(userProfile.getUserid());
                    txt_selfintroduction.setText(userProfile.getSelfintroduction());
                    txt_usergmail.setText(userProfile.getUseremail());
                    txt_googlename.setText(userProfile.getUsername());
                    //imageView
                    Glide.with(MyProfile.this)
                            .load(userProfile.getUserphoto())
                            .into(userImageView);
                    Log.d("userid", userProfile.getUserid());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}

