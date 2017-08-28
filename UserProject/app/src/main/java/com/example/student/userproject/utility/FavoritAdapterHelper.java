package com.example.student.userproject.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.student.userproject.model.PhotographInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.student.userproject.utility.Constants.PHOTOGRAPHS;


public class FavoritAdapterHelper {

    private static List<PhotographInfo> listFavorits;

    public static  List<PhotographInfo> getFavoritList(){
        return listFavorits;
    }

    public static void initFavoritList(Context context) {

        listFavorits = new ArrayList<>();
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child(PHOTOGRAPHS);
        final SharedPreferences sheredPref = context.getSharedPreferences(Constants.FAVORITES_PREF, Context.MODE_PRIVATE);

        final Map<String, ?> allEntries = sheredPref.getAll();
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    String favoriteUid = sheredPref.getString(entry.getKey(), "");

                    if (!favoriteUid.equals("")) {
                        PhotographInfo photographInfo = dataSnapshot.child(favoriteUid).getValue(PhotographInfo.class);
                        listFavorits.add(photographInfo);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
