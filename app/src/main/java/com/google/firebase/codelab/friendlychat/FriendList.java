package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.codelab.friendlychat.R.id.btn_friendprofile;

//import static com.google.firebase.codelab.friendlychat.R.id.btn_friend;

// 顯示該使用者的好友列表
public class FriendList extends AppCompatActivity {
    public DatabaseReference mDatabase;
    public UserProfile userProfile, result;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Object,FriendListViewHolder>
            mFirebaseAdapter;

    // ViewHolder 可以做每個好友的layout設定
    public static class FriendListViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_friendName;
        public Button btn_friendprofile;
        public CircleImageView friendListImageView;
        public UserProfile userProfile;
        public String friendid;
        public FriendListViewHolder( View v) {
            super(v);
            txt_friendName = (TextView) itemView.findViewById(R.id.txt_friendName);
            btn_friendprofile = (Button) itemView.findViewById(R.id.btn_friendprofile);
            friendListImageView= (CircleImageView) itemView.findViewById(R.id.friendListImageView);


            btn_friendprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 刪除好友
                    Intent i = new Intent(v.getContext(), FriendProfile.class);
                    i.putExtra("friendid", friendid);
                    v.getContext().startActivity(i);

                }
            });

/*
            btn_deleteFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 刪除好友
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    // 在雙方的好友資料中都需進行刪除
                    deleteFriend();
                    Toast.makeText(v.getContext(), "恭喜你們已不再是好友", Toast.LENGTH_SHORT).show();

                }
            });
            */
        }

        public void deleteFriend(){
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            // 從自己的好友資料庫刪除對方

            mDatabase.child("Friend").child(userProfile.getUserid()).orderByChild("friendid").equalTo(friendid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        // 找到viewholder對應的 requester
                        dataSnapshot.getRef().removeValue();

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            //從對方的好友資料庫刪除自己
            mDatabase.child("Friend").child(friendid).orderByChild("friendid").equalTo(userProfile.getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        // 找到viewholder對應的 requester
                        dataSnapshot.getRef().removeValue();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        // 初始化，以及程式與layout的連結
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = (RecyclerView) findViewById(R.id.friendListRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        // 顯示最新資訊，由上而下
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        getFriendList();

    }
    private void getFriendList(){
        getUserProfile();
        //userProfile = get("alan");
        //show();
    }
    private void getUserProfile() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // get the userProfile by instanceId
        // 該部分須改成以email取得個人資料
        mDatabase.child("UserProfile").orderByChild("instanceid").equalTo(refreshedToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userProfile = dataSnapshot.getValue(UserProfile.class);
                    Log.d("username", userProfile.getUsername());

                    // 在取得使用者資料後才對好友資料庫存取
                    show();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void show(){
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // 取得好友名單並且列出
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Object,
                FriendListViewHolder>(
                Object.class,
                R.layout.item_friendlist,
                FriendListViewHolder.class,
                mFirebaseDatabaseReference.child("Friend").child(userProfile.getUserid()).orderByChild("friendid")) {

            @Override
            protected void populateViewHolder(FriendListViewHolder viewHolder,
                                              Object o, int position) {

                        viewHolder.txt_friendName.setText(((HashMap)o).get("friendid").toString());
                        viewHolder.friendid = ((HashMap)o).get("friendid").toString();
                        viewHolder.userProfile = userProfile;

            }
        };
        mRecyclerView.setAdapter(mFirebaseAdapter);

    }

    public UserProfile get(String id) {
        result = null;
        mDatabase.child("UserProfile").orderByChild("userid").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    result = dataSnapshot.getValue(UserProfile.class);
                    break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        while( result ==null);
        return result;
    }

}
