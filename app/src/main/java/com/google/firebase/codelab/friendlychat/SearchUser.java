package com.google.firebase.codelab.friendlychat;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.util.Log.e;


public class SearchUser extends AppCompatActivity {

    public DatabaseReference mDatabase;
    private Button btn_send, btn_find;
    private EditText txt_userid, txt_username, txt_searchId;
    public UserProfile userProfile;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Object, RecommendFriendViewHolder>
            mFirebaseAdapter;

    public static class RecommendFriendViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView ImageView;
        public String friendid;
        public TextView txt_recommendId;
        public Button btn_friendProfile;

        public RecommendFriendViewHolder(View v) {
            super(v);
            txt_recommendId = (TextView) itemView.findViewById(R.id.txt_recommendId);
            ImageView = (CircleImageView) itemView.findViewById(R.id.recommedFriendView);
            btn_friendProfile = (Button) itemView.findViewById(R.id.btn_recommendfriendProfile);

            btn_friendProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 好友的個人頁面介紹
                    Intent i = new Intent(v.getContext(), SearchProfile.class);
                    i.putExtra("friendid", friendid);
                    v.getContext().startActivity(i);

                }
            });
        }
    }
    /*
    @Override
    protected  void onPause(){
        mFirebaseAdapter.cleanup();
        super.onPause();
    }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        init();
        mDatabase.child("recFriend").child(userProfile.getUserid()).child("onFresh").setValue("True");
        upDateRes();
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
                                    Intent intent = new Intent(SearchUser.this, SearchProfile.class);
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

        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 進入地圖以GPS結交好友
                Intent intent = new Intent(SearchUser.this, findNearby.class);
                startActivity(intent);

                // it is for Google Map , fragement
                /*
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new findNearby());
                transaction.commitAllowingStateLoss();*/
            }
        });


    }
    private void init(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btn_send = (Button) findViewById(R.id.btn_search_send);
        btn_find = (Button) findViewById(R.id.btn_findnearby);
        txt_userid = (EditText) findViewById(R.id.txt_id);
        txt_username = (EditText) findViewById(R.id.txt_username);
        txt_searchId = (EditText) findViewById(R.id.txt_searchId);
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO(SearchUser.this);
        userProfile = profileIO.ReadFile();

        mRecyclerView = (RecyclerView) findViewById(R.id.RecommendFriendRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        // 顯示最新資訊，由上而下
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
    }
    private void upDateRes(){
        mDatabase.child("recFriend").child(userProfile.getUserid()).child("onFresh").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String flag = snapshot.getValue().toString();
                if (flag.compareTo("False") == 0) {
                    recommendFriend();
                    //mDatabase.child("recFriend").child(userProfile.getUserid()).child("onFresh").removeEventListener(this);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        }
    private void recommendFriend(){

        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // 存取通知資料庫，並且顯示通知出來
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Object,
                RecommendFriendViewHolder>(
                Object.class,
                R.layout.item_recommendfriend,
                RecommendFriendViewHolder.class,
                mFirebaseDatabaseReference.child("recFriend").child(userProfile.getUserid()).orderByChild("userid")) {

            @Override
            protected void populateViewHolder(RecommendFriendViewHolder viewHolder,
                                              Object o , int position) {
                if( o.toString().compareTo("False")==0)
                {
                    viewHolder.ImageView.setVisibility(View.INVISIBLE);
                    viewHolder.txt_recommendId.setVisibility(View.INVISIBLE);
                }
                else {
                    String id = ((HashMap) o).get("userid").toString();
                    String userphoto = ((HashMap) o).get("userphoto").toString();
                    viewHolder.friendid = id;
                    viewHolder.txt_recommendId.setText(id);

                    Glide.with(SearchUser.this)
                            .load(userphoto)
                            .into(viewHolder.ImageView);
                }
            }
        };
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

}
