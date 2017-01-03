//package com.haloteam.imess.service;
//
//import android.Manifest;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.ActivityCompat;
//import android.util.Log;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.haloteam.imess.common.Constant;
//import de.greenrobot.event.EventBus;
//
///**
// * Created by Hau-Do on 26/12/2016.
// */
//public class LocationService extends Service {
//    private LocationManager locationManager;
//    private MyLocationListener myLocationListener;
//    public DatabaseReference rootUrl;
//    private DatabaseReference urlCurrenUser;
//    private FirebaseAuth mFirebaseAuth;
//    private FirebaseAuth.AuthStateListener mAuthStateListener;
//    public static double lattitude=0;
//    public static double longitude=0;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        this.myLocationListener = new MyLocationListener();
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        rootUrl = FirebaseDatabase.getInstance().getReference();
//        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                setAuthenticatedUser(firebaseAuth);
//            }
//        };
//        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, myLocationListener);
//    }
//
//    private void setAuthenticatedUser(FirebaseAuth firebaseAuth) {
//        if (firebaseAuth != null) {
//            urlCurrenUser = FirebaseDatabase.getInstance().getReference();
//
//            //urlCurrenUser.child(Constant.USERS_CHILD).child(firebaseAuth.getUid());
//
//
//
//        } else {
//            urlCurrenUser=null;
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        try {
//            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                return;
//            }
//            locationManager.removeUpdates(myLocationListener);
//        } catch (Exception e) {
//        }
//    }
//    public class MyLocationListener implements LocationListener {
//
//        /**
//         * vị trí ta sẽ lấy được ở hàm onLocationChanged
//         * và ta sẽ push vị trí của mình lên server ở trong hàm này:
//         * @param location
//         */
//        @Override
//        public void onLocationChanged(Location location) {
//            try {
//                urlCurrenUser.child(Constant.LATITUDE_CHILD).setValue(location.getLatitude());
//                urlCurrenUser.child(Constant.LONGITUDE_CHILD).setValue(location.getLongitude());
//            }catch (Exception e){}
//            lattitude=location.getLatitude();
//            longitude=location.getLongitude();
//            EventBus.getDefault().post(location);
//            Log.d("lam", "onLocationChanged:lam "+location.getLatitude());
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//
//
//    }
//}
