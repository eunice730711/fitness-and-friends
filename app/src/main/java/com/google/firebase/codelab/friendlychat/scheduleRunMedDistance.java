package com.google.firebase.codelab.friendlychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class scheduleRunMedDistance extends AppCompatActivity {
    Result r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_run_med_distance);

        Intent intent = this.getIntent();
        r = (Result)intent.getSerializableExtra("medium");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(scheduleRunMedDistance.this, scheduleRunLevel.class);
                startActivity(intent1);
                scheduleRunMedDistance.this.finish();
            }
        });

        ImageButton buttonOneSix = (ImageButton) findViewById(R.id.buttonOneSix);
        buttonOneSix.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(scheduleRunMedDistance.this, scheduleRunDay.class);

                r.setDistance(1);
                Bundle bundle = new Bundle();
                bundle.putSerializable("final",r);
                intent.putExtras(bundle);

                startActivity(intent);
                scheduleRunMedDistance.this.finish();
            }
        });

        ImageButton buttonThree = (ImageButton) findViewById(R.id.buttonThree);
        buttonThree.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(scheduleRunMedDistance.this, scheduleRunDay.class);

                r.setDistance(2);
                Bundle bundle = new Bundle();
                bundle.putSerializable("final",r);
                intent.putExtras(bundle);

                startActivity(intent);
                scheduleRunMedDistance.this.finish();
            }
        });

        ImageButton buttonFive = (ImageButton) findViewById(R.id.buttonFive);
        buttonFive.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(scheduleRunMedDistance.this, scheduleRunDay.class);

                r.setDistance(3);
                Bundle bundle = new Bundle();
                bundle.putSerializable("final",r);
                intent.putExtras(bundle);

                startActivity(intent);
                scheduleRunMedDistance.this.finish();
            }
        });

        ImageButton buttonSeven = (ImageButton) findViewById(R.id.buttonSeven);
        buttonSeven.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(scheduleRunMedDistance.this, scheduleRunDay.class);

                r.setDistance(4);
                Bundle bundle = new Bundle();
                bundle.putSerializable("final",r);
                intent.putExtras(bundle);

                startActivity(intent);
                scheduleRunMedDistance.this.finish();
            }
        });

        ImageButton buttonTen = (ImageButton) findViewById(R.id.buttonTen);
        buttonTen.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(scheduleRunMedDistance.this, scheduleRunDay.class);

                r.setDistance(5);
                Bundle bundle = new Bundle();
                bundle.putSerializable("final",r);
                intent.putExtras(bundle);

                startActivity(intent);
                scheduleRunMedDistance.this.finish();
            }
        });
    }
}
