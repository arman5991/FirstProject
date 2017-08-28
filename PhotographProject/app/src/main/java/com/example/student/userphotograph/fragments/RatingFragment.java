package com.example.student.userphotograph.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.userphotograph.R;
import com.example.student.userphotograph.adapter.RatingRecyclerAdapter;
import com.example.student.userphotograph.models.RatingModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.example.student.userphotograph.utilityes.Constants.PHOTOGRAPHS;
import static com.example.student.userphotograph.utilityes.Constants.RATING_UID;
import static com.example.student.userphotograph.utilityes.Constants.R_UID1;
import static com.example.student.userphotograph.utilityes.Constants.R_UID2;
import static com.example.student.userphotograph.utilityes.Constants.R_UID3;
import static com.example.student.userphotograph.utilityes.Constants.R_UID4;
import static com.example.student.userphotograph.utilityes.Constants.R_UID5;

public class RatingFragment extends Fragment implements RatingRecyclerAdapter.OnItemClickRating {

    private List<RatingModel> ratingList;
    private String uid[] = new String[5];

    public static RatingFragment newInstance() {
        return new RatingFragment();
    }

    public RatingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rating, container, false);

        ratingList = new ArrayList<>();
        final RecyclerView ratingRecyclerView = getRecyclerView(rootView);
        ratingUid(ratingRecyclerView);
        return rootView;
    }

    @NonNull
    private RecyclerView getRecyclerView(View rootView) {
        final RecyclerView ratingRecyclerView = (RecyclerView) rootView.findViewById(R.id.rating_recycler_view);
        LinearLayoutManager rlm = new LinearLayoutManager(getActivity());
        ratingRecyclerView.setLayoutManager(rlm);
        ratingRecyclerView.setHasFixedSize(true);
        return ratingRecyclerView;
    }

    private void sharedUid() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(RATING_UID, Context.MODE_PRIVATE);
        uid[0] = sharedPreferences.getString(R_UID1, "");
        uid[1] = sharedPreferences.getString(R_UID2, "");
        uid[2] = sharedPreferences.getString(R_UID3, "");
        uid[3] = sharedPreferences.getString(R_UID4, "");
        uid[4] = sharedPreferences.getString(R_UID5, "");
    }

    private void ratingUid(final RecyclerView ratingRecyclerView) {
        DatabaseReference rDatabaseReference = FirebaseDatabase.getInstance().getReference().child(PHOTOGRAPHS);
        rDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 0; i < 5; i++) {
                    RatingModel model = dataSnapshot.child(uid[i]).getValue(RatingModel.class);
                    if (ratingList.size() < 5) ratingList.add(model);
                }
                ratingRecyclerView.setAdapter(new RatingRecyclerAdapter(ratingList, RatingFragment.this, getContext()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getModel(RatingModel model) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("userInfo", Parcels.wrap(model));
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        DetailFragment fr = new DetailFragment();
        fr.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.container_home, fr, "DETAILS_FRAGMENT")
                .addToBackStack(null)
                .commit();
    }
}