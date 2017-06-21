package com.bugsnguns.kartta;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class TrackActivity extends AppCompatActivity {

    private static final String TAG = "TrackActivity";
    public TrackDataBase trackDataBaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate method called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        TextView distanceView = (TextView)findViewById(R.id.distanceView);
        distanceView.setText(MapsActivity.thisActivity.trackDatabase.Read());

    }
}
