package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.data;
import static android.net.wifi.SupplicantState.COMPLETED;
import static com.google.android.gms.auth.api.credentials.PasswordSpecification.da;

public class Running extends FragmentActivity
        implements
        LocationListener {
    /** Called when the activity is first created. */
    private final double EARTH_RADIUS = 6378137.0;
    private double cur_lat, cur_lng;
    private double distance = 0;
    private boolean flag_start = false;
    private int msec=0,sec=0,min=0,hour=0;
    Timer timer;
    private TextView txt_time, txt_distance, txt_loc;
    private Button btn_start, btn_finish;
    private LocationManager lms;
    private ArrayList<String> locations;
    private  LocationManager status;
    private Criteria criteria;
    public DatabaseReference mDatabase;
    public UserProfile userProfile;
    private String refreshedToken;

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
                        if( flag_start ==true ){
                            flag_start = false;
                            getUserProfile();

                        }

                    }
                });

        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            locationServiceInitial();

        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
        }

    }


    @Override
    public void onLocationChanged(Location location) {	//當地點改變時
        // TODO Auto-generated method stub
        //Toast.makeText(this, "location change", Toast.LENGTH_LONG).show();

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
        lms = (LocationManager) getSystemService(LOCATION_SERVICE);	//取得系統定位服務
        Location location;
        try {
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);//設置為最大精度
            location = status
                    .getLastKnownLocation(status.getBestProvider(criteria, true));
            //location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER);	//使用GPS定位座標
            getLocation(location);


        } catch (SecurityException e) {
            Toast.makeText(this, "requestLocationUpdate fails", Toast.LENGTH_SHORT).show();

        }

    }
    private void getLocation(Location location) {	//將定位資訊顯示在畫面中
        if(location != null) {
            Double longitude = location.getLongitude();	//取得經度
            Double latitude = location.getLatitude();	//取得緯度

            String s = "lng : "+ longitude + ", lat : "+latitude;
            txt_loc.setText(s);
            cur_lat = latitude;
            cur_lng = longitude;
        }
        else {
            Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
        }
    }

    private double getDistance(double new_lat, double new_lng) {
        double radLat1 = (new_lat * Math.PI / 180.0);
        double radLat2 = (cur_lat * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (new_lng - cur_lng) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private  void getUserProfile(){
        // get the userProfile by instanceId
        mDatabase.child("UserProfile").orderByChild("instanceid").equalTo(refreshedToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userProfile = dataSnapshot.getValue(UserProfile.class);
                    Log.d("userid",userProfile.getUserid());
                    // it's a bug for null because the listener is unstable

                    updateDistance();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateDistance(){
        mDatabase.child("UserProfile").orderByChild("instanceid").equalTo(refreshedToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userProfile = dataSnapshot.getValue(UserProfile.class);
                    Log.d("userid",userProfile.getUserid());

                    dataSnapshot.getRef().child("totaldistance").setValue(userProfile.updateDistance(distance));
                    Toast.makeText(Running.this, "恭喜你已完成本次跑步", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void init(){
        txt_time = (TextView) findViewById(R.id.txt_time_value);
        btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_start = (Button) findViewById(R.id.btn_start);
        txt_distance = (TextView) findViewById(R.id.txt_distance);
        txt_loc = (TextView) findViewById(R.id.txt_loc);

        timer =new Timer();
        timer.schedule(task, 0,10);
        locations = new ArrayList<String>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
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
                        locationServiceInitial();

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

