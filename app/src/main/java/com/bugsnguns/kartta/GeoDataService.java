package com.bugsnguns.kartta;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class GeoDataService extends Service {

    public GeoDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    //метод ранее использовался в качестве тестового, вызывался из MapsActivity.startGeoTracking
    //пока пусть тут полежит
    public static void trackRecorder() {
        //this for-loop just for test
        for (int i = 0; i <=10; i++) {
            Log.v("TEST trackRecorder", "trackRecorder is running " + i);
        }

    }
}
