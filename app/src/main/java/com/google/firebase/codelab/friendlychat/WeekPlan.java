package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WeekPlan extends AppCompatActivity {

    List<WeekContent> myplan = new ArrayList<WeekContent>();
    List <WeekContent> receive = new ArrayList<WeekContent>();

    private ListView planListview;

    private PlanAdapter adapter;
    TextView wtitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_plan);

        Intent intent = this.getIntent();
        receive = (List<WeekContent>) intent.getSerializableExtra("plan");

        for(int i=0; i<receive.size()-1; i++){
            myplan.add(receive.get(i));
        }

        int position = receive.get(receive.size()-1).getWeek();

        wtitle = (TextView) findViewById(R.id.textView);
        wtitle.setText("Week"+myplan.get(position).getWeek());

        planListview = (ListView)findViewById(R.id.EveryDay);
        adapter = new PlanAdapter(WeekPlan.this,myplan,position);
        planListview.setAdapter(adapter);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent intent1 = new Intent();
            intent1.setClass(this, schedule.class);
            startActivity(intent1);
            WeekPlan.this.finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
