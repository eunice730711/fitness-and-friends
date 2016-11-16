package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class schedule extends AppCompatActivity {
        private Button buttonHome,buttonTalk;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_schedule);
            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);


            buttonHome = (Button) findViewById(R.id.buttonHome);
            buttonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(schedule.this, Home.class);
                    startActivity(intent);
                    schedule.this.finish();
                }
            });

            buttonTalk = (Button) findViewById(R.id.buttonTalk);
            buttonTalk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent2 = new Intent();
                    intent2.setClass(schedule.this, MainActivity.class);
                    startActivity(intent2);
                    schedule.this.finish();
                }
            });

        }
        @Override
        public boolean onCreateOptionsMenu(Menu menu){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item){
            int id = item.getItemId();
            if (id == R.id.menu_edit){
                Intent intent1 = new Intent();
                intent1.setClass(this, scheduleChose.class);
                startActivity(intent1);

            }
            return super.onOptionsItemSelected(item);
        }
    }



