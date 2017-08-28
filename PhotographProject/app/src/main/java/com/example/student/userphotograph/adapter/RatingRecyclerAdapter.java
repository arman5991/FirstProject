package com.example.student.userphotograph.adapter;

import android.content.Context;
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
import com.example.student.userphotograph.R;
import com.example.student.userphotograph.models.RatingModel;

import java.util.List;

public class RatingRecyclerAdapter extends RecyclerView.Adapter<RatingRecyclerAdapter.MyViewHolder> {

    private List<RatingModel> list;
    private Context context;
    private OnItemClickRating mListener;
    private Fragment usageFragment;


    public RatingRecyclerAdapter(List<RatingModel> list, Fragment usageFragment, Context context) {
        this.list = list;
        this.usageFragment = usageFragment;
        this.context = context;
    }

    @Override
    public RatingRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_rating, parent, false);
        return new RatingRecyclerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RatingRecyclerAdapter.MyViewHolder holder, final int position) {
        holder.tvRatingName.setText(list.get(position).getName());
        holder.tvRatingRating.setText("Rating ".concat(String.valueOf(list.get(position).getRating())));
        holder.countRating.setProgress((int) list.get(position).getRating());
        if (TextUtils.isEmpty(list.get(position).getAvatarUri())) {
            holder.avatarRating.setImageResource(R.drawable.ic_account_circle_black_24dp);

        } else {
            Glide.with(context)
                    .load(list.get(position).getAvatarUri())
                    .into(holder.avatarRating);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.getModel(list.get(position));
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (mListener == null) {
            mListener = (OnItemClickRating) usageFragment;
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mListener != null) {
            mListener = null;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRatingName;
        private TextView tvRatingRating;
        private ImageView avatarRating;
        private RatingBar countRating;

        MyViewHolder(View view) {
            super(view);
            tvRatingName = (TextView) itemView.findViewById(R.id.tv_rating_name);
            tvRatingRating = (TextView) itemView.findViewById(R.id.tv_rating_rating);
            avatarRating = (ImageView) itemView.findViewById(R.id.avatar_rating);
            countRating = (RatingBar) itemView.findViewById(R.id.rb_rating_count);
        }
    }

    public interface OnItemClickRating {
        void getModel(RatingModel model);
    }
}
