package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    private DatabaseReference FirebaseDatabaseRef;
    private DatabaseReference mem_ref ;
    private DatabaseReference JoinRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_detail);
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
        FirebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();
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

                if (!Content.getText().equals("")) {
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
                .addValueEventListener(new ValueEventListener() {
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
                        JoinRef.child("join_num").setValue(join.getJoin_num());
                        Join_num.setText(join.getJoin_num() + "人");
                    }
                }
                else
                {
                    mem_ref.removeValue();
                    join.setJoin_num(join.getJoin_num()-1);
                    JoinRef.child("join_num").setValue(join.getJoin_num());
                    Join_num.setText(join.getJoin_num() + "人");
                }

            }
        });

    }

}
