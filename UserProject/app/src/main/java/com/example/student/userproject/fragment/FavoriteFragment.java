package com.example.student.userproject.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.student.userproject.adapter.FavoriteRecyclerAdapter;
import com.example.student.userproject.model.PhotographInfo;
import com.example.student.userproject.R;
import com.example.student.userproject.utility.FavoritAdapterHelper;

import org.parceler.Parcels;

public class FavoriteFragment extends Fragment implements FavoriteRecyclerAdapter.OnItemClickFavorite {

    public FavoriteFragment() {

    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(lm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new FavoriteRecyclerAdapter(FavoritAdapterHelper.getFavoritList(), this, getContext()));

        return rootView;
    }

    @Override
    public void getModel(PhotographInfo model) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("userInfo", Parcels.wrap(model));
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        PhotographDetailInfoFragment fr = new PhotographDetailInfoFragment();
        fr.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.container, fr, "DETAILS_FRAGMENT")
                .addToBackStack(null)
                .commit();
    }
}