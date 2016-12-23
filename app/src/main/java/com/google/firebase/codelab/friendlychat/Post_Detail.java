package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Post_Detail extends AppCompatActivity {

    private String post_ref;
    private boolean init;
    private PostMessage post;
    private TextView Name;
    private TextView Date;
    private TextView Time;
    private TextView Text;
    private TextView Like_num;
    private CircleImageView Photo;
    private EditText Content;
    private Button Send;
    private CheckBox Like;
    private DatabaseReference FirebaseDatabaseRef;
    private DatabaseReference mem_ref;
    private DatabaseReference PostRef;
    private UserProfile userProfile;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView message_Recyclerview;
    private LinearLayoutManager message_LayoutManger;
    private FirebaseRecyclerAdapter<Member, LikeViewHolder>
            mFirebaseAdapter;
    private FirebaseRecyclerAdapter<Message, UserMessageViewHolder>
            UsermessageAdapter;

    //紀錄user在like列表中的position
    public int user_positoin;

    public static class LikeViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView User_photo;
        public String userid;

        public LikeViewHolder(View v) {
            super(v);
            User_photo = (CircleImageView) itemView.findViewById(R.id.user_photo);
            User_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 好友的個人頁面介紹
                    Intent i = new Intent(v.getContext(), FriendProfile.class);
                    i.putExtra("friendid", userid);
                    v.getContext().startActivity(i);
                }
            });
        }
    }

    public static class UserMessageViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView User_photo;
        public TextView User_name;
        public TextView User_message;
        public TextView User_date;
        public TextView User_time;
        public String userid;

        public UserMessageViewHolder(View v) {
            super(v);
            User_photo = (CircleImageView) itemView.findViewById(R.id.user_photo);
            User_name = (TextView) itemView.findViewById(R.id.user_name);
            User_message = (TextView) itemView.findViewById(R.id.user_message);
            User_date = (TextView) itemView.findViewById(R.id.user_date);
            User_time = (TextView) itemView.findViewById(R.id.user_time);
            User_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 好友的個人頁面介紹
                    Intent i = new Intent(v.getContext(), FriendProfile.class);
                    i.putExtra("friendid", userid);
                    v.getContext().startActivity(i);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Intent intent = this.getIntent();
        post_ref = intent.getStringExtra("ref");
        // 從file取 使用者資料
        ProfileIO profileIO = new ProfileIO(Post_Detail.this);
        userProfile = profileIO.ReadFile();

        Name = (TextView) findViewById(R.id.Detail_Name);
        Date = (TextView) findViewById(R.id.Detail_Date);
        Time = (TextView) findViewById(R.id.Detail_Time);
        Text = (TextView) findViewById(R.id.Detail_Text);
        Photo = (CircleImageView) findViewById(R.id.Detail_photo);
        Content = (EditText) findViewById(R.id.Content);
        Send = (Button) findViewById(R.id.Send_Message);
        Like_num = (TextView) findViewById(R.id.Like_num);
        Like = (CheckBox) findViewById(R.id.Like);
        //從資料庫取出post內容並顯示
        PostRef = FirebaseDatabase.getInstance().getReferenceFromUrl(post_ref);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                post = dataSnapshot.getValue(PostMessage.class);
                Name.setText(post.getName());
                Date.setText(post.getDate());
                Time.setText(post.getTime());
                Text.setText(post.getText());
                Like_num.setText(post.getLike_num() + "人");
                Glide.with(Post_Detail.this)
                        .load(post.getPhotoUrl())
                        .into(Photo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        PostRef.addValueEventListener(postListener);
        //post = (PostMessage) intent.getSerializableExtra("post");

        //send message button
        Send.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Content.getText().equals("")) {
                    String nowDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                    String nowTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    Message temp = new Message(userProfile.getUsername()
                            , userProfile.getUserphoto(), userProfile.getUserid()
                            , Content.getText().toString(), nowTime, nowDate);
                    PostRef.child("Message").push().setValue(temp);
                    Content.setText("");
                    UsermessageAdapter.notifyDataSetChanged();
                }
            }
        });

        //查詢使用者是否按過like
        PostRef.child("Member").orderByChild("userid")
                .equalTo(userProfile.getUserid()).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            mem_ref = snapshot.getRef();
                            if (!init)
                                Like.setChecked(true);
                        } else {
                            mem_ref = null;
                        }
                        init = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                });

        //like(加入資料庫) or unlike(從資料庫移除)
        Like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    if (init) {
                        Member New_member = new Member(userProfile.getUserid()
                                , userProfile.getUsername(), userProfile.getUserphoto());
                        PostRef.child("Member").push().setValue(New_member);
                        post.setLike_num(post.getLike_num() + 1);
                        //資料庫有新增或刪減需通知adapter!!!!!!!不然會crash
                        mFirebaseAdapter.notifyItemInserted(user_positoin);
                        mFirebaseAdapter.notifyDataSetChanged();
                        mFirebaseAdapter.notifyItemRangeChanged(user_positoin, mFirebaseAdapter.getItemCount());
                    }
                } else {
                    mem_ref.removeValue();
                    post.setLike_num(post.getLike_num() - 1);
                    mFirebaseAdapter.notifyItemRemoved(user_positoin);
                    mFirebaseAdapter.notifyDataSetChanged();
                    mFirebaseAdapter.notifyItemRangeChanged(user_positoin, mFirebaseAdapter.getItemCount());
                }
                PostRef.child("like_num").setValue(post.getLike_num());
                Like_num.setText(post.getLike_num() + "人");


            }
        });
        //like列表
        init = false;
        mRecyclerView = (RecyclerView) findViewById(R.id.Like_people);
        mLinearLayoutManager = new LinearLayoutManager(this);
        // 顯示最新資訊，由上而下
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        // New child entries
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Member, LikeViewHolder>(
                Member.class,
                R.layout.item_likepeople,
                Post_Detail.LikeViewHolder.class,
                PostRef.child("Member")) {
            @Override
            protected void populateViewHolder(final LikeViewHolder viewHolder,
                                              final Member member, final int position) {
                //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.userid = member.getUserid();
                if (member.getUserid().equals(userProfile.getUserid())) {
                    user_positoin = position;
                    mem_ref = mFirebaseAdapter.getRef(position);
                }
                if (member.getPhotoUrl() == null) {
                    viewHolder.User_photo
                            .setImageDrawable(ContextCompat
                                    .getDrawable(Post_Detail.this,
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(Post_Detail.this)
                            .load(member.getPhotoUrl())
                            .into(viewHolder.User_photo);
                }
            }
        };
        mRecyclerView.setAdapter(mFirebaseAdapter);

        // 朋友的留言
        message_Recyclerview = (RecyclerView) findViewById(R.id.user_message);
        message_LayoutManger = new LinearLayoutManager(this);
        // 顯示最新資訊，由上而下
        //message_LayoutManger.setReverseLayout(true);
        //message_LayoutManger.setStackFromEnd(true);
        message_Recyclerview.setLayoutManager(message_LayoutManger);
        UsermessageAdapter = new FirebaseRecyclerAdapter<Message, UserMessageViewHolder>(
                Message.class,
                R.layout.item_usermessage,
                UserMessageViewHolder.class,
                PostRef.child("Message")) {
            @Override
            protected void populateViewHolder(final UserMessageViewHolder viewHolder,
                                              final Message message, final int position) {
                //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.userid = message.getUserid();
                viewHolder.User_name.setText(message.getName());
                viewHolder.User_message.setText(message.getContent());
                viewHolder.User_date.setText(message.getMdate());
                viewHolder.User_time.setText(message.getMtime());
                if (message.getPhotoUrl() == null) {
                    viewHolder.User_photo
                            .setImageDrawable(ContextCompat
                                    .getDrawable(Post_Detail.this,
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(Post_Detail.this)
                            .load(message.getPhotoUrl())
                            .into(viewHolder.User_photo);
                }
            }
        };
        message_Recyclerview.setAdapter(UsermessageAdapter);
    }
}
