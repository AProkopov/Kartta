package com.bugsnguns.kartta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    public SQLiteDatabase db;
    public Cursor cursor;

    public TrackDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "DB is created");
        db.execSQL("CREATE TABLE TRACK ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "DISTANCE REAL); ");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //метод записывает данные о пройденном расстоянии в БД
    public void onWrite () {
        if (db == null) {
            Log.v(TAG, "getWritableDatabase() is going to be called");
            db = this.getWritableDatabase();
            Log.v(TAG, "getWritableDatabase() is called");
        }

        Log.v(TAG, "onWrite is called");
        db.insert("TRACK", null, distanceValues);
        Log.v(TAG, "onWrite finished");

        //// TODO: 23.05.2017
        //метод размещен тут с целью тестирования
        onRead();
    }

    public void createCursor () {
        Log.v(TAG, "createCursor() is called");
        cursor = this.getWritableDatabase().query("TRACK",
                new String[] {"DISTANCE"},
                null, null, null, null, null);
        Log.v(TAG, "createCursor() is finished");
    }

    public void onRead () {

        Log.v(TAG, "onRead() is called");
        if (cursor == null) {
            createCursor();
            Log.v(TAG, "cursor is created");
        }
        //// TODO: 23.05.2017
        //разобраться с проблемой: не обновляются данные в БД
        if (cursor.moveToFirst()) {

            System.out.println("WOW THAT WORKS " + cursor.getDouble(0));
            Log.v(TAG, "onRead() is finished");
        }
    }



}
