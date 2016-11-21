package com.google.firebase.codelab.friendlychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import static com.google.firebase.codelab.friendlychat.R.id.home;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Button buttonTalk = (Button) findViewById(R.id.buttonTalk);
        buttonTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(Home.this, MainActivity.class);
                startActivity(intent2);
                Home.this.finish();
            }
        });

        Button buttonSchedule = (Button) findViewById(R.id.buttonSchedule);
        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(Home.this, schedule.class);
                startActivity(intent2);
                Home.this.finish();
            }
        });
        //  the activity for setting userprofile and requesting friend by id
        Button btn_text = (Button) findViewById(R.id.btn_test);
        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(Home.this, SearchUser.class);
                startActivity(intent2);
                Home.this.finish();
            }
        });


        Button btn_notification = (Button) findViewById(R.id.btn_notification);
        btn_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent();
                intent2.setClass(Home.this, Notification.class);
                startActivity(intent2);
                Home.this.finish();
            }
        });

    }
}
