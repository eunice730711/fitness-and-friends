package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.id;
import static com.google.firebase.codelab.friendlychat.R.id.btn_accept;
import static com.google.firebase.codelab.friendlychat.R.id.btn_deleteFriend;

public class FriendProfile extends AppCompatActivity {

    //Google account information
    //private AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
    public DatabaseReference mDatabase;
    private TextView txt_usergmail, txt_googlename, txt_usercity, txt_userbirthday, txt_userid, txt_usergender, txt_selfintroduction;
    private Button btn_delete;
    private CircleImageView userImageView;
    private String friendid;
    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO(FriendProfile.this);
        userProfile = profileIO.ReadFile();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            friendid = extras.getString("friendid");
        }

        //--------------------------Get firebase information
        txt_usercity = (TextView) findViewById(R.id.txt_usercity);
        txt_userid = (TextView) findViewById(R.id.txt_userid);
        txt_usergender = (TextView) findViewById(R.id.txt_usergender);
        txt_userbirthday = (TextView) findViewById(R.id.txt_userbirthday);
        txt_selfintroduction = (TextView) findViewById(R.id.txt_selfintroduction);
        txt_usergmail = (TextView) findViewById(R.id.txt_usergmail);
        txt_googlename = (TextView) findViewById(R.id.txt_googlename);
        userImageView = (CircleImageView) findViewById(R.id.userImageView);
        btn_delete = (Button) findViewById(btn_deleteFriend);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //refreshedToken = FirebaseInstanceId.getInstance().getToken();
        getProfile(friendid);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 刪除好友

                deleteFriend();
                Toast.makeText(v.getContext(), "恭喜你們已不再是好友", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getProfile(String id) {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.getProfileById(id, new FirebaseData.Callback() {
            @Override
            public void getProfile(UserProfile profile) {
                //User information
                txt_usercity.setText(profile.getUsercity());
                txt_userbirthday.setText(profile.getUserbirthday());
                txt_usergender.setText(profile.getUsergender());
                txt_userid.setText(profile.getUserid());
                txt_selfintroduction.setText(profile.getSelfintroduction());
                txt_usergmail.setText(profile.getUseremail());
                txt_googlename.setText(profile.getUsername());
                //imageView
                Glide.with(FriendProfile.this)
                        .load(profile.getUserphoto())
                        .into(userImageView);
                Log.d("userid", profile.getUserid());

            }
        });
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
