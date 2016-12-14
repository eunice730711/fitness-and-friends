package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.data;
import static android.R.id.message;
import static android.net.wifi.SupplicantState.COMPLETED;
import static android.os.Build.VERSION_CODES.M;
import static com.google.android.gms.auth.api.credentials.PasswordSpecification.da;
import static com.google.firebase.codelab.friendlychat.Day.DatetoString;

public class Running extends FragmentActivity
        implements
        LocationListener {
    /** Called when the activity is first created. */
    private final double EARTH_RADIUS = 6378137.0;
    public static final String ANONYMOUS = "anonymous";
    private String personEmail;
    private double cur_lat, cur_lng;
    private double distance = 0;
    private boolean flag_start = false;
    private int msec=0,sec=0,min=0,hour=0;
    private Timer timer;
    private TextView txt_time, txt_distance, txt_loc;
    private Button btn_start, btn_finish;
    private ArrayList<String> locations;
    private  LocationManager status;
    private Criteria criteria;
    public DatabaseReference mDatabase;
    public UserProfile userProfile;
    private Location location;
    private Toast toast;
    //private Firebase mRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private ScheduleIO scheduleIO ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        init();

        btn_start.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(  flag_start ==false) {
                            flag_start = true;
                        }
                    }
                });

        btn_finish.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 當按下start按鈕後，finish按鈕才會有反應
                        if( flag_start ==true ){
                            flag_start = false;
                            update();
                            // 結束時，對使用者的總里程數累加
                        }

                    }
                });

        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            locationServiceInitial();

        } else {

            if(toast == null){
                toast = Toast.makeText(this,"請開啟定位服務" ,Toast.LENGTH_SHORT);
            }
            else{
                toast.setText("請開啟定位服務");
            }
            toast.show();

            //Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
        }

    }


    @Override
    public void onLocationChanged(Location location) {	//當地點改變時
        // TODO Auto-generated method stub
        Toast.makeText(this, "location change", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProviderDisabled(String arg0) {	//當GPS或網路定位功能關閉時
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {	//當GPS或網路定位功能開啟
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {	//定位狀態改變
        //status=OUT_OF_SERVICE 供應商停止服務
        //status=TEMPORARILY_UNAVAILABLE 供應商暫停服務
    }
    private void locationServiceInitial() {

        try {
            criteria = new Criteria();

            criteria.setAccuracy(Criteria.ACCURACY_FINE);//設置為最大精度
            criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
            //location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER);	//使用GPS定位座標
            getLocation();


        } catch (SecurityException e) {
            Toast.makeText(this, "requestLocationUpdate fails", Toast.LENGTH_SHORT).show();

        }

    }
    // 每十毫秒會更新GPS
    private void getLocation() {	//將定位資訊顯示在畫面中

            try {
                location = status
                        .getLastKnownLocation(status.getBestProvider(criteria, true));
            }catch (SecurityException e) {
                Toast.makeText(this, "requestLocationUpdate fails", Toast.LENGTH_SHORT).show();

            }
        if(location != null) {
            Double longitude = location.getLongitude();	//取得經度
            Double latitude = location.getLatitude();	//取得緯度

            String s = "lng : "+ longitude + ", lat : "+latitude;
            txt_loc.setText(s);
            cur_lat = latitude;
            cur_lng = longitude;
        }
        else {

            if(toast == null){
                toast = Toast.makeText(this,"無法定位座標" ,Toast.LENGTH_SHORT);
            }
            else{
                toast.setText("無法定位座標");
            }
            toast.show();

            //Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
        }
    }

    private double getDistance(double last_lat, double last_lng) {
        double radLat1 = (last_lat * Math.PI / 180.0);
        double radLat2 = (cur_lat * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (last_lng - cur_lng) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private void update(){
        // 修改里程數
        updateDistance();
        // 上傳本次紀錄
        updateRecord();
        // 更新檔案 : 是否完成本日schedute
        scheduleIO.UpdateComplete();

        if(toast == null){
            toast = Toast.makeText(Running.this,"恭喜你已完成本次跑步" ,Toast.LENGTH_SHORT);
        }
        else{
            toast.setText("恭喜你已完成本次跑步");
        }
        toast.show();

        //Toast.makeText(Running.this, "恭喜你已完成本次跑步", Toast.LENGTH_LONG).show();
    }

    private void updateDistance(){

        mDatabase.child("UserProfile").orderByChild("userid").equalTo(userProfile.getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userProfile = dataSnapshot.getValue(UserProfile.class);
                    Log.d("userid",userProfile.getUserid());

                    // 更新總距離
                    dataSnapshot.getRef().child("totaldistance").setValue(userProfile.updateDistance(distance));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void updateRecord(){
        mDatabase.child("Record").child(userProfile.getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Object> map = new HashMap();
                Calendar calendar = Calendar.getInstance();
                String now = DatetoString(calendar.getTime(),0);
                map.put("date",now);
                map.put("distance", String.valueOf(distance));
                // 每次跑步紀錄的時間以分鐘為單位
                map.put("time", String.valueOf(hour*60+min+sec/60));
                snapshot.getRef().push().updateChildren(map);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private AlertDialog GoalAlertDialog(){
        //產生一個Builder物件
        Builder builder = new AlertDialog.Builder(Running.this);
        //設定Dialog的標題
        builder.setTitle("Today goal");
        //設定Dialog的內容
        String message=null;
        Day day = scheduleIO.TodayJob();
        if( day.getDist() ==0 && day.getTime() ==0){
            message = "It's free today.";
        }
        else if( day.getTime() ==0)
            //  中階
            message = String.valueOf(day.getDist()) +" km";
        else if( day.getDist() ==0)
            // 初階
            message = String.valueOf(day.getTime()) + "minutes";
        else
            message = "File doesn't exist";
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

    private void init(){
        scheduleIO = new ScheduleIO(Running.this);
        AlertDialog alertDialog = GoalAlertDialog();
        alertDialog.show();
        txt_time = (TextView) findViewById(R.id.txt_time_value);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_start = (Button) findViewById(R.id.btn_start);
        txt_distance = (TextView) findViewById(R.id.txt_distance);
        txt_loc = (TextView) findViewById(R.id.txt_loc);

        // 設定計時器，每10毫秒會對碼表修正時間
        timer =new Timer();
        timer.schedule(task, 0,10);
        locations = new ArrayList<String>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO(Running.this);
        userProfile = profileIO.ReadFile();

    }

    private Handler handler = new Handler(){
        public  void  handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case 1:

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
                    txt_time.setText(String.valueOf(s));
                    if(msec <=100) {
                        double last_lat = cur_lat, last_lng = cur_lng;
                        // 進行GPS定位
                        getLocation();
                        if( last_lat != cur_lat || last_lng !=cur_lng){
                            TextView t = (TextView)findViewById(R.id.change);
                            t.setText("changed!!!");
                        }
                        String loc = cur_lng + "," + cur_lat;
                        locations.add(loc);
                        distance += getDistance(last_lat, last_lng);
                        txt_distance.setText(String.valueOf(distance) + "m");
                    }
                    break;
            }

        }

    };
    private TimerTask task = new TimerTask(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message message = new Message();

            if (flag_start){
                //如果startflag==true則每秒tsec+1
                msec+=10;

                //傳送訊息1
                message.what =1;
                handler.sendMessage(message);



            }
        }

    };


}

