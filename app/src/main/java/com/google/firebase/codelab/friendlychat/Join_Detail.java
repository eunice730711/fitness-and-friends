package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

import static android.widget.ImageButton.*;

public class Join_Detail extends AppCompatActivity {

    private String join_ref;
    private JoinMessage join;
    private boolean init;
    private TextView Name;
    private TextView Date;
    private TextView Time;
    private TextView Text;
    private TextView Title;
    private TextView J_Time;
    private TextView J_Date;
    private TextView J_Pos;
    private TextView Join_num;
    private CircleImageView Photo;
    private EditText Content;
    private Button Send;
    private CheckBox Will_Go;
    private UserProfile userProfile;
    private DatabaseReference user_profile_ref;
    private DatabaseReference mem_ref ;
    private DatabaseReference JoinRef;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView message_Recyclerview;
    private LinearLayoutManager message_LayoutManger;
    private FirebaseRecyclerAdapter<Member, LikeViewHolder>
            mFirebaseAdapter;
    private FirebaseRecyclerAdapter<Message, UserMessageViewHolder>
            UsermessageAdapter;
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
    //紀錄user在join列表中的position
    public int user_position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_detail);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Intent intent = this.getIntent();
        join_ref = intent.getStringExtra("ref");
        // 從file取 使用者資料
        ProfileIO profileIO = new ProfileIO(Join_Detail.this);
        userProfile = profileIO.ReadFile();
        //join = (JoinMessage) intent.getSerializableExtra("join");

        Name=(TextView)findViewById(R.id.Detail_Name);
        Date=(TextView)findViewById(R.id.Detail_Date);
        Time=(TextView)findViewById(R.id.Detail_Time);
        Text=(TextView)findViewById(R.id.Detail_Text);
        Photo =(CircleImageView)findViewById(R.id.Detail_photo);
        Title=(TextView)findViewById(R.id.Detail_Title);
        J_Date=(TextView)findViewById(R.id.Detail_JDate);
        J_Time=(TextView)findViewById(R.id.Detail_JTime);
        J_Pos=(TextView)findViewById(R.id.Detail_JPos);
        Content = (EditText) findViewById(R.id.Content);
        Join_num = (TextView)findViewById(R.id.Join_num);
        Send = (Button) findViewById(R.id.Send_Message);
        Will_Go = (CheckBox)findViewById(R.id.Will_Go);


        JoinRef = FirebaseDatabase.getInstance().getReferenceFromUrl(join_ref);
        user_profile_ref = FirebaseDatabase.getInstance().getReferenceFromUrl(userProfile.getUserref());
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                join = dataSnapshot.getValue(JoinMessage.class);
                Name.setText(join.getName());
                Date.setText(join.getDate());
                Time.setText(join.getTime());
                Text.setText(join.getText());
                Title.setText(join.getTitle());
                J_Date.setText(join.getJdate());
                J_Time.setText(join.getJtime());
                J_Pos.setText(join.getJpos());
                Join_num.setText(join.getJoin_num() + "人");
                Glide.with(Join_Detail.this)
                        .load(join.getPhotoUrl())
                        .into(Photo);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        };
        JoinRef.addListenerForSingleValueEvent(postListener);

        Send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Content.getText().toString().equals("")) {
                    String nowDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                    String nowTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    Message temp = new Message(userProfile.getUsername()
                            , userProfile.getUserphoto(), userProfile.getUserid()
                            , Content.getText().toString(), nowTime, nowDate);
                    JoinRef.child("Message").push().setValue(temp);
                    Content.setText("");
                }
            }
        });

        init = false;
        JoinRef.child("Member").orderByChild("userid")
                .equalTo(userProfile.getUserid()).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            mem_ref = snapshot.getRef();
                            if(!init)
                                Will_Go.setChecked(true);
                        }
                        else{
                            mem_ref = null;
                        }
                        init = true;
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                });

        Will_Go.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked)
                {
                    if(init)
                    {
                        Member New_member = new Member(userProfile.getUserid()
                                ,userProfile.getUsername(),userProfile.getUserphoto());
                        JoinRef.child("Member").push().setValue(New_member);
                        join.setJoin_num(join.getJoin_num()+1);
                        user_profile_ref.child("Join_list").child(JoinRef.getKey()).setValue("1");
                        //資料庫有新增或刪減需通知adapter!!!!!!!不然會crash
                        mFirebaseAdapter.notifyItemInserted(user_position);
                        mFirebaseAdapter.notifyDataSetChanged();
                        mFirebaseAdapter.notifyItemRangeChanged(user_position,mFirebaseAdapter.getItemCount());
                    }
                }
                else
                {
                    mem_ref.removeValue();
                    join.setJoin_num(join.getJoin_num()-1);
                    user_profile_ref.child("Join_list").child(JoinRef.getKey()).removeValue();
                    mFirebaseAdapter.notifyItemRemoved(user_position);
                    mFirebaseAdapter.notifyDataSetChanged();
                    mFirebaseAdapter.notifyItemRangeChanged(user_position,mFirebaseAdapter.getItemCount());
                }
                JoinRef.child("join_num").setValue(join.getJoin_num());
                Join_num.setText(join.getJoin_num() + "人");

            }
        });
        //like列表
        init = false;
        mRecyclerView = (RecyclerView) findViewById(R.id.Join_people);
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
                LikeViewHolder.class,
                JoinRef.child("Member")) {
            @Override
            protected void populateViewHolder(final LikeViewHolder viewHolder,
                                              final Member member, final int position) {
                //mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.userid = member.getUserid();
                if (member.getUserid().equals(userProfile.getUserid())) {
                    user_position = position;
                    mem_ref = mFirebaseAdapter.getRef(position);
                }
                if (member.getPhotoUrl() == null) {
                    viewHolder.User_photo
                            .setImageDrawable(ContextCompat
                                    .getDrawable(Join_Detail.this,
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(Join_Detail.this)
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
                JoinRef.child("Message")) {
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
                                    .getDrawable(Join_Detail.this,
                                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(Join_Detail.this)
                            .load(message.getPhotoUrl())
                            .into(viewHolder.User_photo);
                }
            }
        };
        message_Recyclerview.setAdapter(UsermessageAdapter);
    }

}
