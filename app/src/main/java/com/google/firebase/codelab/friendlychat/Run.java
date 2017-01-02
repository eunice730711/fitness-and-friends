package com.google.firebase.codelab.friendlychat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.firebase.codelab.friendlychat.Day.DatetoString;

public class Run extends AppCompatActivity implements DataCommunication{

    private ViewPager mViewPager;

    //要共享的變數
    private double totalDistance;
    private boolean start;
    private boolean firstLocate;
    private UserProfile userProfile;
    public DatabaseReference mDatabase;


    @Override
    public double getTotalDistance() {
        return this.totalDistance;
    }

    @Override
    public void setTotalDistance(double distance) {
        this.totalDistance = distance;
    }

    @Override
    public boolean getStart() {
        return this.start;
    }

    @Override
    public void setStart(boolean start) {
        this.start = start;
    }

    @Override
    public boolean getFirstLocate() { return firstLocate; }

    @Override
    public void setFirstLocate(boolean firstLocate) { this.firstLocate = firstLocate; }

    @Override
    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    @Override
    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public DatabaseReference getDatabase() {
        return this.mDatabase;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.Run_tab);
        tabLayout.addTab(tabLayout.newTab().setText("Run"));
        tabLayout.addTab(tabLayout.newTab().setText("Map"));
        mViewPager = (ViewPager) findViewById(R.id.Run_viewpager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        final Run_PagerAdapter adapter = new Run_PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager.setAdapter(adapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        init();

        //上一頁
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        myToolbar.setNavigationIcon(R.drawable.ic_chevron_left_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAchieveGoal();
                Intent intent = new Intent();
                intent.setClass(Run.this, Home.class);
                startActivity(intent);
                Run.this.finish();
            }
        });

    }
    public void init(){
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO(Run.this);
        userProfile = profileIO.ReadFile();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        totalDistance = 0;
        start = false;
        firstLocate = false;

        //今日目標提醒
        android.app.AlertDialog alertDialog = GoalAlertDialog();
        alertDialog.show();

    }


    //今日目標提醒
    private android.app.AlertDialog GoalAlertDialog(){
        //產生一個Builder物件
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Run.this);
        //設定Dialog的標題
        builder.setTitle("Today goal");
        //設定Dialog的內容
        String message=null;
        ScheduleIO scheduleIO = new ScheduleIO(Run.this);
        Day day = scheduleIO.TodayJob();
        if( day ==null){
            message = "It's free today.";
        }
        else if(day.getComplete()){
            message = "Today's job is complete.";
        }
        else
            message = String.valueOf(day.getDist()) +" km";
        builder.setMessage(message);

        //設定Positive按鈕資料
        builder.setPositiveButton("GO!!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //按下按鈕時顯示快顯
            }
        });
        //利用Builder物件建立AlertDialog
        return builder.create();
    }

    //新建一個callback interface 裡面的func會等搜尋完firebase才會呼叫
    public interface isAchieve {
        //當搜尋完firebase中的record並加總今日之record後，如果距離大於今日的目標，則更新Schedule
        void scheduleUpdate(double recordDist,int time);
    }

    public void qureyRecord(final isAchieve callback){
        mDatabase.child("Record").child(userProfile.getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double recordTotalDist=0;
                int recordTotalTime=0;
                Calendar calendar = Calendar.getInstance();
                String now = DatetoString(calendar.getTime(),0);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Object o  = dataSnapshot.getValue(Object.class);
                    if(((HashMap<String,String>)o).get("date").equals(now)){
                        //加總今日record的距離及時間
                        String distance = ((HashMap<String,String>)o).get("distance");
                        String time = ((HashMap<String,String>)o).get("time");
                        recordTotalDist += Double.valueOf(distance);
                        recordTotalTime += Integer.valueOf(time);
                    }
                }
                callback.scheduleUpdate(recordTotalDist, recordTotalTime);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void isAchieveGoal(){

        qureyRecord(new isAchieve() {
            @Override
            public void scheduleUpdate(double recordDist ,int recordTime) {
                ScheduleIO scheduleIO = new ScheduleIO(Run.this);

                if(scheduleIO.IsComplete(recordTime,recordDist)){
                    Toast.makeText(Run.this,"恭喜你達成今日目標" ,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



}
