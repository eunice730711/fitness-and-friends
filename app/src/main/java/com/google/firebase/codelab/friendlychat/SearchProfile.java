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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.id;
import static com.google.firebase.codelab.friendlychat.R.id.txt_searchId;

public class SearchProfile extends AppCompatActivity {

    //Google account information
    //private AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
    public DatabaseReference mDatabase;

    private TextView txt_usergmail, txt_googlename, txt_usercity, txt_userbirthday, txt_userid, txt_usergender, txt_selfintroduction;
    private Button btn_invite;
    private CircleImageView userImageView;
    private String friendid;
    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            friendid = extras.getString("friendid");
        }
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO(SearchProfile.this);
        userProfile = profileIO.ReadFile();

        //--------------------------Get firebase information
        txt_usercity = (TextView) findViewById(R.id.txt_usercity);
        txt_userid = (TextView) findViewById(R.id.txt_userid);
        txt_usergender = (TextView) findViewById(R.id.txt_usergender);
        txt_userbirthday = (TextView) findViewById(R.id.txt_userbirthday);
        txt_selfintroduction = (TextView) findViewById(R.id.txt_selfintroduction);
        txt_usergmail = (TextView) findViewById(R.id.txt_usergmail);
        txt_googlename = (TextView) findViewById(R.id.txt_googlename);
        userImageView = (CircleImageView) findViewById(R.id.userImageView);
        btn_invite = (Button)findViewById(R.id.btn_invite);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //refreshedToken = FirebaseInstanceId.getInstance().getToken();
        getProfile(friendid);

        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 送出邀請
                mDatabase.child("RequestFriend").child(friendid).getRef()
                        .orderByChild("requester").equalTo(userProfile.getUserid()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        // 避免重複送出邀請
                        if (snapshot.exists())
                            Toast.makeText(SearchProfile.this, "已發送過邀請，請等待對方確認", Toast.LENGTH_SHORT).show();
                        else {
                            // 送出邀請
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("requester", userProfile.getUserid());
                            mDatabase.child("RequestFriend").child(friendid).push().setValue(map);
                            Toast.makeText(SearchProfile.this, "Friend request has been sent", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

    }
    private void getProfile(String id){
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.getProfileById(id,new FirebaseData.Callback(){
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

                Glide.with(SearchProfile.this)
                        .load(profile.getUserphoto())
                        .into(userImageView);
                Log.d("userid", profile.getUserid());

            }
        });
    }
}
