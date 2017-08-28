package com.example.student.userphotograph.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.student.userphotograph.R;
import com.example.student.userphotograph.activityes.HomeActivity;
import com.example.student.userphotograph.fragments.FullScreenFragment;
import com.example.student.userphotograph.models.RatingModel;

import java.io.Serializable;
import java.util.List;

public class CarouselPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<RatingModel> mInfoList;

    public CarouselPagerAdapter(Context context, List<RatingModel> mInfoList) {
        this.mContext = context;
        this.mInfoList = mInfoList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.full_screen_item, null);
        try {

            RelativeLayout relMain = (RelativeLayout) view.findViewById(R.id.rel_main);
            TextView galleryTitle = (TextView) view.findViewById(R.id.gallery_title);
            ImageView image = (ImageView) view.findViewById(R.id.gallery_image);
            relMain.setTag(position);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", (Serializable) mInfoList);
                    bundle.putInt("position", position);

                    FragmentTransaction ft = ((HomeActivity) mContext).getSupportFragmentManager().beginTransaction();
                    FullScreenFragment newFragment = FullScreenFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "CaruselPagerAdapter");
                }
            });

            Glide.with(mContext)
                    .load(mInfoList.get(position).getImageUri())
                    .into(image);
            galleryTitle.setText(mInfoList.get(position).getTitle());
            container.addView(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mInfoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}