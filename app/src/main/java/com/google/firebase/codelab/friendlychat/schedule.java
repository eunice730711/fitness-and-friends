package com.google.firebase.codelab.friendlychat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.Calendar;
import java.util.List;

public class schedule extends AppCompatActivity {

    private RecyclerView weeksRecyclerView;
    private LinearLayoutManager layoutManager;
    private WeekAdapter adapter;
    private List<String> weekList = new ArrayList<String>();

    private List <WeekContent> myplan = new ArrayList<WeekContent>();

    private ScheduleIO fileIO = new ScheduleIO(schedule.this);

    private Calendar now = Calendar.getInstance();
    private int now_of_week = set_Now_of_week();

    Result r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = this.getIntent();
        r = (Result) intent.getSerializableExtra("schedule");


        //初始化RecyclerView用的layoutManager
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // 01 init and display every week
        weeksRecyclerView = (RecyclerView) findViewById(R.id.weeks_RecyclerView);
        for(int i=1;i<=4;i++){
            weekList.add("Week "+ String.valueOf(i) );
        }


        // 02 Read the old plan from a file in your mobile phone,
        //    or create new one and write new file.
        if(r!=null){
            setPlan();
            fileIO.WriteFile(myplan);
            fileIO.WriteFirebase(myplan);

        }
        else{
            myplan = fileIO.ReadFile();
        }

        if(myplan!=null && myplan.size() == 5){
            weekList.add("Week 5");
        }

//        Day da = fileIO.TodayJob();
//        if(da != null ){
//            Toast.makeText(schedule.this, "here", Toast.LENGTH_LONG).show();
//        }




        // 03 This is my own WeekAdapter which is used to display the custom ListView.
        adapter = new WeekAdapter(weekList,myplan);
        weeksRecyclerView.setLayoutManager(layoutManager);
        weeksRecyclerView.setAdapter(adapter);

        weeksRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getApplicationContext(), weeksRecyclerView, new RecyclerViewTouchListener.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(getApplicationContext(), weekList.get(position) + " is clicked!", Toast.LENGTH_SHORT).show();

                if(myplan.size()!=0 ){
                    Intent intent3 = new Intent();
                    intent3.setClass(schedule.this, WeekPlan.class);

                    // 05 用最後一周的 var week 去代表哪一周被按，因為要丟全部的plan到下一頁。
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
                else
                {
                    Toast.makeText(
                            schedule.this,
                            "尚未建立運動計畫\n請點擊右上角扳手建立",
                            Toast.LENGTH_LONG).show();
                }
            }



