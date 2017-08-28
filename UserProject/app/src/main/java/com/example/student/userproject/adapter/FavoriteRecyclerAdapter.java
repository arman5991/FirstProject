package com.example.student.userproject.adapter;

import android.content.Context;
import android.media.Rating;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.student.userproject.R;
import com.example.student.userproject.model.PhotographInfo;

import java.util.List;

public class FavoriteRecyclerAdapter extends RecyclerView.Adapter<FavoriteRecyclerAdapter.MyViewHolder> {

    private List<PhotographInfo> list;
    private Fragment usageFragment;
    private OnItemClickFavorite mListener;
    private Context context;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (mListener == null) {
            mListener = (OnItemClickFavorite) usageFragment;
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mListener != null) {
            mListener = null;
        }
    }

    public FavoriteRecyclerAdapter(List<PhotographInfo> list, Fragment usageFragment, Context context) {
        this.list = list;
        this.usageFragment = usageFragment;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tvName.setText(list.get(position).getName());
        holder.tvPhone.setText(list.get(position).getEmail());

        if (TextUtils.isEmpty(list.get(position).getAvatarUri())) {
            holder.imgAvatar.setImageResource(R.drawable.ic_account_circle_black_24dp);
        } else {
            Glide.with(context)
                    .load(list.get(position).getAvatarUri())
                    .into(holder.imgAvatar);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.getModel(list.get(position));
                }
            });
        }
        holder.favRating.setProgress((int) list.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvPhone;
        private final ImageView imgAvatar;
        private final RatingBar favRating;
        private TextView tvName;

        MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.person_name);
            tvPhone = (TextView) view.findViewById(R.id.person_phone);
            imgAvatar = (ImageView) view.findViewById(R.id.person_photo);
            favRating = (RatingBar) view.findViewById(R.id.favorite_rating_bar);
        }
    }

    public interface OnItemClickFavorite {
        void getModel(PhotographInfo model);
    }
}
