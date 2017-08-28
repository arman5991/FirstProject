package com.example.student.userproject.service;


import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("MyFirebaseInstanceID", "Refreshed token: " + refreshedToken);

        SharedPreferences sharedPref = getSharedPreferences("shared_key",MODE_PRIVATE);
        SharedPreferences.Editor editToken = sharedPref.edit();
        editToken.putString("token_key",refreshedToken);
        editToken.apply();
    }

}