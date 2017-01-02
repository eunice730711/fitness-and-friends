package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


import de.hdodenhof.circleimageview.CircleImageView;


public class findNearby extends FragmentActivity {
    private  LocationManager status;
    private Criteria criteria;
    private UserProfile userProfile;
    private Location location;
    private Toast toast;
    private double lat, lng;
    private DatabaseReference mDatabase;
    private ArrayList list;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private final double nearDist = 500000;
    private final double EARTH_RADIUS = 6378137.0;
    private boolean first = true;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;

    public static class NearbyViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_friendid, txt_friendname, txt_dist;
        public Button btn_friendProfile;
        public CircleImageView nearbyImageView;
        public UserProfile userProfile;
        public String friendid;
        private View view;

        public NearbyViewHolder(View v) {
            super(v);
            view =v ;
            txt_friendid = (TextView) itemView.findViewById(R.id.txt_friendid);
            txt_friendname = (TextView) itemView.findViewById(R.id.txt_friendname);
            btn_friendProfile = (Button) itemView.findViewById(R.id.btn_friendProfile);
            nearbyImageView= (CircleImageView) itemView.findViewById(R.id.nearbyImageView);
            txt_dist = (TextView) itemView.findViewById(R.id.txt_dist);

            btn_friendProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view = v;
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                    ProfileIO profileIO = new ProfileIO( view.getContext());
                    userProfile = profileIO.ReadFile();
                    // 查看 你們是否有好友關係
                    mDatabase.child("Friend").child(userProfile.getUserid()).getRef()
                            .orderByChild("friendid").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Boolean f = false;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String id = ((HashMap)dataSnapshot.getValue()).get("friendid").toString();
                                if( id.compareTo(friendid)==0){
                                    // 傳送好友ID 到好友頁面
                                    Intent intent = new Intent(view.getContext(), FriendProfile.class);
                                    intent.putExtra("friendid", friendid);
                                    view.getContext().startActivity(intent);
                                    f = true;
                                }
                            }
                            if( f == false){
                                //  邀請內容的個人頁面介紹
                                Intent i = new Intent(view.getContext(), SearchProfile.class);
                                i.putExtra("friendid", friendid);
                                view.getContext().startActivity(i);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearby);
        //  給予一個固定GPS , 以免虛擬機找不到GPS

        init();

        getGPS();
        getNearUsers();

    }



    private void init(){
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO( findNearby.this);
        userProfile = profileIO.ReadFile();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = (RecyclerView) findViewById(R.id.NearbyRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        // 顯示最新資訊，由上而下
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        requestAccess();
    }

    private void requestAccess(){
        int permission = ContextCompat.checkSelfPermission(findNearby.this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if(permission != PackageManager.PERMISSION_GRANTED){
            //如果未授權，則請求授權
            ActivityCompat.requestPermissions(findNearby.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
        }
        else {
            status = (LocationManager) (findNearby.this.getSystemService(Context.LOCATION_SERVICE));
        }
    }

    private void getNearUsers(){
        mDatabase.child("Nearby").orderByChild("gps").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list = new ArrayList();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HashMap map = (HashMap<String, String>) dataSnapshot.getValue(Object.class);
                    //  過濾自己
                    if( map.get("userid").toString().compareTo(userProfile.getUserid())==0)
                        continue;
                    double dist = getDistance(map.get("gps").toString());
                    // 太遠
                    if( dist > nearDist )
                        continue;
                    map.put("dist",dist);
                    list.add(map);
                }

                // 依距離排序

                Collections.sort(list,
                        new Comparator<HashMap<String, Double>>() {
                            // 由小排到大
                            public int compare(HashMap<String, Double>o1, HashMap<String, Double> o2) {
                                double d1 = Double.valueOf(o1.get("dist"));
                                double d2 = Double.valueOf(o2.get("dist"));
                                if (d1 >d2)
                                    return -1;
                                else if (d1 ==d2)
                                    return 0;
                                else
                                    return  1;
                            }
                        });
                int end = 7;
                if(list.size() <7)
                    end = list.size();
                ArrayList nearby = new ArrayList(list.subList(0,end));


                JsAdapter adapter = new JsAdapter(nearby){
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        // 設定viewHolder 所使用的 layout
                        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nearbyuser, parent, false);
                        NearbyViewHolder vh = new NearbyViewHolder(mView);
                        return vh;
                    }
                    @Override
                    public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {
                        NearbyViewHolder viewHolder = (NearbyViewHolder) holder;

                        HashMap map = (HashMap)iList.get(position);
                        viewHolder.friendid = map.get("userid").toString();

                        viewHolder.userProfile = userProfile;
                        viewHolder.txt_friendid.setText(map.get("userid").toString());
                        viewHolder.txt_friendname.setText(map.get("username").toString());
                        viewHolder.txt_dist.setText(map.get("dist").toString()+" m");
                        Glide.with(findNearby.this)
                                .load(map.get("userphotourl"))
                                .into(viewHolder.nearbyImageView);

                    }
                };
                mRecyclerView.setAdapter(adapter);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void pushGPStoDB(){
        HashMap<String, String> map = new HashMap<String, String>();
        String gps = String.valueOf(lat)+","+String.valueOf(lng);
        map.put("userid",userProfile.getUserid());
        map.put("username",userProfile.getUsername());
        map.put("userphotourl",userProfile.getUserphoto());
        map.put("gps",gps);

        // 對 Nearby table　寫入我的個人資料以及我的現在GPS
        mDatabase.child("Nearby").push().setValue(map);
    }

    private void getGPS(){

        status = (LocationManager)  getSystemService(Context.LOCATION_SERVICE);
        if (status.isProviderEnabled(LocationManager.PASSIVE_PROVIDER) || status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()初始化
            locationServiceInitial();
        } else {
            if(toast == null){
                toast = Toast.makeText(this,"請開啟定位服務" ,Toast.LENGTH_SHORT);
            } else{
                toast.setText("請開啟定位服務");
            }
            toast.show();
        }
    }

    private void locationServiceInitial() {

        try {
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);//設置為最大精度
            criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);

            location = status.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = status.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            else {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
            Log.e("GPS",lat+", "+lng);

            pushGPStoDB();
        } catch (SecurityException e) {
            Toast.makeText( this, "requestLocationUpdate fails", Toast.LENGTH_SHORT).show();
        }
    }

    private double getDistance(String otherGPS) {
        double other_lat, other_lng;
        String[] gps= otherGPS.split(",");
        other_lat = Double.valueOf(gps[0]);
        other_lng = Double.valueOf(gps[1]);
        double radLat1 = (other_lat * Math.PI / 180.0);
        double radLat2 = (lat * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (other_lng - lng) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
    @Override
    public void onPause() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        // 從 Nearby table 刪除自己的GPS紀錄

        mDatabase.child("Nearby").orderByChild("userid").equalTo(userProfile.getUserid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // 找到viewholder對應的 requester
                    dataSnapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        super.onPause();


    }
    public void onResume() {
        if(first)
            first = false;
        else
            pushGPStoDB();
        super.onResume();

    }
}
