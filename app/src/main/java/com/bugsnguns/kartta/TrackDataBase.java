package com.bugsnguns.kartta;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Antonio on 21.03.2017.
 */

public class TrackDataBase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LastTrack.db";
    private static final String TAG = "TrackDataBase";
    public ContentValues distanceValues= new ContentValues();

    public TrackDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "DB is created");
        db.execSQL("CREATE TABLE DISTANCE ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "DISTANCE REAL); ");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
