package com.google.firebase.codelab.friendlychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;

public class scheduleRunDay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_run_day);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(scheduleRunDay.this, scheduleRunLevel.class);
                startActivity(intent1);
                scheduleRunDay.this.finish();
            }
        });

        final TextView mTextView = (TextView)findViewById(R.id.next);

        CheckBox c1 = (CheckBox)findViewById(R.id.c1);
        CheckBox c2 = (CheckBox)findViewById(R.id.c2);
        CheckBox c3 = (CheckBox)findViewById(R.id.c3);
        CheckBox c4 = (CheckBox)findViewById(R.id.c4);
        CheckBox C5 = (CheckBox)findViewById(R.id.c5);
        CheckBox C6 = (CheckBox)findViewById(R.id.c6);
        CheckBox c7 = (CheckBox)findViewById(R.id.c7);

        c1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mTextView.setVisibility(View.VISIBLE);
                }
                else{
                    mTextView.setVisibility(View.GONE);
                }
            }
        });

    }
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
}
