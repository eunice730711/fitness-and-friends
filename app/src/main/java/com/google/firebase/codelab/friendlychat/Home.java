package com.google.firebase.codelab.friendlychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.google.firebase.codelab.friendlychat.R.id.home;

public class Home extends AppCompatActivity {
    public UserProfile userProfile;
    public TextView txt_distance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Button buttonTalk = (Button) findViewById(R.id.buttonTalk);
        buttonTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(Home.this, MainActivity.class);
                startActivity(intent2);
                Home.this.finish();
            }
        });

        Button buttonSchedule = (Button) findViewById(R.id.buttonSchedule);
        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(Home.this, schedule.class);
                startActivity(intent2);
                Home.this.finish();
            }
        });
        //  the activity for setting userprofile and requesting friend by id
        Button btn_text = (Button) findViewById(R.id.btn_test);
        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(Home.this, SearchUser.class);
                startActivity(intent2);
            }
        });


        Button btn_notification = (Button) findViewById(R.id.btn_notification);
        btn_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(Home.this, Notification.class);
                startActivity(intent2);
            }
        });

        Button btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(Home.this, Running.class);
                startActivity(intent2);
            }
        });

        Button btn_friend = (Button) findViewById(R.id.btn_friend);
        btn_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(Home.this, FriendList.class);
                startActivity(intent2);
            }
        });
        txt_distance = (TextView)findViewById(R.id.txt_homeditatnce);
        updateDistance();
    }

    private void updateDistance(){
        getUserProfile();
    }
    private void getUserProfile() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // get the userProfile by instanceId
        mDatabase.child("UserProfile").orderByChild("instanceid").equalTo(refreshedToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                /*
                userProfile = snapshot.getValue(UserProfile.class);
                Log.e("username", userProfile.getUsername());
                txt_distance.setText(userProfile.updateDistance(0)+" M");
*/

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userProfile = dataSnapshot.getValue(UserProfile.class);
                    Log.e("username", userProfile.getUsername());
                    txt_distance.setText(userProfile.updateDistance(0)+" M");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
