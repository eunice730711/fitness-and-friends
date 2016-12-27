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
import android.view.KeyEvent;
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

import java.text.DecimalFormat;
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

public class Running extends FragmentActivity{
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
    private MyLocationListener locationListener;
    private double last_lat, last_lng;

    //private Firebase mRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
        locationListener = new MyLocationListener(Running.this);
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

        if (status.isProviderEnabled(LocationManager.PASSIVE_PROVIDER) || status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()初始化
            locationServiceInitial();

        } else {

            if(toast == null){
                toast = Toast.makeText(this,"請開啟定位服務" ,Toast.LENGTH_SHORT);
            }
            else{
                toast.setText("請開啟定位服務");
            }
            toast.show();

            //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
        }



    }

    private void locationServiceInitial() {

        try {
            criteria = new Criteria();

            criteria.setAccuracy(Criteria.ACCURACY_FINE);//設置為最大精度
            criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
            //location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER);	//使用GPS定位座標

            location = status.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = status.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            last_lat = location.getLatitude();
            last_lng = location.getLongitude();

            //註冊 MylocationListener
            status.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
            status.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);

            //last_lng = status.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
            //last_lat = status.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();


        } catch (SecurityException e) {
            Toast.makeText(this, "requestLocationUpdate fails", Toast.LENGTH_SHORT).show();

        }
    }

    private void Freelistener(){
        try {
            status.removeUpdates(locationListener);

        } catch (SecurityException e) {
            Toast.makeText(this, "remove fails", Toast.LENGTH_SHORT).show();

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
        //
        if(toast == null){
            toast = Toast.makeText(Running.this,"恭喜你已完成本次跑步" ,Toast.LENGTH_SHORT);
        }
        else{
            toast.setText("恭喜你已完成本次跑步");
        }
        toast.show();

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
                // to do 要改成取得當天日期的函式
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
        ScheduleIO scheduleIO = new ScheduleIO(Running.this);
        Day day = scheduleIO.TodayJob();
        if( day ==null){
            message = "It's free today.";
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

    private void init(){
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
                    txt_time.setText(String.valueOf(s));
                    if(msec <=100) {
                        double last_lat = cur_lat, last_lng = cur_lng;
                        // 進行GPS定位


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

    class MyLocationListener implements LocationListener{
        private Context c;

        public MyLocationListener(Context c){
            this.c = c;
        }

        @Override
        public void onLocationChanged(Location location) {
            if(location.getAccuracy() <= 30){
                Double longitude = location.getLongitude();	//取得經度
                Double latitude = location.getLatitude();	//取得緯度

                String s = "lng : "+ longitude + ", lat : "+latitude;
                txt_loc.setText(s);
                cur_lat = latitude;
                cur_lng = longitude;

                // 進行GPS定位
                if( last_lat != cur_lat || last_lng !=cur_lng){
                    TextView t = (TextView)findViewById(R.id.change);
                    t.setText("changed!!!");
                }
                String loc = cur_lng + "," + cur_lat;
                locations.add(loc);
                distance += getDistance(last_lat, last_lng);

                txt_distance.setText( String.format("%.2f",distance) + "m");

                last_lat = cur_lat;
                last_lng = cur_lng;


                if(toast == null){
                    toast = Toast.makeText(c,"location change" ,Toast.LENGTH_SHORT);
                }
                else{
                    toast.setText("location change");
                }
                toast.show();

            }





        }
        @Override
        public void onProviderDisabled(String provider) {
            //當所選的Location Provider不可用時 調用

            if(toast == null){
                toast = Toast.makeText(c,"無法定位座標" ,Toast.LENGTH_SHORT);
            }
            else{
                toast.setText("無法定位座標");
            }
            toast.show();

        }
        @Override
        public void onProviderEnabled(String provider) {
            //當所選的Location Provider可用時 調用
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //當定位狀況改變時調用
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按上一頁  會移除LocationListener

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Freelistener();
            Intent intent1 = new Intent();
            intent1.setClass(this, Home.class);
            startActivity(intent1);
            Running.this.finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}

