package com.example.student.userproject.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student.userproject.R;
import com.example.student.userproject.model.PostModel;
import com.example.student.userproject.utility.Constants;
import com.example.student.userproject.utility.SQLHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.google.android.gms.internal.zzt.TAG;

public class PostFragment extends Fragment {

    private DatabaseReference mDatabaseRef;
    private SQLHelper db;

    public PostFragment() {

    }

    public static PostFragment newInstance() {

        return new PostFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLHelper(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
        RecyclerView postRecyclerView = (RecyclerView) rootView.findViewById(R.id.post_recycler_view);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        postRecyclerView.setLayoutManager(lm);
        postRecyclerView.setHasFixedSize(true);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        postRecyclerView.setAdapter(getFirebaseRecyclerAdapter());
        return rootView;
    }

    @NonNull
    private FirebaseRecyclerAdapter<PostModel, PostHolder> getFirebaseRecyclerAdapter() {
        return new FirebaseRecyclerAdapter<PostModel, PostHolder>(
                PostModel.class,
                R.layout.post_recycler_row_item,
                PostHolder.class,
                mDatabaseRef.child(Constants.POSTS)) {
            @Override
            protected void populateViewHolder(final PostHolder viewHolder, final PostModel model, int position) {
                viewHolder.tvUserName.setText(model.getUserName());
                long date = model.getDate();
                viewHolder.tvPostTime.setText(getCurrentDate(date, "dd/MM/yyyy HH:mm"));
                viewHolder.tvPostTitle.setText(model.getTitle());
                viewHolder.tvLikesCount.setText(String.valueOf(model.getLikes()));
                final String uid = model.getUid();
                if (db.isLiked(uid)) {
                    viewHolder.imgLike.setImageResource(R.drawable.ic_thumb_up_blue_dark_24dp);
                }
                viewHolder.imgLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (db.isLiked(uid)) {
                            Toast.makeText(getActivity(), "has already liked", Toast.LENGTH_SHORT).show();
                        } else {
                            db.addPostLikes(uid);
                            updateNumLikes(model.getUid());
                            updateUserRating(model.getUserId());
                            Toast.makeText(getActivity(), "liked", Toast.LENGTH_SHORT).show();
                            viewHolder.imgLike.setImageResource(R.drawable.ic_thumb_up_blue_dark_24dp);
                        }
                    }
                });
                Glide.with(getActivity())
                        .load(model.getImageUrl())
                        .into(viewHolder.imgPost);
            }
        };
    }

    private void updateUserRating(String userId) {
        mDatabaseRef.child(Constants.PHOTOGRAPHS).child(userId).child(Constants.RATING)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        return getResult(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    }
                });
    }

    public static String getCurrentDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void updateNumLikes(String uid) {
        mDatabaseRef.child(Constants.POSTS).child(uid).child(Constants.LIKES)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        return getResult(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.d(TAG, "likeTransaction:onComplete:" + databaseError);
                    }
                });
    }

    @NonNull
    private Transaction.Result getResult(MutableData mutableData) {
        long num = (long) mutableData.getValue();
        num++;
        mutableData.setValue(num);
        return Transaction.success(mutableData);
    }

    private static class PostHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName;
        private TextView tvPostTitle;
        private TextView tvPostTime;
        private TextView tvLikesCount;
        private ImageView imgPost;
        private ImageView imgLike;

        public PostHolder(View itemView) {
            super(itemView);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_post_username);
            tvPostTitle = (TextView) itemView.findViewById(R.id.tv_post_title);
            tvPostTime = (TextView) itemView.findViewById(R.id.tv_post_time);
            tvLikesCount = (TextView) itemView.findViewById(R.id.tv_likes_count);
            imgPost = (ImageView) itemView.findViewById(R.id.post_image);
            imgLike = (ImageView) itemView.findViewById(R.id.img_like);
        }
    }
}
