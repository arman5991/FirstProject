package com.example.student.userphotograph.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.student.userphotograph.R;
import com.example.student.userphotograph.models.RatingModel;

import java.util.List;

public class FullScreenFragment extends DialogFragment {

    private ViewPager fullViewPager;
    private List<RatingModel> infoList;
    private TextView galleryTitleFull;
    private int mSelectedPosition;

    public FullScreenFragment() {
    }

    public static FullScreenFragment newInstance() {
        return new FullScreenFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_full_screen, container, false);
        fullViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        infoList = (List<RatingModel>) getArguments().getSerializable("images");

        mSelectedPosition = getArguments().getInt("position");

        MyViewPagerAdapter mAdapter = new MyViewPagerAdapter();
        fullViewPager.setAdapter(mAdapter);
        fullViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(mSelectedPosition);

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void setCurrentItem(int position) {
        fullViewPager.setCurrentItem(position, false);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        MyViewPagerAdapter() {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView = layoutInflater.inflate(R.layout.image_fullscreen, container, false);
            ImageView imageViewPreview = (ImageView) rootView.findViewById(R.id.image_full_screen);
            galleryTitleFull = (TextView) rootView.findViewById(R.id.gallery_title_full);
            Glide.with(getActivity())
                    .load(infoList.get(position).getImageUri())
                    .into(imageViewPreview);
            galleryTitleFull.setText(infoList.get(position).getTitle());
            container.addView(rootView);
            return rootView;
        }

        @Override
        public int getCount() {
            return infoList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}
