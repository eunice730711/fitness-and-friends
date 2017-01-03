package com.google.firebase.codelab.friendlychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class scheduleChose extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_chose);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(scheduleChose.this, schedule.class);
                startActivity(intent1);
                scheduleChose.this.finish();
            }
        });

        ImageButton buttonRun = (ImageButton) findViewById(R.id.buttonRun);
        buttonRun.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(scheduleChose.this, scheduleRunLevel.class);

                Result r = new Result();
                Bundle bundle = new Bundle();
                r.setType(1);
                bundle.putSerializable("run",r);
                intent.putExtras(bundle);

                startActivity(intent);
                scheduleChose.this.finish();
            }
        });
    }
}
