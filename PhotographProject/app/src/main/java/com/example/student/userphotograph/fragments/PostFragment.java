package com.example.student.userphotograph.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.student.userphotograph.R;
import com.example.student.userphotograph.models.PostModel;
import com.example.student.userphotograph.utilityes.Constants;
import com.example.student.userphotograph.utilityes.FirebaseHelper;
import com.example.student.userphotograph.utilityes.SQLHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.example.student.userphotograph.utilityes.Constants.PHOTOGRAPHS;
import static com.example.student.userphotograph.utilityes.Constants.POST;
import static com.example.student.userphotograph.utilityes.Constants.RATING;

public class PostFragment extends Fragment implements View.OnClickListener {

    private static PostFragment instance;
    private RecyclerView postRecyclerView;
    private DatabaseReference mDatabaseRef;
    private EditText photoTitle;
    private AlertDialog alertDialog;
    private StorageReference mStoragePostGallery;
    private DatabaseReference databasePost;
    private SQLHelper db;

    public PostFragment() {

    }

    public static PostFragment newInstance() {
        if (instance == null)
            instance = new PostFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databasePost = FirebaseDatabase.getInstance().getReference();
        db = new SQLHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts, container, false);
        final FloatingActionButton btn = (FloatingActionButton) rootView.findViewById(R.id.newPost);

        postRecyclerView = (RecyclerView) rootView.findViewById(R.id.post_recycler_view);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        postRecyclerView.setLayoutManager(lm);
        postRecyclerView.setHasFixedSize(true);
        floatingScrollListener(btn);
        btn.setOnClickListener(this);
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        mStoragePostGallery = mStorageRef.child(POST);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(POST);
        FirebaseRecyclerAdapter<PostModel, PostHolder> postAdapter = new FirebaseRecyclerAdapter<PostModel, PostHolder>(
                PostModel.class,
                R.layout.post_recycler_row_item,
                PostHolder.class,
                mDatabaseRef
        ) {
            @Override
            protected void populateViewHolder(final PostHolder viewHolder, final PostModel model, int position) {
                viewHolder.tvUserName.setText(model.getUserName());
                long date = model.getDate();
                viewHolder.tvPostTime.setText(getCurrentDate(date, "dd/MM/yyyy HH:mm"));
                viewHolder.tvPostTitle.setText(model.getTitle());
                viewHolder.tvLikesCount.setText(String.valueOf(model.getLikes()));
                final String postId = model.getUid();
                viewHolder.imgLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (db.isLiked(postId)) {
                            Toast.makeText(getActivity(), "has already been liked", Toast.LENGTH_SHORT).show();
                        } else {
                            db.addPostLikes(postId);
                            updateNumLikes(model.getUid());
                            updateRatingLikes(model.getUserId());
                            Toast.makeText(getActivity(), "liked", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Glide.with(getActivity())
                        .load(model.getImageUrl())
                        .into(viewHolder.imgPost);
            }
        };
        postRecyclerView.setAdapter(postAdapter);
        return rootView;
    }

    private void floatingScrollListener(final FloatingActionButton btn) {
        postRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && btn.isShown()) {
                    btn.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    btn.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
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

    private void updateNumLikes(String uid) { // likeri qanakna update anum
        mDatabaseRef.child(uid).child("likes")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        long num = (long) mutableData.getValue();
                        num++;
                        mutableData.setValue(num);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.d(TAG, "likeTransaction:onComplete:" + databaseError);
                    }
                });
    }

    private void updateRatingLikes(final String userId) {
        databasePost.child(PHOTOGRAPHS).child(userId).child(RATING)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        long num = (long) mutableData.getValue();
                        num++;
                        mutableData.setValue(num);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.d(TAG, "likeTransaction:onComplete:" + databaseError);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder dialogBuilder = initDialog();
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private AlertDialog.Builder initDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.Uploading1);
        dialogBuilder.setView(getDialogLayout());
        return dialogBuilder;
    }

    private View getDialogLayout() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.upload_dialog_layout, null);
        photoTitle = (EditText) dialogView.findViewById(R.id.et_dialog_title);
        final Button btnChoosePhoto = (Button) dialogView.findViewById(R.id.btn_choose_photo);
        btnChoosePhoto.setEnabled(false);
        photoTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    btnChoosePhoto.setEnabled(true);
                } else btnChoosePhoto.setEnabled(false);
            }
        });

        btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic(Constants.REQUEST_POST_CHOOSE_PICK);
            }
        });
        return dialogView;
    }

    private void choosePic(int requestCode) {
        Intent choosePicIntent = new Intent(Intent.ACTION_GET_CONTENT);
        choosePicIntent.setType("image/*");
        startActivityForResult(choosePicIntent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_POST_CHOOSE_PICK && resultCode == RESULT_OK && data.getData() != null) {
            Uri mFilePath = data.getData();
            String mImageName = System.currentTimeMillis() + "." + FirebaseHelper.getFileExtension(mFilePath, getActivity());
            FirebaseHelper.uploadPost(getActivity(), mImageName, photoTitle, mStoragePostGallery, databasePost, mFilePath);
            alertDialog.dismiss();
        }
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