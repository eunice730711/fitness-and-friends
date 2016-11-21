package com.google.firebase.codelab.friendlychat;


import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.codelab.friendlychat.R.id.txt_searchId;


public class Notification extends AppCompatActivity {
    public DatabaseReference mDatabase;
    public UserProfile userProfile;
    private RecyclerView mMessageRecyclerView;
    private FirebaseRecyclerAdapter<RequestFriend, RequestViewHolder>
            mFirebaseAdapter;

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_request;
        public Button btn_accept, btn_refect;
        public CircleImageView messengerImageView;
        public UserProfile userProfile;
        public RequestFriend requestFriend;
        public RequestViewHolder(View v) {
            super(v);
            txt_request = (TextView) itemView.findViewById(R.id.txt_request);
            btn_accept = (Button) itemView.findViewById(R.id.btn_accept);
            btn_refect = (Button) itemView.findViewById(R.id.btn_reject);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.requestImageView);

            btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                    int index = txt_request.getText().toString().indexOf("送");
                    String userid1 = txt_request.getText().subSequence(0,index).toString();
                    Friend friend = new Friend(userid1,userProfile.getUserid());
                    mDatabase.child("Friend").push().setValue(friend);
                }
            });

            btn_refect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("RequestFriend").orderByChild("user").equalTo(requestFriend.getUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                RequestFriend requestFriend = dataSnapshot.getValue(RequestFriend.class);
                                if( requestFriend.getRequester().compareTo(requestFriend.getRequester())==0){
                                    dataSnapshot.getRef().removeValue();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);

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
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
