package com.example.student.userphotograph.utilityes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "post.db";
    public static final String UID = "uid";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "likes";
    private ContentValues cv;

    public SQLHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE likes (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + UID + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long addPostLikes(String uid) {
        cv = new ContentValues();
        cv.put("uid", uid);
        SQLiteDatabase db = getWritableDatabase();

        long rowId = db.insert(TABLE, null, cv);
        db.close();
        return rowId;
    }


    public boolean isLiked(String uid) {

        String selectQuery = "SELECT  * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String postId = cursor.getString(cursor.getColumnIndex(UID));
                if (postId.equals(uid)) return true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }
}
