package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;



// 顯示該使用者的好友列表
public class FriendList extends AppCompatActivity {
    public DatabaseReference mDatabase;
    public UserProfile userProfile, friendProfile;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Object,FriendListViewHolder>
            mFirebaseAdapter;

    // ViewHolder 可以做每個好友的layout設定
    public static class FriendListViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_friendid, txt_friendname;
        public Button btn_friendProfile;
        public CircleImageView friendListImageView;
        public UserProfile userProfile;
        public String friendid;
        public FriendListViewHolder( View v) {
            super(v);
            txt_friendid = (TextView) itemView.findViewById(R.id.txt_friendid);
            txt_friendname = (TextView) itemView.findViewById(R.id.txt_friendname);
            btn_friendProfile = (Button) itemView.findViewById(R.id.btn_friendProfile);
            friendListImageView= (CircleImageView) itemView.findViewById(R.id.friendListImageView);

            btn_friendProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 好友的個人頁面介紹
                    Intent i = new Intent(v.getContext(), FriendProfile.class);
                    i.putExtra("friendid", friendid);
                    v.getContext().startActivity(i);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO(FriendList.this);
        userProfile = profileIO.ReadFile();
        // 初始化，以及程式與layout的連結
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = (RecyclerView) findViewById(R.id.friendListRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        // 顯示最新資訊，由上而下
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        showFriendList();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(FriendList.this, MainActivity.class);
                startActivity(intent1);
                FriendList.this.finish();
            }
        });

    }


    private void showFriendList(){
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
                        Log.e("list",o.toString());
                        String friendid = ((HashMap)o).get("friendid").toString();
                        String friendname = ((HashMap)o).get("friendname").toString();

                Log.e("friend",friendid);
                        viewHolder.friendid = friendid;
                        viewHolder.userProfile = userProfile;
                        viewHolder.txt_friendid.setText(friendid);
                        viewHolder.txt_friendname.setText(friendname);
                //imageView
                Glide.with(FriendList.this)
                        .load(((HashMap)o).get("friendphotourl"))
                        .into(viewHolder.friendListImageView);
                Log.d("userid", userProfile.getUserid());
            }
        };
        mRecyclerView.setAdapter(mFirebaseAdapter);

    }

}
