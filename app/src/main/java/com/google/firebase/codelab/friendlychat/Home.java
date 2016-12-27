package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;


public class Home extends AppCompatActivity {
    public UserProfile userProfile;
    public TextView txt_distance;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.home_menu);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO(Home.this);
        userProfile = profileIO.ReadFile();

        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);



        ImageButton ischedule = (ImageButton) findViewById(R.id.ischedule);
        ischedule.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Home.this, schedule.class);
                startActivity(intent);
                Home.this.finish();
            }
        });

        ImageButton italk = (ImageButton) findViewById(R.id.italk);
        italk.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Home.this, MainActivity.class);
                startActivity(intent);
                Home.this.finish();
            }
        });

        //  the activity for setting userprofile and requesting friend by id
//        Button btn_text = (Button) findViewById(R.id.btn_test);
//        btn_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent2 = new Intent();
//                intent2.setClass(Home.this, SearchUser.class);
//                startActivity(intent2);
//            }
//        });


//        Button btn_notification = (Button) findViewById(R.id.btn_notification);
//        btn_notification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent2 = new Intent();
//                intent2.setClass(Home.this, Notification.class);
//                startActivity(intent2);
//            }
//        });

        Button btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent2 = new Intent();
                intent2.setClass(Home.this, Run.class);
                startActivity(intent2);
                //Home.this.finish();
            }
        });

        // show the all records when click the button
        Button btn_record = (Button) findViewById(R.id.btn_record);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(Home.this, Record.class);
                startActivity(intent2);
            }
        });
        // 設定自己可以接收好友通知
        SendNotification sendNotification = new SendNotification();
        sendNotification.subscribeFriend(userProfile.getUserid());





        Button btn_delete = (Button) findViewById(R.id.deleteALL);

//        Button btn_friend = (Button) findViewById(R.id.btn_friend);
//        btn_friend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent2 = new Intent();
//                intent2.setClass(Home.this, FriendList.class);
//                startActivity(intent2);
//            }
//        });
/*
        Button btn_delete = (Button) findViewById(R.id.deleteALL);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("UserProfile").orderByChild("instanceid").addListenerForSingleValueEvent(new ValueEventListener() {
                //mDatabase.child("UserProfile").orderByChild("instanceid").equalTo(refreshedToken).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dataSnapshot.getRef().removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
*/
        txt_distance = (TextView)findViewById(R.id.txt_homeditatnce);
        updateDistance();
    }
    private void addDrawerItems() {
        String[] osArray = { "Android", "iOS", "Windows", "OS X", "Linux" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }


    private void updateDistance(){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // get the userProfile by userid
            mDatabase.child("UserProfile").orderByChild("userid").equalTo(userProfile.getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

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
