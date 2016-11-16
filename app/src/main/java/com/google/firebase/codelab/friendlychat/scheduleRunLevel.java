package com.google.firebase.codelab.friendlychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class scheduleRunLevel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_run_level);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(scheduleRunLevel.this, scheduleChose.class);
                startActivity(intent1);
                scheduleRunLevel.this.finish();
            }
        });

        ImageButton buttonRookie = (ImageButton) findViewById(R.id.buttonRookie);
        buttonRookie.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(scheduleRunLevel.this, scheduleRunDay.class);
                startActivity(intent);
                scheduleRunLevel.this.finish();
            }
        });

        ImageButton buttonMed = (ImageButton) findViewById(R.id.buttonMed);
        buttonMed.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(scheduleRunLevel.this, scheduleRunMedDistance.class);
                startActivity(intent);
                scheduleRunLevel.this.finish();
            }
        });
    }
}
