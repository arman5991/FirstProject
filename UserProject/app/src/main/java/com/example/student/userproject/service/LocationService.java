package com.example.student.userproject.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

@SuppressWarnings("MissingPermission")
public class LocationService extends Service {

    private static LocationManager mLocationManager;
    private static LocationListener mLocationListener;
    private Location mLocation;
    private static boolean bNetwork;
    private static SharedPreferences shared;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        shared = getSharedPreferences("SWITCH", Context.MODE_PRIVATE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                changeNetwork();
                Intent intent = new Intent("LOCATION_UPDATE");
                intent.putExtra("lat", location.getLatitude());
                intent.putExtra("lng", location.getLongitude());
                intent.putExtra("mLocation", location);
                sendBroadcast(intent);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                if (provider.equals(LocationManager.GPS_PROVIDER)) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        };
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    public static void changeNetwork() {
        bNetwork = shared.getBoolean("SWITCH_TRUE", false);
        if (bNetwork) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        }
    }
}
