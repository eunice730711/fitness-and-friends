package com.google.firebase.codelab.friendlychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class scheduleRunLevel extends AppCompatActivity {
    Result r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_run_level);

        r = new Result();
        r.setType(1);

//        Intent intent = this.getIntent();
//        r = (Result)intent.getSerializableExtra("run");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(scheduleRunLevel.this, schedule.class);
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

                r.setLevel(1);
                r.setDistance(0);
                Bundle bundle = new Bundle();
                bundle.putSerializable("final",r);
                intent.putExtras(bundle);

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

                r.setLevel(2);
                Bundle bundle = new Bundle();
                bundle.putSerializable("medium",r);
                intent.putExtras(bundle);

                startActivity(intent);
                scheduleRunLevel.this.finish();
            }
        });
    }
}
