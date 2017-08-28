package com.example.student.userphotograph.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.student.userphotograph.R;
import com.example.student.userphotograph.adapter.AboutRecyclerAdapter;
import com.example.student.userphotograph.models.AboutModel;

import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends Fragment {

    private List<AboutModel> aboutModelList;
    private TextView tvInfo;

    public AboutFragment() {
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        aboutModelList = new ArrayList<>();
        findViewById(rootView);
        recyclerView(rootView);
        prepareAboutData();

        tvInfo.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='http://vtc.am/am/'> @2017 VTC Android team   " +
                "http://vtc.am </a>";
        tvInfo.setText(Html.fromHtml(text));
        return rootView;
    }

    private void recyclerView(View rootView) {
        RecyclerView myRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_about);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        myRecycler.setLayoutManager(mLayoutManager);
        myRecycler.setHasFixedSize(true);
        myRecycler.setAdapter(new AboutRecyclerAdapter(aboutModelList, getActivity()));
    }

    private void findViewById(View rootView) {
        tvInfo = (TextView) rootView.findViewById(R.id.vtc_and_team);
    }

    private void prepareAboutData() {
        AboutModel modelSmbat = new AboutModel(drawableToBitmap(R.drawable.smbat_sargsyan), "Smbat Sargsyan");
        aboutModelList.add(modelSmbat);
        AboutModel modelNarek = new AboutModel(drawableToBitmap(R.drawable.narek_jaghatspanyan), "Narek Jaghatspanyan");
        aboutModelList.add(modelNarek);
        AboutModel modelNelly = new AboutModel(drawableToBitmap(R.drawable.nelly_galstyan), "Nelly Galstyan");
        aboutModelList.add(modelNelly);
        AboutModel modelIrina = new AboutModel(drawableToBitmap(R.drawable.irina_kazazyan), "Irina Kazazyan");
        aboutModelList.add(modelIrina);
        AboutModel modelBabken = new AboutModel(drawableToBitmap(R.drawable.babken_mxitaryan), "Babken Mxitaryan");
        aboutModelList.add(modelBabken);
        AboutModel modelTaron = new AboutModel(drawableToBitmap(R.drawable.taron_mkrtchyan), "Taron Mkrtchyan");
        aboutModelList.add(modelTaron);
        AboutModel modelSeryan = new AboutModel(drawableToBitmap(R.drawable.seyran_alaverdyan), "Seyran Alavaerdyan");
        aboutModelList.add(modelSeryan);
        AboutModel modelArmen = new AboutModel(drawableToBitmap(R.drawable.armen_gevorgyan), "Armen Gevorgyan");
        aboutModelList.add(modelArmen);
        AboutModel modelArman = new AboutModel(drawableToBitmap(R.drawable.arman_vardanyan), "Arman Vardanyan");
        aboutModelList.add(modelArman);
        AboutModel modelAnna = new AboutModel(drawableToBitmap(R.drawable.avatar_girl), "Anna Hovhannisyan");
        aboutModelList.add(modelAnna);
    }
    private Integer drawableToBitmap(Integer  drawable){
        return drawable;
    }
}