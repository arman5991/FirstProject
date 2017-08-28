package com.example.student.userphotograph.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.arsy.maps_library.MapRipple;
import com.example.student.userphotograph.R;
import com.example.student.userphotograph.service.GPSTracker;
import com.example.student.userphotograph.service.LocationService;
import com.example.student.userphotograph.utilityes.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.student.userphotograph.utilityes.Constants.MAP_RADAR_ANIMATION_DURATION;

public class GMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnPoiClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private DatabaseReference mLatRef;
    private DatabaseReference mLngRef;
    private BroadcastReceiver mBroadcastReceiver;
    private double currentLat;
    private double currentLng;
    private Location mLocation;
    private Marker currentMarker;
    private boolean isFirstLocationDetection;
    private MapRipple mapRipple;
    private Location locationUser;
    private SharedPreferences shared;
    private Intent serviceIntent;
    private LatLng latLngUser;

    public GMapFragment() {
    }

    public static GMapFragment newInstance() {
        return new GMapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIntent = new Intent(getActivity(), LocationService.class);
        getActivity().startService(serviceIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        ImageView imgMyLocation = (ImageView) rootView.findViewById(R.id.my_location_map);
        imgMyLocation.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        shared = getActivity().getSharedPreferences("location", Context.MODE_PRIVATE);
        float lng = shared.getFloat("key_lng", 0);
        float lat = shared.getFloat("key_lat", 0);
        final int phone = shared.getInt("key_phone", 0);

        mMap = googleMap;
        mMap.setOnPoiClickListener(this);
        firstLocation();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        latLngUser = new LatLng(lat, lng);
        locationUser = new Location("");
        locationUser.setLatitude(lat);
        locationUser.setLongitude(lng);
        mMap.addMarker(new MarkerOptions().position(latLngUser).title(String.valueOf(phone)));
        if (lat != 0 && lng != 0) {
            mMap.addMarker(new MarkerOptions().position(latLngUser)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngUser));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngUser, 15));
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phone));

                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                        return;
                    }
                    startActivity(callIntent);
                }
            });
        }
        listenLocationChanges();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private void initMapRadar(LatLng currentPosition) {
        mapRipple = new MapRipple(mMap, currentPosition, getActivity());
        mapRipple.withNumberOfRipples(1);
        mapRipple.withFillColor(Color.BLUE);
        mapRipple.withStrokeColor(Color.BLACK);
        mapRipple.withStrokewidth(10);
        mapRipple.withDistance(Constants.DISTANCE);
        mapRipple.withRippleDuration(MAP_RADAR_ANIMATION_DURATION);
        mapRipple.withTransparency(0.5f);
        mapRipple.startRippleMapAnimation();
    }

    private void addCurrentMarker(double latitude, double longitude) {
        if (mMap != null) {
            if (currentMarker != null) {
                currentMarker.remove();
            }
            LatLng currentPosition = new LatLng(latitude, longitude);
            currentMarker = mMap.addMarker(
                    new MarkerOptions().position(currentPosition));
            if (isFirstLocationDetection) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.ZOOM_NUMBER),
                        Constants.MAP_ANIMATION_DURATION, null);
                isFirstLocationDetection = false;
            }
            if (mapRipple == null) {
                initMapRadar(currentPosition);
            } else if (!mapRipple.isAnimationRunning()) {
                mapRipple.startRippleMapAnimation();
            }
            mapRipple.withLatLng(currentPosition);
        }
    }

    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(getActivity(), poi.name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        firstLocation();
    }

    public void firstLocation() {
        GPSTracker gpsTracker = new GPSTracker(getActivity().getApplicationContext());
        Location mlocation = gpsTracker.getLocation();

        if (mlocation == null) {
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else {
            double mLatitude = mlocation.getLatitude();
            double mLongitude = mlocation.getLongitude();

            LatLng myLocation = new LatLng(mLatitude, mLongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.PHOTOGRAPHS).child(user.getUid());
            mLatRef = mDatabaseRef.child(Constants.LATITUDE);
            mLngRef = mDatabaseRef.child(Constants.LONGITUDE);
            mLatRef.setValue(mLatitude);
            mLngRef.setValue(mLongitude);
        }
    }


    private void listenLocationChanges() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (mMap != null) mMap.clear();
                    currentLat = (double) intent.getExtras().get("lat");
                    currentLng = (double) intent.getExtras().get("lng");
                    mLocation = (Location) intent.getExtras().get("mLocation");
                    if (locationUser.getLatitude() != 0 && locationUser.getLongitude() != 0) {
                        if (locationUser.distanceTo(mLocation) <= 50) {
                            shared.edit().remove("location").apply();
                        }
                    }
                    addCurrentMarker(currentLat, currentLng);
                }

            };
        }
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter("LOCATION_UPDATE"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
        if (mMap != null) {
            mMap.clear();
        }
        getActivity().stopService(serviceIntent);
    }
}
