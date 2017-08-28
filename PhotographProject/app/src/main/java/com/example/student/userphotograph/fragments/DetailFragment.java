package com.example.student.userphotograph.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student.userphotograph.R;
import com.example.student.userphotograph.adapter.CarouselPagerAdapter;
import com.example.student.userphotograph.models.RatingModel;
import com.example.student.userphotograph.utilityes.CarouselTransformer;
import com.example.student.userphotograph.utilityes.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment implements View.OnClickListener {

    private ImageView detailAvatar;
    private TextView detailName;
    private TextView detailAddress;
    private TextView detailCameraInfo;
    private TextView detailEmail;
    private List<RatingModel> detailList;
    private RatingModel model;
    private String uid;
    private ViewPager viewpagerCar;
    private ImageView imageViewEmail;
    private RatingBar ratingBar;

    public DetailFragment() {
    }

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = Parcels.unwrap(getArguments().getParcelable("userInfo"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        findViewById(rootView);
        if (model != null) {
            photographerInfo(model);
        }
        FirebaseDatabase mDtabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseRef = mDtabase.getReference().child(Constants.PHOTOGRAPHS).child(uid);
        DatabaseReference mDatabaseGalleryRef = mDatabaseRef.child(Constants.GALLERY);
        mDatabaseGalleryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnepshot : dataSnapshot.getChildren()) {
                    final RatingModel info = postSnepshot.getValue(RatingModel.class);
                    detailList.add(info);
                }
                setupViewPager();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        init(rootView);
        imageViewEmail.setOnClickListener(this);
        return rootView;
    }

    private void init(View rootView) {
        viewpagerCar = (ViewPager) rootView.findViewById(R.id.view_pager_car);
        viewpagerCar.setClipChildren(false);
        viewpagerCar.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.pager_margin));
        viewpagerCar.setOffscreenPageLimit(3);
        viewpagerCar.setPageTransformer(false, new CarouselTransformer(getContext()));
    }

    private void setupViewPager() {
        CarouselPagerAdapter adapter = new CarouselPagerAdapter(getContext(), detailList);
        viewpagerCar.setAdapter(adapter);
        viewpagerCar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int index = 0;

            @Override
            public void onPageSelected(int position) {
                index = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void photographerInfo(RatingModel ratingModel) {
        detailList = new ArrayList<>();
        detailName.setText(ratingModel.getName());
        detailCameraInfo.setText(ratingModel.getCamera_info());
        detailEmail.setText(ratingModel.getEmail());
        detailAddress.setText(ratingModel.getAddress());
        uid = ratingModel.getUid();
        ratingBar.setProgress((int) ratingModel.getRating());
        if (TextUtils.isEmpty(ratingModel.getAvatarUri())) {
            detailAvatar.setImageResource(R.drawable.ic_account_circle_black_24dp);
        } else {
            Glide.with(getActivity())
                    .load(ratingModel.getAvatarUri())
                    .into(detailAvatar);
        }

    }

    private void findViewById(View rootView) {
        detailAvatar = (ImageView) rootView.findViewById(R.id.img_detail_avatar);
        detailName = (TextView) rootView.findViewById(R.id.tv_detail_name);
        detailAddress = (TextView) rootView.findViewById(R.id.tv_detail_address);
        detailCameraInfo = (TextView) rootView.findViewById(R.id.tv_detail_camera_info);
        detailEmail = (TextView) rootView.findViewById(R.id.tv_detail_email);
        imageViewEmail = (ImageView) rootView.findViewById(R.id.email_img);
        ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
