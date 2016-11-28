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

import de.hdodenhof.circleimageview.CircleImageView;

//import static com.google.firebase.codelab.friendlychat.R.id.btn_friend;

public class FriendList extends AppCompatActivity {
    public DatabaseReference mDatabase;
    public UserProfile userProfile;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<String,FriendListViewHolder>
            mFirebaseAdapter;

    public static class FriendListViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_friendName;
        public Button btn_deleteFriend;
        public CircleImageView friendListImageView;
        public UserProfile userProfile;
        public String friendid;
        public FriendListViewHolder( View v) {
            super(v);
            txt_friendName = (TextView) itemView.findViewById(R.id.txt_friendName);
            btn_deleteFriend = (Button) itemView.findViewById(R.id.btn_deleteFriend);
            friendListImageView= (CircleImageView) itemView.findViewById(R.id.friendListImageView);


            btn_deleteFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 刪除好友
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    // 不知道是個順序
                    mDatabase.child("Friend").child(userProfile.getUserid()).child("friendid").equalTo(friendid).getRef().removeValue();
                    mDatabase.child("Friend").child(friendid).child("friendid").equalTo(userProfile.getUserid()).getRef().removeValue();
                    Toast.makeText(v.getContext(), "恭喜你們已不再是好友", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = (RecyclerView) findViewById(R.id.friendListRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        getFriendList();

    }
    private void getFriendList(){
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
                    Log.d("username", userProfile.getUsername());
                    // 再取得使用者資料後才對資料庫存取

                    show();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void show(){
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<String,
                FriendListViewHolder>(
                String.class,
                R.layout.item_friendlist,
                FriendListViewHolder.class,
                mFirebaseDatabaseReference.child("Friend").child(userProfile.getUserid())) {

            @Override
            protected void populateViewHolder(FriendListViewHolder viewHolder,
                                              String m, int position) {
                        viewHolder.txt_friendName.setText(  m);
                        viewHolder.friendid =   m;
                        viewHolder.userProfile = userProfile;
            }
        };
        mRecyclerView.setAdapter(mFirebaseAdapter);

    }


}
