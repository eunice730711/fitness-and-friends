package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private String personEmail, personId, PhotoUrl, personName;
    private Uri personPhoto;
    private CircleImageView userImageView;
    private String refreshedToken;

    // Firebase instance variables
    //private Firebase mRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, ProfileViewHolder>
            mFirebaseAdapter;

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameView;
        public TextView userBirthdayView;
        public TextView userGenderView;
        public TextView userCityView;


        public ProfileViewHolder(View v) {
            super(v);
            userNameView = (TextView) itemView.findViewById(R.id.messageTextView);
            userBirthdayView = (TextView) itemView.findViewById(R.id.messengerTextView);
            userGenderView = (TextView) itemView.findViewById(R.id.mdateTextView);
            userCityView = (TextView) itemView.findViewById(R.id.mtimeTextView);;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        //-------------------------Get Google Account Information
        personName = ANONYMOUS;
        personEmail = ANONYMOUS;
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        userImageView = (CircleImageView) findViewById(R.id.userImageView);

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
                Glide.with(MyProfile.this)
                        .load(mFirebaseUser.getPhotoUrl())
                        .into(userImageView);
            }
            else
            {
                userImageView.setImageDrawable(ContextCompat
                        .getDrawable(MyProfile.this,
                                R.drawable.ic_account_circle_black_36dp));
            }
        }
        txt_usergmail = (TextView) findViewById(R.id.txt_usergmail);
        txt_googlename = (TextView) findViewById(R.id.txt_googlename);

        txt_usergmail.setText(personEmail);
        txt_googlename.setText(personName);


        //--------------------------Get firebase information
        txt_usercity = (TextView) findViewById(R.id.txt_usercity);
        txt_userid = (TextView) findViewById(R.id.txt_userid);
        txt_usergender = (TextView) findViewById(R.id.txt_usergender);
        txt_userbirthday = (TextView) findViewById(R.id.txt_userbirthday);
        txt_selfintroduction = (TextView) findViewById(R.id.txt_selfintroduction);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        refreshedToken = FirebaseInstanceId.getInstance().getToken();


        // get the userProfile by instanceId
        mDatabase.child("UserProfile").orderByChild("instanceid").equalTo(refreshedToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    txt_usercity.setText(userProfile.getUsercity());
                    txt_userbirthday.setText(userProfile.getUserbirthday());
                    txt_usergender.setText(userProfile.getUsergender());
                    txt_userid.setText(userProfile.getUserid());
                    txt_selfintroduction.setText(userProfile.getSelfintroduction());
                    Log.d("userid", userProfile.getUserid());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}

