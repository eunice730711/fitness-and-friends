package com.google.firebase.codelab.friendlychat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class Register extends AppCompatActivity {

    public DatabaseReference mDatabase;
    private Button btn_send, btn_setProfile;
    private EditText txt_userid, txt_username, txt_searchId, txt_usercity, txt_userbirthday, txt_selfintroduction;
    private TextView txt_usergender;
    private RadioGroup usergender;
    private RadioButton checkedRadioButton;
    public UserProfile userProfile;
    private String refreshedToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("Registration");

        init();

        usergender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);

                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    // Changes the textview's text to "Checked: example radiobutton text"
                    //txt_usergender.setText(checkedRadioButton.getText());
                    txt_usergender.setText(checkedRadioButton.getText());
                    //txt_usergender =
                }
            }
        });

        btn_setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = txt_userid.getText().toString();
                String username= txt_username.getText().toString();
                String usercity = txt_usercity.getText().toString();
                String userbirthday = txt_userbirthday.getText().toString();
                String usergender = txt_usergender.getText().toString();
                String selfintroduction = txt_selfintroduction.getText().toString();
//
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                UserProfile test = new UserProfile(username, userid,refreshedToken, usercity, userbirthday, usergender, selfintroduction);
                mDatabase.child("UserProfile").push().setValue(test);
                Intent intent2 = new Intent();
                intent2.setClass(Register.this, Home.class);
                startActivity(intent2);
                Register.this.finish();
            }
        });


    }
//    private void init(){
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        btn_setProfile = (Button) findViewById(R.id.btn_setProfile);
//        txt_userid = (EditText) findViewById(R.id.txt_id);
//        txt_username = (EditText) findViewById(R.id.txt_username);
//        txt_searchId = (EditText) findViewById(R.id.txt_searchId);
//        refreshedToken = FirebaseInstanceId.getInstance().getToken();
//
//        //getUserProfile();
//    }

    private void init(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btn_send = (Button) findViewById(R.id.btn_search_send);
        btn_setProfile = (Button) findViewById(R.id.btn_setProfile);
        txt_userid = (EditText) findViewById(R.id.txt_id);
        txt_username = (EditText) findViewById(R.id.txt_username);
        txt_searchId = (EditText) findViewById(R.id.txt_searchId);
        txt_usercity = (EditText) findViewById(R.id.txt_usercity);
        txt_userbirthday = (EditText) findViewById(R.id.txt_userbirthday);
        txt_selfintroduction = (EditText) findViewById(R.id.txt_selfintroduction);
        usergender = (RadioGroup) findViewById(R.id.usergender);
        txt_usergender = (TextView) findViewById(R.id.showgender);
        checkedRadioButton = (RadioButton)usergender.findViewById(usergender.getCheckedRadioButtonId());
        refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //getUserProfile();
    }

//    private  void getUserProfile(){
//        // get the userProfile by instanceId
//        mDatabase.child("UserProfile").orderByChild("instanceid").equalTo(refreshedToken).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    userProfile = dataSnapshot.getValue(UserProfile.class);
//                    Log.d("userid",userProfile.getUserid());
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }


}
