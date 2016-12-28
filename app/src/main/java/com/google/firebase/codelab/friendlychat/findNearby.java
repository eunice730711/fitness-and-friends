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
import android.support.v4.app.FragmentActivity;
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

public class findNearby extends FragmentActivity {
    private  LocationManager status;
    private Criteria criteria;
    public DatabaseReference mDatabase;
    public UserProfile userProfile;
    private Location location;
    private Toast toast;
    private double lat, lng;
    MapView mapView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lat= 24.7859195;
        lng=120.9945463;
        init();

        status = (LocationManager)  getSystemService(Context.LOCATION_SERVICE);
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

            lat = location.getLatitude();
            lng = location.getLongitude();
            lat= 24.7859195;
            lng=120.9945463;
            Log.e("GPS",lat+", "+lng);
        } catch (SecurityException e) {
            Toast.makeText( this, "requestLocationUpdate fails", Toast.LENGTH_SHORT).show();
        }
    }

    private void init(){
        // 從file取得 使用者資料
        ProfileIO profileIO = new ProfileIO( this);
        userProfile = profileIO.ReadFile();

    }



}
