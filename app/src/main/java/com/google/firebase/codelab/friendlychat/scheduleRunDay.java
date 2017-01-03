package com.google.firebase.codelab.friendlychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;

public class scheduleRunDay extends AppCompatActivity {
    Result r;
    int d1;
    int d2;
    int d3;
    int d4;
    int d5;
    int d6;
    int d7;
    int all;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_run_day);

        button = (Button)findViewById(R.id.button5) ;

        Intent intent = this.getIntent();
        r = (Result)intent.getSerializableExtra("final");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(scheduleRunDay.this, schedule.class);
                startActivity(intent1);
                scheduleRunDay.this.finish();
            }
        });

        CheckBox c1 = (CheckBox)findViewById(R.id.c1);
        CheckBox c2 = (CheckBox)findViewById(R.id.c2);
        CheckBox c3 = (CheckBox)findViewById(R.id.c3);
        CheckBox c4 = (CheckBox)findViewById(R.id.c4);
        CheckBox c5 = (CheckBox)findViewById(R.id.c5);
        CheckBox c6 = (CheckBox)findViewById(R.id.c6);
        CheckBox c7 = (CheckBox)findViewById(R.id.c7);

        c1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    d1 += 1;
                    button.setVisibility(View.VISIBLE);
                }
                else{
                    d1 -= 1;
                }
            }
        });

        c2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    d2 += 1;
                    button.setVisibility(View.VISIBLE);
                }
                else{
                    d2 -= 1;
                }
            }
        });

        c3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    d3 += 1;
                    button.setVisibility(View.VISIBLE);
                }
                else{
                    d3 -= 1;
                }
            }
        });

        c4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    d4 += 1;
                    button.setVisibility(View.VISIBLE);
                }
                else{
                    d4 -= 1;
                }
            }
        });

        c5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    d5 += 1;
                    button.setVisibility(View.VISIBLE);
                }
                else{
                    d5 -= 1;
                }
            }
        });

        c6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    d6 += 1;
                    button.setVisibility(View.VISIBLE);
                }
                else{
                    d6 -= 1;
                }
            }
        });

        c7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    d7 += 1;
                    button.setVisibility(View.VISIBLE);
                }
                else {
                    d7 -= 1;
                }
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all = d1 + d2 + d3 + d4 + d5 + d6 + d7;
                if((all == 0)||(all == 1) ){
                    Toast.makeText(getApplicationContext(), "You should select at least 2 days.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent();
                    intent.setClass(scheduleRunDay.this, schedule.class);
                    String s1 = String.valueOf(d1);
                    String s2 = String.valueOf(d2);
                    String s3 = String.valueOf(d3);
                    String s4 = String.valueOf(d4);
                    String s5 = String.valueOf(d5);
                    String s6 = String.valueOf(d6);
                    String s7 = String.valueOf(d7);
                    String days = "" + s1 + s2 + s3 + s4 + s5 + s6 + s7;
                    r.setDays(days);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("schedule", r);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    scheduleRunDay.this.finish();
                }
            }
        });

    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.done){
            Intent intent1 = new Intent();
            intent1.setClass(this, schedule.class);
            startActivity(intent1);
            scheduleRunDay.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    */
}
