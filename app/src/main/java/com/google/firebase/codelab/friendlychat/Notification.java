package com.google.firebase.codelab.friendlychat;


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


public class Notification extends AppCompatActivity {
    public DatabaseReference mDatabase;
    public UserProfile userProfile;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<RequestFriend, RequestViewHolder>
            mFirebaseAdapter;
    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_request;
        public Button btn_accept, btn_refect;
        public CircleImageView requestImageView;
        public UserProfile userProfile;
        public RequestFriend requestFriend;
        public RequestViewHolder( View v) {
            super(v);
            txt_request = (TextView) itemView.findViewById(R.id.txt_request);
            btn_accept = (Button) itemView.findViewById(R.id.btn_accept);
            btn_refect = (Button) itemView.findViewById(R.id.btn_reject);
            requestImageView = (CircleImageView) itemView.findViewById(R.id.requestImageView);

            btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    int index = txt_request.getText().toString().indexOf("送")-1;
                    String friendid = txt_request.getText().subSequence(0,index).toString();
                    HashMap<String, String> friend = new HashMap<String, String>();
                    HashMap<String, String> friend2 = new HashMap<String, String>();

                    friend.put("friendid",friendid);
                    friend2.put("friendid",userProfile.getUserid());
                    mDatabase.child("Friend").child(userProfile.getUserid()).getRef().setValue(friend);
                    mDatabase.child("Friend").child(friendid).getRef().setValue(friend2);
                    // 刪除好友邀請
                    mDatabase.child("RequestFriend").orderByChild("user").equalTo(requestFriend.getUser()).getRef().
                            equalTo(requestFriend.getRequester()).getRef().removeValue();
                    Toast.makeText(v.getContext(), "恭喜你們已成為好友", Toast.LENGTH_LONG).show();

                }
            });

            btn_refect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 刪除好友邀請
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("RequestFriend").orderByChild("user").equalTo(requestFriend.getUser()).getRef().
                            equalTo(requestFriend.getRequester()).getRef().removeValue();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = (RecyclerView) findViewById(R.id.notificationRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        getUserProfile();


    }

    private void getUserProfile() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // get the userProfile by instanceId
        mDatabase.child("UserProfile").orderByChild("instanceid").equalTo(refreshedToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userProfile = dataSnapshot.getValue(UserProfile.class);
                    Log.e("username", userProfile.getUsername());
                    // 再取得使用者資料後才對資料庫存取
                    show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void show(){
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<RequestFriend,
                RequestViewHolder>(
                RequestFriend.class,
                R.layout.item_request_friend,
                RequestViewHolder.class,
                mFirebaseDatabaseReference.child("RequestFriend").orderByChild("user").equalTo(userProfile.getUserid())) {

            @Override
            protected void populateViewHolder(RequestViewHolder viewHolder,
                                              RequestFriend requestFriend, int position) {
                viewHolder.txt_request.setText(requestFriend.getRequester() + " 送出好友邀請");
                viewHolder.requestFriend = requestFriend;
                viewHolder.userProfile = userProfile;
            }
        };
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }
}
