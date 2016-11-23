package com.google.firebase.codelab.friendlychat;


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

        //

        btn_setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = txt_userid.getText().toString();
                String username= txt_username.getText().toString();
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                UserProfile test = new UserProfile(username, userid,refreshedToken);
                mDatabase.child("UserProfile").push().setValue(test);
            }
        });

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
                            Toast.makeText(SearchUser.this, "該 ID 不存在", Toast.LENGTH_LONG).show();

                        }
                        else{
                            // if the user account exists
                            String userId = txt_searchId.getText().toString();

                            // 送出邀請
                            mDatabase.child("RequestFriend").orderByChild("user").equalTo(userId).getRef()
                                    .equalTo(userProfile.getUserid()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    // 避免重複送出邀請
                                    if ( snapshot.exists())
                                        Toast.makeText(SearchUser.this, "已發送過邀請，請等待對方確認", Toast.LENGTH_LONG).show();
                                    else{
                                        // 送出邀請
                                        String userId = txt_searchId.getText().toString();
                                        RequestFriend requestFriend = new RequestFriend(userId, userProfile.getUserid());
                                        mDatabase.child("RequestFriend").push().setValue(requestFriend);
                                        Toast.makeText(SearchUser.this, "好友邀請已送出", Toast.LENGTH_LONG).show();
                                    }
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
