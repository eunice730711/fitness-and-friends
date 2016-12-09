package com.google.firebase.codelab.friendlychat;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;


public class SearchUser extends AppCompatActivity {

    public DatabaseReference mDatabase;
    private Button btn_send, btn_setProfile;
    private EditText txt_userid, txt_username, txt_searchId;
    public UserProfile userProfile;
    private String refreshedToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();

        // send requesting friend by ID
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // retrieve data olny one time
                String userId = txt_searchId.getText().toString();
                mDatabase.child("UserProfile").orderByChild("userid").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // do some stuff once

                        // check if it doesn't exsit
                        if( !snapshot.exists()){
                            Toast.makeText(SearchUser.this, "Cannot find ID", Toast.LENGTH_LONG).show();

                        }
                        else{
                            // if the user account exists
                            String userId = txt_searchId.getText().toString();

                            // 送出邀請
                            mDatabase.child("RequestFriend").child(userId).getRef()
                                    .orderByChild("requester").equalTo(userProfile.getUserid()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    // 避免重複送出邀請
                                    String userId2 = txt_searchId.getText().toString();
                                    // 傳送好友ID 到好友頁面
                                    Intent intent = new Intent(SearchUser.this, FriendProfile.class);
                                    intent.putExtra("friendid", userId2);
                                    startActivity(intent);

/*
                                    if ( snapshot.exists())
                                        Toast.makeText(SearchUser.this, "已發送過邀請，請等待對方確認", Toast.LENGTH_SHORT).show();
                                    else{
                                        // 送出邀請
                                        String userId = txt_searchId.getText().toString();
                                        HashMap<String,String> map = new HashMap<String, String>();
                                        map.put("requester",userProfile.getUserid());
                                        mDatabase.child("RequestFriend").child(userId).push().setValue(map);
                                        Toast.makeText(SearchUser.this, "Friend request has been sent", Toast.LENGTH_SHORT).show();
                                    }
*/
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
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
        refreshedToken = FirebaseInstanceId.getInstance().getToken();

        getUserProfile();
    }

    private  void getUserProfile(){
        // get the userProfile by instanceId
        mDatabase.child("UserProfile").orderByChild("instanceid").equalTo(refreshedToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userProfile = dataSnapshot.getValue(UserProfile.class);
                    Log.d("userid",userProfile.getUserid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



}
