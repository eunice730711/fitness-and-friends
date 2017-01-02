package com.google.firebase.codelab.friendlychat;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


/**
 * Created by CatLin on 2016/12/24.
 */

public class Map_fragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private MapView mapView;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LatLng mLatLng, lastLatLng;
    private LocationManager manager;
    private PolylineOptions mPolylineOptions;
    private Marker currentMarker;
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;


    private TextView dd;
    private double totalDistance = 0;
    DataCommunication mCallback;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.map_fragment, container, false);

        dd = (TextView) returnView.findViewById(R.id.d);
        mapView = (MapView) returnView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();// needed to get the map to display immediately

        mPolylineOptions = new PolylineOptions();

        buildGoogleApiClient();


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                map.getUiSettings().setScrollGesturesEnabled(false);  // 禁止拖動 MAP
                map.getUiSettings().setZoomControlsEnabled(true);  // 右下角的放大縮小功能
                map.getUiSettings().setCompassEnabled(true);       // 左上角的指南針，要兩指旋轉才會出現
                map.getUiSettings().setMapToolbarEnabled(true);    // 右下角的導覽及開啟 Google Map功能


            }
        });

        return returnView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
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

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }



    @Override
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //更新位置
        lastLatLng = mLatLng;
        if(lastLatLng == null){
            lastLatLng = latLng;
        }
        mLatLng = latLng;

        if(getDistance() >= 10){
            mLatLng = lastLatLng;
        }

        if(mCallback.getStart()){
            //畫線 + 算距離
            totalDistance += getDistance();
            mCallback.setTotalDistance(totalDistance);
            dd.setText(String.valueOf(mCallback.getTotalDistance()));

            drawLine();
            //標記

            //移動地圖
            map.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(16));
        }
        //移動地圖
        map.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(16));
        if(currentMarker == null){
            currentMarker = map.addMarker(new MarkerOptions().position(mLatLng));
        }
        else{
            currentMarker.setPosition(latLng);
        }



    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {


        int permission = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION);

        if(permission != PackageManager.PERMISSION_GRANTED){
            //如果未授權，則請求授權
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
        }
        else{
            manager = (LocationManager) (getActivity().getSystemService(Context.LOCATION_SERVICE));

            // 如果沒開GPS會跳到設定頁面
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("Location is disabled")
                        .setMessage("Please enable your location")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 102);
                                getActivity().finish();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
            else {
                mGoogleApiClient.connect();
                LocationRequest mLocationRequest = createLocationRequest();
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            //mGoogleApiClient.connect();
        }


    }

    //google 連線中斷
    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }



    // 建立Google API用戶端物件
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this).addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    // 建立Location request物件 設定讀取的時間間隔、修先讀取的資訊 等
    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    //android 6.0 以上的授權請求
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mGoogleApiClient.connect();

                } else {
                    mGoogleApiClient.disconnect();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    //檢查 是否有某一種定位已經打開
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    || manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {

                mGoogleApiClient.connect();

            }
        }
    }

    public void drawLine(){
        mPolylineOptions
                .add(mLatLng)
                .color(Color.BLUE)
                .width(20);

        map.addPolyline(mPolylineOptions);
    }

    private double getDistance() {

        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
        Location loc2 = new Location(LocationManager.GPS_PROVIDER);

        loc1.setLatitude(lastLatLng.latitude);
        loc1.setLongitude(lastLatLng.longitude);

        loc2.setLatitude(mLatLng.latitude);
        loc2.setLongitude(mLatLng.longitude);

        return loc1.distanceTo(loc2);
    }

    //fragment共享資料
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

}
