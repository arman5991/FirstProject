package com.example.student.userproject.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arsy.maps_library.MapRipple;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.student.userproject.R;
import com.example.student.userproject.model.PhotographInfo;
import com.example.student.userproject.service.LocationService;
import com.example.student.userproject.utility.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.example.student.userproject.utility.Constants.MAP_ANIMATION_DURATION;
import static com.example.student.userproject.utility.Constants.MAP_RADAR_ANIMATION_DURATION;
import static com.example.student.userproject.utility.Constants.ZOOM_NUMBER;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static float DISTANCE = 5000;

    private DatabaseReference mDatabaseRef;
    private List<PhotographInfo> photograpsList;
    private GoogleMap mMap;
    private MapRipple mapRipple;
    private BroadcastReceiver mBroadcastReceiver;
    private Intent serviceIntent;
    private SupportMapFragment fragmentMap;
    private Location mLocation;
    private Location location;
    private double currentLat;
    private double currentLng;
    private Marker currentMarker;
    private Bitmap bmp;
    private Marker marker;
    private boolean isFirstLocationDetection = true;
    private boolean orderOk = false;
    private SharedPreferences orderOkey;
    private SharedPreferences shared;
    private String uid;
    private TextView etText;
    private Button btnOk;
    private FloatingActionButton btnSettingsMap;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public MapFragment() {
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference().child(Constants.PHOTOGRAPHS);
        ImageView btn = (ImageView) rootView.findViewById(R.id.img_settings);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRadius();
            }
        });
        mDatabaseRef = database.getReference().child(Constants.PHOTOGRAPHS);
        photograpsList = new ArrayList<>();
        orderOkey = getActivity().getSharedPreferences("NOTIFICATION_OK", Context.MODE_PRIVATE);
        orderOk = orderOkey.getBoolean("OK", false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        fragmentMap.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        listenLocationChanges();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapRipple != null && mapRipple.isAnimationRunning()) {
            mapRipple.stopRippleMapAnimation();
        }
    }

    @Override
    public void onDestroy() {
        try {
            FragmentTransaction ft = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            ft.remove(fragmentMap);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (shared != null && orderOkey != null) {
            shared.edit().remove(uid).apply();
            orderOkey.edit().putBoolean("OK", false).apply();
        }
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
        if (mMap != null) mMap.clear();
        getActivity().stopService(serviceIntent);
    }

    private void initMapRadar(LatLng currentPosition) {
        mapRipple = new MapRipple(mMap, currentPosition, getActivity());
        mapRipple.withNumberOfRipples(1);
        mapRipple.withFillColor(Color.BLUE);
        mapRipple.withStrokeColor(Color.BLACK);
        mapRipple.withStrokewidth(10);
        mapRipple.withDistance(DISTANCE);
        mapRipple.withRippleDuration(MAP_RADAR_ANIMATION_DURATION);
        mapRipple.withTransparency(0.5f);
        mapRipple.startRippleMapAnimation();
    }

    private void updateRadius() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.update_radius, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        etText = (TextView) view.findViewById(R.id.et_text_update_radius);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setProgress(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                etText.setText(String.valueOf(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnOk = (Button) view.findViewById(R.id.btn_update_radius);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int distance = 0;
                if (etText.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Enter currently number", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    distance = 1000 * Integer.parseInt(etText.getText().toString());
                }
                DISTANCE = distance;
                //addCurrentMarker(currentLat, currentLng);
                getAllPhotographsNearly();
                dialog.dismiss();
            }
        });

    }

    private void startDetailFragment(Marker marker) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("userInfo", Parcels.wrap(marker.getTag()));
        bundle.putDouble("lat", currentLat);
        bundle.putDouble("lng", currentLng);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        PhotographDetailInfoFragment fr = new PhotographDetailInfoFragment();
        fr.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.container, fr, "DETAILS_FRAGMENT")
                .addToBackStack(null)
                .commit();
    }

    private void listenLocationChanges() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    currentLat = (double) intent.getExtras().get("lat");
                    currentLng = (double) intent.getExtras().get("lng");
                    mLocation = (Location) intent.getExtras().get("mLocation");
                    if (orderOk) {
                        getPhotographer();
                    } else {
                        getAllPhotographsNearly();
                    }
                }
            };
        }
        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter("LOCATION_UPDATE"));
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
                mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_NUMBER),
                        MAP_ANIMATION_DURATION, null);
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

    private void getAllPhotographsNearly() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (mMap != null) {
//                    mMap.clear();
//                }
                for (DataSnapshot postSnepshot : dataSnapshot.getChildren()) {
                    final PhotographInfo info = postSnepshot.getValue(PhotographInfo.class);
                    final LatLng latLng = getLatLng(info);
                    if (location.distanceTo(mLocation) >= DISTANCE) {
                        marker = mMap.addMarker(
                                new MarkerOptions().position(latLng).title(info.getName()));
                        marker.setTag(info);
                        mMap.setOnInfoWindowClickListener(
                                new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        startDetailFragment(marker);
                                    }
                                });
                        getPhotographAvatar(info, marker);
                    }
                    photograpsList.add(info);
                }
                addCurrentMarker(mLocation.getLatitude(), mLocation.getLongitude());
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void getPhotographer() {
        shared = getActivity().getSharedPreferences("PHOTOGRAPHER_UID", Context.MODE_PRIVATE);
        uid = shared.getString("uid", "");
        mDatabaseRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mMap != null) mMap.clear();
                PhotographInfo info = dataSnapshot.getValue(PhotographInfo.class);
                LatLng latLng = getLatLng(info);
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(info.getName()));
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                if (location.distanceTo(mLocation) >= DISTANCE) {
                    orderOkey.edit().putBoolean("OK", false).apply();
                    getAllPhotographsNearly();
                }
                addCurrentMarker(mLocation.getLatitude(), mLocation.getLongitude());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @NonNull
    private LatLng getLatLng(PhotographInfo info) {
        location = new Location("provider");
        double latitude = info.getLatitude();
        double longitude = info.getLongitude();
        final LatLng latLng = new LatLng(latitude, longitude);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        mLocation = new Location("provider");
        mLocation.setLatitude(currentLat);
        mLocation.setLongitude(currentLng);
        return latLng;
    }

    private void getPhotographAvatar(final PhotographInfo info, final Marker marker) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (marker != null) {
                            Glide.with(getActivity())
                                    .load(info.getAvatarUri())
                                    .asBitmap()
                                    .fitCenter()
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource,
                                                                    GlideAnimation<? super Bitmap> glideAnimation) {
                                            bmp = Bitmap.createScaledBitmap(resource, 80, 80, false);
                                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bmp));
                                        }
                                    });
                        }
                    }
                });
            }
        }).start();
    }

}
