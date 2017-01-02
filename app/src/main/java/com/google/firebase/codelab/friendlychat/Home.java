package com.google.firebase.codelab.friendlychat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Home extends AppCompatActivity {
    public UserProfile userProfile;
    public TextView txt_distance;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference myprofile_ref;
    private DatabaseReference mDatabase;
    private List<String> JoinList;
    private JoinMessage Join;
    private boolean is_todayJoin;
    private Button btn_jointoday;

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

        mDrawerList = (ListView) findViewById(R.id.navList);
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


        final ImageButton ischedule = (ImageButton) findViewById(R.id.ischedule);
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

        //
        final Button btn_todayJoin = (Button) findViewById(R.id.btn_todayJoin);
        btn_todayJoin.setVisibility(View.INVISIBLE);
        is_todayJoin = false;
        final ValueEventListener Listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                HashMap map = (HashMap<String, String>) dataSnapshot.getValue(Object.class);
                String date = map.get("jdate").toString();
                String nowDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                if (date.equals(nowDate)) {
                    Join = dataSnapshot.getValue(JoinMessage.class);
                    btn_todayJoin.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference();
        myprofile_ref = FirebaseDatabase.getInstance().getReferenceFromUrl(userProfile.getUserref());
        myprofile_ref.child("Join_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot Snapshot) {
                JoinList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : Snapshot.getChildren()) {
                    JoinList.add(dataSnapshot.getKey());
                }
                for (int i = 0; i < JoinList.size(); i++) {
                    mDatabase.child("Join").child(JoinList.get(i)).addListenerForSingleValueEvent(Listener);
                    if (is_todayJoin) break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        btn_todayJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {
                LayoutInflater inflater = LayoutInflater.from(Home.this);
                final View v = inflater.inflate(R.layout.show_todayjoin, null);
                final TextView showDate = (TextView) (v.findViewById(R.id.show_date));
                final TextView showTime = (TextView) (v.findViewById(R.id.show_time));
                final TextView showPos = (TextView) (v.findViewById(R.id.show_pos));
                final TextView showtype = (TextView) (v.findViewById(R.id.show_type));
                showDate.setText(Join.getJdate());
                showTime.setText(Join.getJtime());
                showPos.setText(Join.getJpos());
                showtype.setText(Join.getText());
                new AlertDialog.Builder(Home.this)
                        .setTitle("Today Join")
                        .setView(v)
                        .show();
            }
        });
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
        txt_distance = (TextView) findViewById(R.id.txt_homeditatnce);
        updateDistance();
    }


    private void addDrawerItems() {
        String[] osArray = {"Android", "iOS", "Windows", "OS X", "Linux"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }


    private void updateDistance() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // get the userProfile by userid
        mDatabase.child("UserProfile").orderByChild("userid").equalTo(userProfile.getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userProfile = dataSnapshot.getValue(UserProfile.class);
                    Log.e("username", userProfile.getUsername());
                    String d = String.format("%.2f", userProfile.updateDistance(0));
                    txt_distance.setText(d + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


}
