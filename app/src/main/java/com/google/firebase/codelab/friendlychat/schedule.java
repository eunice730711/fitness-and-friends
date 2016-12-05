package com.google.firebase.codelab.friendlychat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class schedule extends AppCompatActivity {
    private Button buttonHome,buttonTalk;

    private ListView weekListview;
    List<String> weekList = new ArrayList<String>();
    private WeekAdapter adapter;

    List <WeekContent> myplan = new ArrayList<WeekContent>();

    ScheduleIO fileIO = new ScheduleIO(schedule.this);

    Result r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = this.getIntent();
        r = (Result) intent.getSerializableExtra("schedul");


        // 01 display every week
        weekListview = (ListView)findViewById(R.id.ListView);
        for(int i=1;i<=4;i++){
            weekList.add("Week "+ String.valueOf(i) );
        }


        // 02 Read the old plan from a file in your mobile phone,
        //    or create new one and write new file.
        if(r!=null){
            setPlan();
            fileIO.WriteFile(myplan);
        }
        else{
            myplan = fileIO.ReadFile();
        }



        // 03 This is my own WeekAdapter which is used to display the custom ListView.
        adapter = new WeekAdapter(schedule.this,weekList,myplan);
        weekListview.setAdapter(adapter);


        // 04 When a user click one list
        weekListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                ListView listView = (ListView) adapterView;

                if(myplan.size()!=0 ){

                    if(position < myplan.size()){
                        Intent intent3 = new Intent();
                        intent3.setClass(schedule.this, WeekPlan.class);

                        // 05 using the var week in fifth element to represent which list is clicked,
                        //    and the element 1~4 (index 0~3) are user's plan.
                        //    Then pass this list to next page.
                        List <WeekContent> send;
                        send = myplan;
                        WeekContent p = new WeekContent();
                        p.setWeek(position);
                        send.add(p);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("plan", (Serializable) send);
                        intent3.putExtras(bundle);

                        startActivity(intent3);
                        schedule.this.finish();
                    }
                    if(position >= myplan.size()){
                        Toast.makeText(
                                schedule.this,
                                "此週尚未建立運動計畫\n請點擊右上角扳手建立",
                                Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
                    Toast.makeText(
                            schedule.this,
                            "尚未建立運動計畫\n請點擊右上角扳手建立",
                            Toast.LENGTH_LONG).show();
                }

            }
        });



        ImageButton ihome = (ImageButton) findViewById(R.id.ihome);
        ihome.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(schedule.this, Home.class);
                startActivity(intent);
                schedule.this.finish();
            }
        });

        ImageButton italk = (ImageButton) findViewById(R.id.italk);
        italk.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(schedule.this, MainActivity.class);
                startActivity(intent);
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

            if(myplan.size()>=3 ){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("是否重新設定運動計畫?")
                        .setNegativeButton("是", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                fileIO.DeleteFile();
                                Intent intent1 = new Intent();
                                intent1.setClass(schedule.this, scheduleRunLevel.class);

                                Result r = new Result();
                                Bundle bundle = new Bundle();
                                r.setType(1);
                                bundle.putSerializable("run",r);
                                intent1.putExtras(bundle);

                                startActivity(intent1);
                                schedule.this.finish();
                            }
                        })
                        .setPositiveButton("否", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog about_dialog = builder.create();
                about_dialog.show();
            }
            else if(myplan.size()<=2 && myplan.size()!=0){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("重新建立或是接續目前的運動計畫?")
                        .setNegativeButton("重新建立", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                fileIO.DeleteFile();
                                Intent intent1 = new Intent();
                                intent1.setClass(schedule.this, scheduleRunLevel.class);

                                Result r = new Result();
                                Bundle bundle = new Bundle();
                                r.setType(1);
                                bundle.putSerializable("run",r);
                                intent1.putExtras(bundle);

                                startActivity(intent1);
                                schedule.this.finish();
                            }
                        })
                        .setPositiveButton("接續", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent1 = new Intent();
                                intent1.setClass(schedule.this, scheduleRunLevel.class);

                                Result r = new Result();
                                Bundle bundle = new Bundle();
                                r.setType(1);
                                bundle.putSerializable("run",r);
                                intent1.putExtras(bundle);

                                startActivity(intent1);
                                schedule.this.finish();
                            }
                        });
                AlertDialog about_dialog = builder.create();
                about_dialog.show();
            }
            else{
                Intent intent1 = new Intent();
                intent1.setClass(this, scheduleRunLevel.class);

                Result r = new Result();
                Bundle bundle = new Bundle();
                r.setType(1);
                bundle.putSerializable("run",r);
                intent1.putExtras(bundle);

                startActivity(intent1);
                schedule.this.finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void setPlan(){

        List <WeekContent> oldplan = new ArrayList<WeekContent>();
        oldplan = fileIO.ReadFile();

        if(r.getLevel() == 1){
            List<Integer> time_range = new ArrayList<Integer>(Arrays.asList(10,15,12,15,15,12,10));

            if(oldplan.size()==2){
                myplan = oldplan;
            }

            for(int w=0; w<2; w++){
                List<Day> days = new ArrayList<Day>();

                int count=0;
                for(int i=0; i<r.getDays().length(); i++){
                    Day new_day = new Day();
                    new_day.setDist(0.0);
                    new_day.setComplete(false);
                    if(r.getDays().charAt(i) == '1'){
                        if(count == r.countDays()-1){
                            new_day.setTime(10+5*w);
                        }
                        else{
                            new_day.setTime(time_range.get(count)+5*w);
                            count+=1;
                        }
                        new_day.setChoose(true);
                    }
                    else{
                        new_day.setTime(0);
                        new_day.setChoose(false);
                    }
                    days.add(new_day);
                }

                WeekContent new_week = new WeekContent();
                new_week.setDays(days);
                new_week.setWeek(w+1);
                new_week.setLevel(r.getLevel());

                myplan.add(new_week);
            }

        }
        else if(r.getLevel() == 2){

            List<Double> dist_range = new ArrayList<Double>(Arrays.asList(0.0,0.5,0.25,0.5,0.5,0.25,0.0));
            List<Double> dist = new ArrayList<Double>(Arrays.asList(1.6,2.1,2.5,3.0,
                    3.0,3.5,4.0,4.5,
                    5.0,5.5,6.0,6.5,
                    7.0,7.5,8.0,8.5,
                    9.0,9.5,10.0,10.5));
            int end = 4;
            if(oldplan.size() == 2){
                end = 2;
                myplan = oldplan;
            }

            for(int w=0; w<end; w++){
                List<Day> days = new ArrayList<Day>();

                int count=0;
                for(int i=0; i<r.getDays().length(); i++){
                    Day new_day = new Day();
                    new_day.setTime(0);
                    new_day.setComplete(false);
                    if(r.getDays().charAt(i) == '1'){
                        if(count == r.countDays()-1){
                            new_day.setDist(dist.get((r.getDistance()-1)*4+w));
                        }
                        else{
                            if(w==3 && r.getDistance()==1){
                                new_day.setDist(dist.get((r.getDistance()-1)*4+w));
                            }
                            else{
                                new_day.setDist(dist.get((r.getDistance()-1)*4+w)+ dist_range.get(count));
                                count+=1;
                            }
                        }
                        new_day.setChoose(true);
                    }
                    else{
                        new_day.setDist(0.0);
                        new_day.setChoose(false);
                    }
                    days.add(new_day);
                }

                WeekContent new_week = new WeekContent();
                new_week.setDays(days);
                new_week.setWeek(w+1);
                new_week.setLevel(r.getLevel());

                myplan.add(new_week);
            }
        }
    }
}



