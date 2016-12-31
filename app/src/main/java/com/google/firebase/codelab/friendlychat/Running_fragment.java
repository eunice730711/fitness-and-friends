package com.google.firebase.codelab.friendlychat;


import android.content.Context;
import android.os.*;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.firebase.codelab.friendlychat.Day.DatetoString;

/**
 * Created by CatLin on 2016/12/24.
 */

public class Running_fragment extends Fragment{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private TextView dist_text, time_text;
    private Button start_buttuon;
    private int msec=0,sec=0,min=0,hour=0;
    private Timer timer;

    private Toast toast;

    DataCommunication mCallback;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();

        start_buttuon.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mCallback.getStart()) {
                            mCallback.setStart(true);
                            start_buttuon.setText("Finish");
                        }
                        else {
                            mCallback.setStart(false);
                            start_buttuon.setText("Start");
                            update();
                        }
                    }
                });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.running_fragment, container, false);

        dist_text = (TextView) returnView.findViewById(R.id.Run_dist_text);
        time_text = (TextView) returnView.findViewById(R.id.Run_time_text);
        start_buttuon = (Button) returnView.findViewById(R.id.Run_start);

        timer =new Timer();
        timer.schedule(task, 0,10);

        return returnView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mCallback = (DataCommunication) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DataCommunication");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //切換fragment時刷新
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mCallback!=null && dist_text!=null) {
            String d = String.format("%.2f",mCallback.getTotalDistance()/1000);
            dist_text.setText(d);
        }
    }

    //計時器及刷新頁面
    private Handler handler = new Handler(){
        public  void  handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case 1:{
                    if(msec >=1000) {
                        sec++;
                        msec -=1000;
                    }
                    if(sec >= 60){
                        min ++;
                        sec -=60;
                    }
                    if(min >= 60){
                        hour ++;
                        min -= 60;
                    }
                    String s = String.format("%02d:%02d:%02d.%02d",hour,min,sec,msec/10);
                    time_text.setText(String.valueOf(s));
                    if(msec <=100) {
                        String d = String.format("%.2f",mCallback.getTotalDistance()/1000);
                        dist_text.setText(d);
                    }
                    break;
                }
            }

        }

    };
    private TimerTask task = new TimerTask(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            android.os.Message message = new android.os.Message();

            if (mCallback.getStart()){
                //如果startflag==true則每秒tsec+1
                msec+=10;

                //傳送訊息1
                message.what =1;
                handler.sendMessage(message);

            }
        }

    };


    private void update(){
        // 修改里程數
        updateDistance();
        // 上傳本次紀錄
        updateRecord();

        if(toast == null){
            toast = Toast.makeText(getActivity(),"恭喜你已完成本次跑步" ,Toast.LENGTH_SHORT);
        }
        else{
            toast.setText("恭喜你已完成本次跑步");
        }
        toast.show();

    }

    private void updateDistance(){

        mCallback.getDatabase().child("UserProfile").orderByChild("userid").equalTo(mCallback.getUserProfile().getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mCallback.setUserProfile(dataSnapshot.getValue(UserProfile.class));
                    Log.d("userid",mCallback.getUserProfile().getUserid());

                    // 更新總距離
                    dataSnapshot.getRef().child("totaldistance").setValue(mCallback.getUserProfile().updateDistance(mCallback.getTotalDistance()/1000));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void updateRecord(){
        mCallback.getDatabase().child("Record").child(mCallback.getUserProfile().getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> map = new HashMap();
                // to do 要改成取得當天日期的函式
                Calendar calendar = Calendar.getInstance();
                String now = DatetoString(calendar.getTime(),0);
                map.put("date",now);
                map.put("distance", String.valueOf(mCallback.getTotalDistance()/1000));
                // 每次跑步紀錄的時間以分鐘為單位
                map.put("time", String.valueOf(hour*60+min+sec/60));
                snapshot.getRef().push().updateChildren(map);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
