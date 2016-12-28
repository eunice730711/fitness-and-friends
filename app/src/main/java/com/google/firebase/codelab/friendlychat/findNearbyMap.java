package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.codelab.friendlychat.R.id.map;
import static com.google.firebase.codelab.friendlychat.R.id.userImageView;

public class findNearbyMap extends Fragment {
    private  LocationManager status;
    private Criteria criteria;
    public DatabaseReference mDatabase;
    public UserProfile userProfile;
    private Location location;
    private Toast toast;
    private double lat, lng;
    MapView mapView;

    private Bitmap theBitmap;
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();


    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    /*
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            View v = getView();
        }
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.activity_find_nearby, container, false);
        mapView = (MapView)  returnView.findViewById(R.id.findMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();// needed to get the map to display immediately

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                // Add a marker in Sydney, Australia, and move the camera.
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                map.getUiSettings().setZoomControlsEnabled(true);
                CameraUpdate center =
                        CameraUpdateFactory.newLatLngZoom(new LatLng(24.7859195, 120.9945463), 15);
                map.animateCamera(center);

                map.getUiSettings().setZoomControlsEnabled(true);  // 右下角的放大縮小功能
                map.getUiSettings().setCompassEnabled(true);       // 左上角的指南針，要兩指旋轉才會出現
                map.getUiSettings().setMapToolbarEnabled(true);    // 右下角的導覽及開啟 Google Map功能

                //標示現在位置
                MarkerOptions markerOpt = new MarkerOptions();
                markerOpt.position(new LatLng(24.7859195,120.9945463));
                markerOpt.title("現在位置");
                theBitmap = null;

                Threading t = new Threading();
                t.start();
                /*
                try {
                    URL url = new URL(userProfile.getUserphoto());
                    theBitmap = BitmapFactory.decodeStream(url.openStream());

                    //theBitmap = BitmapFactory.decodeStream( (InputStream)url.getContent());
                    //theBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), url);
                    Log.e("bitmap","ok");
                } catch(Exception e) {
                    Log.e("bitmap","Caught IOException: " + e.getMessage());
                }*/
                try {
                    t.join();
                } catch(Exception e) {
                    Log.e("bitmap","Caught IOException: " + e.getMessage());
                }
                markerOpt.icon(BitmapDescriptorFactory.fromBitmap(theBitmap));
                map.addMarker(markerOpt).showInfoWindow();
            }
        });
        return  returnView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lat= 24.7859195;
        lng=120.9945463;
        init();
/*
        status = (LocationManager)  (getActivity().getSystemService(Context.LOCATION_SERVICE));
        if (status.isProviderEnabled(LocationManager.PASSIVE_PROVIDER) || status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()初始化
            locationServiceInitial();
        } else {

            if(toast == null){
                toast = Toast.makeText( getActivity(),"請開啟定位服務" ,Toast.LENGTH_SHORT);
            }
            else{
                toast.setText("請開啟定位服務");
            }
            toast.show();
        }


*/

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

            lat = location.getLatitude();
            lng = location.getLongitude();
            lat= 24.7859195;
            lng=120.9945463;
            Log.e("GPS",lat+", "+lng);
        } catch (SecurityException e) {
            Toast.makeText( getActivity(), "requestLocationUpdate fails", Toast.LENGTH_SHORT).show();
        }
    }

    private void init(){
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO( getActivity());
        userProfile = profileIO.ReadFile();

    }
    public class Threading extends Thread {
        public void run() {
            try {
                URL url = new URL(userProfile.getUserphoto());
                theBitmap = BitmapFactory.decodeStream(url.openStream());

            }catch(Exception e) {
                Log.e("bitmap","Caught IOException: " + e.getMessage());
            }
        }
    }


}
