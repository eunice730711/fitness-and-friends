package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.id;

public class FriendProfile extends AppCompatActivity {

    //Google account information
    //private AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
    public DatabaseReference mDatabase;
    private TextView txt_usergmail, txt_googlename, txt_usercity, txt_userbirthday, txt_userid, txt_usergender, txt_selfintroduction;
    private CircleImageView userImageView;
    private String friendid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            friendid = extras.getString("friendid");
        }

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
        //refreshedToken = FirebaseInstanceId.getInstance().getToken();
        getProfile(friendid);

    }

    private void getProfile(String id) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.getProfileById(id, new FirebaseData.Callback() {
            @Override
            public void getProfile(UserProfile profile) {
                //User information
                txt_usercity.setText(profile.getUsercity());
                txt_userbirthday.setText(profile.getUserbirthday());
                txt_usergender.setText(profile.getUsergender());
                txt_userid.setText(profile.getUserid());
                txt_selfintroduction.setText(profile.getSelfintroduction());
                txt_usergmail.setText(profile.getUseremail());
                txt_googlename.setText(profile.getUsername());
                //imageView
                Glide.with(FriendProfile.this)
                        .load(profile.getUserphoto())
                        .into(userImageView);
                Log.d("userid", profile.getUserid());

            }
        });
    }
}
