package com.example.student.userproject.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.student.userproject.R;
import com.example.student.userproject.model.DataObject;
import com.example.student.userproject.model.NotificationData;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class NetworkHelper {

    private static final String SERVER_KEY = "AAAAXOlepJ4:APA91bG49WQvvZ4YDaT_Wf_n7cLXwhjjwoo0rVzuw-r3cIEE9FC2WvimfMEmxEK1aw7rdkIkMwPyDk0_BxWWf3XuCIDSnWKqE8v_4hHNpQQb85yCgmcH2mgC5lSJjcScGRRdBRkknFhI";

    public static void sendNotificationRequest(Context context, String uid, double lat, double lng, String phoneNumber) {
        SharedPreferences getPref = context.getSharedPreferences("shared_key", MODE_PRIVATE);
        String token = getPref.getString("token_key", "");
        SharedPreferences shared = context.getSharedPreferences("PHOTOGRAPHER_UID", MODE_PRIVATE);
        SharedPreferences.Editor edit = shared.edit();
        edit.putString("uid", uid);
        edit.apply();
        Gson gson = new Gson();
        NotificationData notificationData = new NotificationData();
        DataObject dataObject = new DataObject();
        if (lat != 0 && lng != 0) {
            dataObject.setLat(lat);
            dataObject.setLng(lng);
        }
        if (phoneNumber != null) {
            dataObject.setPhone(phoneNumber);
        }
        dataObject.setTitle("You have a new order!");
        dataObject.setToken(token);

        notificationData.setData(dataObject);
        notificationData.setTo("/topics/" + uid);

        String json = gson.toJson(notificationData);
        String url = "https://fcm.googleapis.com/fcm/send";

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "key=" + SERVER_KEY)
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }
}