            @Override
            public void onLongClick(View view, int position) {
                //處理長按事件
                //Toast.makeText(getApplicationContext(), weekList.get(position) + " is long pressed!", Toast.LENGTH_SHORT).show();
            }
        }));



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

            if(myplan.size()!=0){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("是否重新設定運動計畫?")
                        .setNegativeButton("是", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //fileIO.DeleteFile();
                                Intent intent1 = new Intent();
                                intent1.setClass(schedule.this, scheduleRunLevel.class);

//                                Result r = new Result();
//                                Bundle bundle = new Bundle();
//                                r.setType(1);
//                                bundle.putSerializable("run",r);
//                                intent1.putExtras(bundle);

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
            else{
                Intent intent1 = new Intent();
                intent1.setClass(this, scheduleRunLevel.class);

//                Result r = new Result();
//                Bundle bundle = new Bundle();
//                r.setType(1);
//                bundle.putSerializable("run",r);
//                intent1.putExtras(bundle);

                startActivity(intent1);
                schedule.this.finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void setPlan(){
        //以user剛剛選擇的plan來設定Plan

        int end;

        List<Integer> time_range = new ArrayList<Integer>(Arrays.asList(10,15,12,15,15,12,10));
        List<Double> dist_range = new ArrayList<Double>(Arrays.asList(0.0,0.5,0.25,0.5,0.5,0.25,0.0));
        List<Double> dist = new ArrayList<Double>(Arrays.asList(1.6,2.1,2.5,3.0,
                3.0,3.5,4.0,4.5,
                5.0,5.5,6.0,6.5,
                7.0,7.5,8.0,8.5,
                9.0,9.5,10.0,10.5));

        //----設定會有幾周
        if(ifFiveWeek()) end = 5;
        else end = 4;

        int count=0;
        int week_count=0;
        boolean isRepeat = true;
        Calendar set_date = Calendar.getInstance();

        for(int w=0; w<end ; w++){
            List<Day> days = new ArrayList<Day>();

            int s=0;
            int f=7;

            //如果有切掉user選的星期，week1中被切掉的部分塞0
            if(w == 0 && ifFiveWeek()){
                for(int k=0; k<now_of_week-1; k++){
                    Day new_day = new Day();

                    set_date = Calendar.getInstance();
                    set_date.add(Calendar.DATE, -(now_of_week-1-k));

                    new_day.setDate(set_date.getTime());
                    new_day.setDist(0.0);
                    new_day.setComplete(false);
                    new_day.setTime(0);
                    new_day.setChoose(false);
                    days.add(new_day);
                }
                s = now_of_week-1;
            }
            else if(w == 4){
                //如果開始填第五周
                f = now_of_week-1;
            }
            else if(w == 0 && !ifFiveWeek()){
                //如果沒有第五周 而且現在開始填第一周的計畫時
                set_date = Calendar.getInstance();
                for(int k=0; k<now_of_week; k++){
                    set_date.add(Calendar.DATE, -1);
                }
            }

            //根據幾周開始塞計畫
            for(int i=s; i<f; i++){
                set_date.add(Calendar.DATE, 1);

                Day new_day = new Day();
                new_day.setDate(set_date.getTime());

                //初階會設定Time,dist=0  中階會設定Dist,Time=0
                switch (r.getLevel()){
                    case 1:
                        new_day.setDist(0.0);
                        break;
                    case 2:
                        new_day.setTime(0);
                        break;
                }
                new_day.setComplete(false);

                //如果那天有被user選擇要跑
                if(r.getDays().charAt(i) == '1'){
                    //如果現在是一周計畫的最後一天
                    if((count%r.countDays()) == r.countDays()-1){
                        switch (r.getLevel()){
                            case 1:{
                                new_day.setTime(10+5*week_count);
                                if(isRepeat && week_count == 1){
                                    isRepeat = false;
                                }
                                else{
                                    week_count+=1;
                                }
                                count+=1;
                            }break;
                            case 2:{
                                new_day.setDist(dist.get((r.getDistance()-1)*4+week_count));
                                week_count+=1;
                                count+=1;
                            }break;
                        }
                    }
                    else{
                        switch (r.getLevel()){
                            case 1:
                                new_day.setTime(time_range.get((count%r.countDays()))+5*week_count);
                                count+=1;
                                break;
                            case 2:
                                if(w==3 && r.getDistance()==1){
                                    new_day.setDist(dist.get((r.getDistance()-1)*4+week_count));
                                }
                                else{
                                    new_day.setDist(dist.get((r.getDistance()-1)*4+week_count)+ dist_range.get((count%r.countDays())));
                                    count+=1;
                                }
                                break;
                        }
                    }
                    new_day.setChoose(true);
                }
                else{
                    switch (r.getLevel()){
                        case 1:
                            new_day.setTime(0);
                            break;
                        case 2:
                            new_day.setDist(0.0);
                            break;
                    }
                    new_day.setChoose(false);
                }
                days.add(new_day);
            }

            if(w == 4){
                //第五周後面都補0
                for(int k=0; k<(7-now_of_week+1); k++){
                    Day new_day = new Day();

                    set_date.add(Calendar.DATE, 1);

                    new_day.setDate(set_date.getTime());
                    new_day.setDist(0.0);
                    new_day.setComplete(false);
                    new_day.setTime(0);
                    new_day.setChoose(false);
                    days.add(new_day);
                }
            }

            WeekContent new_week = new WeekContent();
            new_week.setDays(days);
            new_week.setWeek(w+1);
            new_week.setLevel(r.getLevel());

            myplan.add(new_week);
        }

    }

    public boolean ifFiveWeek(){
        //true = 有到第五周 false = 沒有第五周(即四周結束)

        boolean check = false;

        for(int i=0; i<now_of_week-1; i++){
            if(r.getDays().charAt(i) == '1'){
                check = true;
            }
        }
        return check;
    }

    public int set_Now_of_week(){
        //預設第一天禮拜天，所以要-1 (1 2 3 4 5 6 0) => (1 2 3 4 5 6 7)

        boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
        int weekDay = now.get(Calendar.DAY_OF_WEEK);
        if(isFirstSunday){
            weekDay = weekDay - 1;
            if(weekDay == 0){
                weekDay = 7;
            }
        }
        return weekDay;
    }

}


