package com.example.student.userphotograph.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LocationService extends Service {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private DatabaseReference mLatRef;
    private DatabaseReference mLngRef;
    private Location oldLocation;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onCreate() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference()
                .child("photographs").child(user.getUid());
        mLatRef = mDatabaseRef.child("latitude");
        mLngRef = mDatabaseRef.child("longitude");
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location newLocation) {
                Intent intent = new Intent("LOCATION_UPDATE");
                intent.putExtra("lat", newLocation.getLatitude());
                intent.putExtra("lng", newLocation.getLongitude());
                sendBroadcast(intent);
                if (oldLocation != newLocation) {
                    mLatRef.setValue(newLocation.getLatitude());
                    mLngRef.setValue(newLocation.getLongitude());
                    oldLocation = newLocation;
                }
                Log.i("ssssss", "location service");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(LocationService.this, "enable GPS", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, mLocationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}