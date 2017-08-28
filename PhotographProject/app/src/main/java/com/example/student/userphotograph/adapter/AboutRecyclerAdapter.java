package com.example.student.userphotograph.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.student.userphotograph.R;
import com.example.student.userphotograph.models.AboutModel;

import java.util.List;

public class AboutRecyclerAdapter extends RecyclerView.Adapter<AboutRecyclerAdapter.MyViewHolder> {

    private List<AboutModel> aboutModelList;
    private Context context;

    @Override
    public AboutRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_about, parent, false);

        return new MyViewHolder(itemView);
    }

    public AboutRecyclerAdapter(List<AboutModel> aboutModelList, Context context) {
        this.aboutModelList = aboutModelList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(AboutRecyclerAdapter.MyViewHolder holder, int position) {
        AboutModel model = aboutModelList.get(position);
        holder.tvMemberNameSurname.setText(model.getTvMemberName());
        Glide.with(context)
                .load(model.getImgMember())
                .into(holder.imgMemberPhoto);
    }

    @Override
    public int getItemCount() {
        return aboutModelList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMemberNameSurname;
        private final ImageView imgMemberPhoto;

        MyViewHolder(View view) {
            super(view);
            tvMemberNameSurname = (TextView) view.findViewById(R.id.tv_member_name_surname);
            imgMemberPhoto = (ImageView) view.findViewById(R.id.img_member_photo);
        }
    }
}
