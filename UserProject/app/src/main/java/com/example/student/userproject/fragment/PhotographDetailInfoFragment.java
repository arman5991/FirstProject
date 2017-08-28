package com.example.student.userproject.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student.userproject.R;
import com.example.student.userproject.adapter.CarouselPagerAdapter;
import com.example.student.userproject.model.PhotographInfo;
import com.example.student.userproject.utility.CarouselTransformer;
import com.example.student.userproject.utility.Constants;
import com.example.student.userproject.utility.NetworkHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PhotographDetailInfoFragment extends Fragment implements View.OnClickListener {

    private ImageView detailAvatar;
    private Button sendNotification;
    private TextView detailName;
    private TextView detailAddress;
    private TextView detailCameraInfo;
    private TextView detailEmail;
    private List<PhotographInfo> detailList;
    private boolean isFavorite;
    private PhotographInfo userInfo;
    private String uid;
    private ImageView imgFavorite;
    private SharedPreferences sharedPref;
    private EditText etPhone;
    private Dialog alertDialog;
    private ViewPager viewpagerCar;
    private ImageView imageViewEmail;
    private RatingBar ratingBar;

    public PhotographDetailInfoFragment() {
    }

    public static PhotographDetailInfoFragment newInstance() {
        return new PhotographDetailInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = Parcels.unwrap(getArguments().getParcelable("userInfo"));
        sharedPref = this.getActivity().getSharedPreferences(Constants.FAVORITES_PREF, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photograph_detail_info, container, false);
        findViewById(rootView);
        if (userInfo != null) {
            photographerInfo(userInfo);
        }
        FirebaseDatabase mDtabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = mDtabase.getReference().child(Constants.PHOTOGRAPHS).child(uid);
        DatabaseReference mDatabaseGalleryRef = mDatabaseRef.child(Constants.GALLERY);
        mDatabaseGalleryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnepshot : dataSnapshot.getChildren()) {
                    final PhotographInfo info = postSnepshot.getValue(PhotographInfo.class);
                    detailList.add(info);
                }
                setupViewPager();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        init(rootView);
        getFavoriteStatus();
        sendNotification.setOnClickListener(this);
        imgFavorite.setOnClickListener(this);
        imageViewEmail.setOnClickListener(this);

        return rootView;
    }

    private void init(View rootView) {
        viewpagerCar = (ViewPager) rootView.findViewById(R.id.view_pager_car);
        viewpagerCar.setClipChildren(false);
        viewpagerCar.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.pager_margin));
        viewpagerCar.setOffscreenPageLimit(3);
        viewpagerCar.setPageTransformer(false, new CarouselTransformer(getContext()));// Set transformer);
    }

    private void setupViewPager() {
        CarouselPagerAdapter adapter = new CarouselPagerAdapter(getContext(), detailList);
        viewpagerCar.setAdapter(adapter);
        viewpagerCar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int index = 0;

            @Override
            public void onPageSelected(int position) {
                index = position;
                Log.w("====m ","onpageselected");
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void getFavoriteStatus() {
        String getFavoriteStatus = sharedPref.getString(Constants.FAVORITE_KEY + uid, "");
        isFavorite = !getFavoriteStatus.equals("");
        if (isFavorite) imgFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
        else imgFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
    }

    private void photographerInfo(PhotographInfo photographInfo) {
        detailList = new ArrayList<>();
        detailName.setText(photographInfo.getName());
        detailCameraInfo.setText(photographInfo.getCamera_info());
        detailEmail.setText(photographInfo.getEmail());
        detailAddress.setText(photographInfo.getAddress());
        uid = photographInfo.getUid();
        ratingBar.setProgress((int) photographInfo.getRating());
        Glide.with(getActivity())
                .load(photographInfo.getAvatarUri())
                .into(detailAvatar);
    }

    private void findViewById(View rootView) {
        sendNotification = (Button) rootView.findViewById(R.id.send_notification);
        detailAvatar = (ImageView) rootView.findViewById(R.id.img_detail_avatar);
        detailName = (TextView) rootView.findViewById(R.id.tv_detail_name);
        detailAddress = (TextView) rootView.findViewById(R.id.tv_detail_address);
        detailCameraInfo = (TextView) rootView.findViewById(R.id.tv_detail_camera_info);
        detailEmail = (TextView) rootView.findViewById(R.id.tv_detail_email);
        imgFavorite = (ImageView) rootView.findViewById(R.id.btn_favorite);
        imageViewEmail = (ImageView) rootView.findViewById(R.id.email_img);
        ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
    }

    private AlertDialog.Builder initDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("Please set your phone");
        dialogBuilder.setView(getDialogLayout());
        return dialogBuilder;
    }

    private View getDialogLayout() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        etPhone = (EditText) dialogView.findViewById(R.id.et_phone);
        final Button btnSend = (Button) dialogView.findViewById(R.id.btn_send);
        btnSend.setEnabled(false);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getArguments();
                double lat = bundle.getDouble("lat");
                double lng = bundle.getDouble("lng");
                NetworkHelper.sendNotificationRequest(getContext(), userInfo.getUid(), lat, lng, etPhone.getText().toString());
                alertDialog.dismiss();
            }
        });
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    btnSend.setEnabled(true);
                } else btnSend.setEnabled(false);
            }
        });
        return dialogView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_notification: {
                AlertDialog.Builder dialogBuilder = initDialog();
                alertDialog = dialogBuilder.create();
                alertDialog.show();
                break;
            }
            case R.id.btn_favorite: {
                if (isFavorite) {
                    Log.i("==== ", "Favorite --> is  to not");
                    imgFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(Constants.FAVORITE_KEY + uid, "");
                    isFavorite = false;
                    editor.apply();

                } else {
                    Log.i("==== ", "Favorite --> not to is");
                    imgFavorite.setImageResource(R.drawable.ic_favorite_black_24dp);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(Constants.FAVORITE_KEY + uid, uid);
                    isFavorite = true;
                    editor.apply();
                }
                break;
            }
            case R.id.email_img: {
                sendEmail();
                break;
            }
        }
    }

    private void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{detailEmail.getText().toString()});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT, "body of email");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
