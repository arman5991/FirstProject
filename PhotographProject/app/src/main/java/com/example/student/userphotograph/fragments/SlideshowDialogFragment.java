package com.example.student.userphotograph.fragments;

import android.content.Context;
import android.os.Bundle;
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
import com.example.student.userphotograph.models.Pictures;

import java.util.List;

public class SlideshowDialogFragment extends DialogFragment {

    private ViewPager mViewPager;
    private List<Pictures> mImgPager;

    public SlideshowDialogFragment() {
    }

    public static SlideshowDialogFragment newInstance() {
        return new SlideshowDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slideshow_dialog, container, false);

        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mImgPager = (List<Pictures>) getArguments().getSerializable("images");
        int mSelectedPosition = getArguments().getInt("position");

        MyViewPagerAdapter mAdapter = new MyViewPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(mSelectedPosition);

        return rootView;
    }

    private void setCurrentItem(int position) {
        mViewPager.setCurrentItem(position, false);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            mViewPager.setCurrentItem(position, false);
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
            View rootView = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);
            ImageView imageViewPreview = (ImageView) rootView.findViewById(R.id.image_preview);
            TextView galleryTitle = (TextView) rootView.findViewById(R.id.gallery_title);

            Glide.with(getActivity())
                    .load(mImgPager.get(position).getImageUri())
                    .into(imageViewPreview);
            galleryTitle.setText(mImgPager.get(position).getTitle());

            container.addView(rootView);
            return rootView;
        }

        @Override
        public int getCount() {
            return mImgPager.size();
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