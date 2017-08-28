package com.example.student.userphotograph.activityes;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.userphotograph.R;
import com.example.student.userphotograph.fragments.AboutFragment;
import com.example.student.userphotograph.fragments.GMapFragment;
import com.example.student.userphotograph.fragments.PostFragment;
import com.example.student.userphotograph.fragments.RatingFragment;
import com.example.student.userphotograph.fragments.SettingsFragment;
import com.example.student.userphotograph.utilityes.Constants;
import com.example.student.userphotograph.utilityes.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener {

    private StorageReference mStorageAvatarRef;
    private DrawerLayout mDrawer;
    private ImageView mNavDrawerAvatar;
    private TextView mLastName;
    private TextView mEmail;
    private Typeface mTypeface;
    private String uid;
    AlertDialog dialog;
    private SharedPreferences shared;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        responsePermissionGranted();
        shared = getSharedPreferences("localization", MODE_PRIVATE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("choco_cooky.ttf")
                .build());
        mTypeface = Typeface.createFromAsset(getAssets(), "choco_cooky.ttf");

        findViewById();
        writeFbDb();
        FirebaseMessaging.getInstance().subscribeToTopic(uid);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        replaceFragment(SettingsFragment.newInstance());
        toggleNavDrawer();
    }

    private void toggleNavDrawer() {
        Button button = (Button) findViewById(R.id.nav_bar_toggle);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDrawer.openDrawer(Gravity.LEFT);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Here you can try to detect the swipe. It will be necessary to
                        // store more than the previous value to check that the user move constantly in the same direction
                        mDrawer.openDrawer(Gravity.LEFT);

                    case MotionEvent.ACTION_UP:
                        mDrawer.openDrawer(Gravity.LEFT);
                        break;
                }
                mDrawer.openDrawer(Gravity.LEFT);
                return true;
            }
        });
    }

    private void responsePermissionGranted() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        10);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == 10 &&
                grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissions is disable", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Permissions is enabled", Toast.LENGTH_LONG).show();
        }
    }

    private void findViewById() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        View header = navigationView.getHeaderView(0);

        mLastName = (TextView) header.findViewById(R.id.tv_name_last_name);
        mLastName.setTypeface(mTypeface);
        mEmail = (TextView) header.findViewById(R.id.tv_email);
        mEmail.setTypeface(mTypeface);
        mNavDrawerAvatar = (ImageView) header.findViewById(R.id.img_nav_drawer);

        navigationView.setNavigationItemSelectedListener(this);
        mDrawer.addDrawerListener(this);
    }

    private void writeFbDb() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("photographs").child(mUser.getUid());
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        mStorageAvatarRef = mStorageRef.child("photographs").child("avatar").child(mUser.getUid());
        uid = mUser.getUid();

        FirebaseHelper.downloadImageAndSetAvatar(mStorageAvatarRef, mNavDrawerAvatar);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mLastNameRef = dataSnapshot.child("name").getValue(String.class);
                mLastName.setText(mLastNameRef);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mEmail.setText(mUser.getEmail());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().findFragmentById(R.id.container_home) != null) {
            super.onBackPressed();
            if (getSupportFragmentManager().findFragmentById(R.id.container_home) == null) {
                finish();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_map: {
                replaceFragment(GMapFragment.newInstance());
            }
            break;

            case R.id.nav_settings: {
                replaceFragment(SettingsFragment.newInstance());
            }
            break;

            case R.id.nav_posts: {
                replaceFragment(PostFragment.newInstance());
            }
            break;
            case R.id.nav_about: {
                replaceFragment(AboutFragment.newInstance());
            }
            break;
            case R.id.nav_language: {
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.languages, null);
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setView(dialogLayout);
                dialog = dialogBuilder.create();
                dialog.show();

                Button btnOk = (Button) dialogLayout.findViewById(R.id.btn_confirm);
                final RadioGroup rgLanguage = (RadioGroup) dialogLayout.findViewById(R.id.language_group);
                RadioButton rbEnglish = (RadioButton) dialogLayout.findViewById(R.id.rb_english);
                RadioButton rbArmenian = (RadioButton) dialogLayout.findViewById(R.id.rb_armenian);
                RadioButton rbSpanish = (RadioButton) dialogLayout.findViewById(R.id.rb_spanish);
                RadioButton rbGermany = (RadioButton) dialogLayout.findViewById(R.id.rb_german);
                RadioButton rbRussian = (RadioButton) dialogLayout.findViewById(R.id.rb_russian);

                String language = shared.getString(Constants.DEFAULT_LANGUAGE, "");
                switch (language){
                    case "en":{
                        rbEnglish.setChecked(true);
                        break;
                    }
                    case "ru":{
                        rbRussian.setChecked(true);
                        break;
                    }
                    case "hy":{
                        rbArmenian.setChecked(true);
                        break;
                    }
                    case "de":{
                        rbGermany.setChecked(true);
                        break;
                    }
                    case "es":{
                        rbSpanish.setChecked(true);
                        break;
                    }
                }

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int cheked = rgLanguage.getCheckedRadioButtonId();
                        switch (cheked){
                            case R.id.rb_english: {
                                Locale english = new Locale("en");
                                changeLocale(english);
                                break;
                            }
                            case R.id.rb_armenian: {
                                Locale armenian = new Locale("hy");
                                changeLocale(armenian);
                                break;
                            }
                            case R.id.rb_spanish: {
                                Locale spanish = new Locale("es");
                                changeLocale(spanish);
                                break;
                            }
                            case R.id.rb_german: {
                                Locale german = new Locale("de");
                                changeLocale(german);
                                break;
                            }
                            case R.id.rb_russian: {
                                Locale russian = new Locale("ru");
                                changeLocale(russian);
                                break;
                            }
                        }
                        shared.edit().putString(Constants.DEFAULT_LANGUAGE, Locale.getDefault().getLanguage()).apply();
                        Toast.makeText(HomeActivity.this, shared.getString(Constants.DEFAULT_LANGUAGE,""), Toast.LENGTH_SHORT).show();
                    }

                    private void changeLocale(Locale language) {
                        Locale.setDefault(language);
                        Configuration config = new Configuration();
                        config.locale = language;
                        getBaseContext().getResources().updateConfiguration(config,
                                getBaseContext().getResources().getDisplayMetrics());
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        dialog.dismiss();
                    }

                });
            }
            break;

            case R.id.nav_rating:{
                replaceFragment(RatingFragment.newInstance());
                break;
            }

            case R.id.nav_log_out: {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(uid);
                ProgressDialog mProgressDialog = new ProgressDialog(HomeActivity.this);
                mProgressDialog.show();
                FirebaseAuth.getInstance().signOut();
                finish();

                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                mProgressDialog.dismiss();
            }
            break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_home, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        invalidateOptionsMenu();
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        FirebaseHelper.downloadImageAndSetAvatar(mStorageAvatarRef, mNavDrawerAvatar);
        invalidateOptionsMenu();
    }

    @Override
    public void onDrawerClosed(View drawerView) {
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

}
