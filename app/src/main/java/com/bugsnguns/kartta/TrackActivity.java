package com.bugsnguns.kartta;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class TrackActivity extends AppCompatActivity {

    private static final String TAG = "TrackActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate method called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
    }
}
