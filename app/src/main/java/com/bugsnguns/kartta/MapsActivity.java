package com.bugsnguns.kartta;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    protected static GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    public boolean mRequestingLocationUpdates = true;
    public boolean isRecording = false;
    public boolean isDrawing = false;
    public PolylineOptions routeOpts = null;
    public Polyline route;
    public boolean isStarted = false;
    public boolean isPaused = false;
    public boolean isStopped = false;
    public boolean onceStarted = false;
    public Button startButton;
    public Button pauseButton;
    public Button stopButton;
    public DataStorage dataStorage;
    public DataHandler dataHandler;
    TrackDataBase trackDataBase = new TrackDataBase(this);
    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_maps);
        Log.v(TAG, "Activity created successfully");

        //создаем объекты Button для работы с кнопками
        startButton = (Button) findViewById(R.id.startButton);
        pauseButton = (Button) findViewById(R.id.pauseButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        Log.v(TAG, "Button objects successfully");

        //создание объектов, которые будут хранить и обрабатывать поступающую
        //информацию о местоположении
        dataStorage = new DataStorage();
        dataHandler = new DataHandler();
        Log.v(TAG, "dataStorage and dataHandler created successfully");

        //создаем SupportMapFragment для отображения карты
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //создание GeoDataService
        Intent serviceIntent = new Intent(this, GeoDataService.class);
        startService(serviceIntent);
        Log.v(TAG, "GeoDataService is running");

        //Создаем GoogleApiClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.v(TAG, "mGoogleApiClient created");
        }

        //делаем запрос о местоположении
        createLocationRequest();
        Log.v(TAG, "createLocationRequest() is called");
    }

    //добавляем возможность запрашивать у Google Play Services информацию о местоположении
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.v(TAG, "createLocationRequest() works fine");
    }

    //метод onStart() класса Activity
    @Override
    protected void onStart() {
        //соединение с API при создании Activity
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    //метод onStop() класса Activity
    @Override
    protected void onStop() {
        //отключение от API при остановке Activity
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    //метод onCreateOptionsMenu() класса Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps_menu, menu);
        return true;
    }

    //метод выполняет действия, необходимые для каждого пункта меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //нажатие пункта Last activity открывает экран с данными о последнем трэке
            case R.id.lastTrack: {
                Log.v(TAG, "Last track menu item clicked");
                Intent intent = new Intent(this, TrackActivity.class);
                Log.v(TAG, "Intent for TrackActivity created");
                startActivity(intent);
                Log.v(TAG, "Intent for TrackActivity sent");
            }
        }
        return false;
    }

    //требование реализации интерфейса GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnected(Bundle bundle) throws SecurityException{
        //запрашиваем последнюю известную локацию
        dataStorage.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        Log.v(TAG, "onConnected() works fine");

    }

    //требование реализации интерфейса GoogleApiClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    //требование реализации интерфейса GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnectionSuspended(int i) {
    }

    //требование реализации интерфейса LocationListener
    @Override
    public void onLocationChanged (Location location) {
        dataStorage.mCurrentLocation = location;

        //вызывается метод объекта dataHandler, записывающий данные о поступающих местоположениях
        //в ArrayList объекта dataStorage
        dataHandler.receiveData(dataStorage, isRecording, onceStarted);
        dataHandler.makeUniqueLocations(dataStorage, dataHandler);
        dataHandler.toDrawRoute(route, dataStorage.locationsForMap, isDrawing);
    }

    //требование реализации интерфейса onMapReadyCallback
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //проверка доступности данных о текущем местоположении
        try {
            mMap.setMyLocationEnabled(true);
            Log.v(TAG, "mMap is ready");

        } catch (SecurityException e ) {
            Toast.makeText(MapsActivity.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
        }

        //когда готова карта, создаем polyline для отрисовки трэка
        if (mMap != null) {
            routeOpts = new PolylineOptions()
                    .color(Color.BLUE)
                    .width(4)
                    .geodesic(true);
            route = mMap.addPolyline(routeOpts);
            Log.v(TAG, "route is ready");
        }
    }

    //начинаем получать обновления локации
    protected void startLocationUpdates() throws SecurityException{
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.v(TAG, "startLocationUpdates() called successfully");
    }

    public void startGeoTracking(View view) {
        if (dataStorage.mCurrentLocation != null) {

            //проверка нажатия кнопки
            if (isStarted == false) {
                onceStarted = true;
                isRecording = true;
                isDrawing = true;
                isStarted = true;
                isPaused = false;
                isStopped = false;

                //убираем ранее проставленный маркер Finish
                dataHandler.removeFinishMarker(dataStorage);

                startButton.getBackground().setColorFilter(getResources().getColor(R.color.startButtonColor), PorterDuff.Mode.MULTIPLY);
                pauseButton.getBackground().setColorFilter(null);
                stopButton.getBackground().setColorFilter(null);
                Log.v(TAG, "startButton is pressed");

                dataHandler.putStartMarker(dataStorage);

                Log.v(TAG, "putStartMarker method is called");
            }

        } else {
            Context context = getApplicationContext();
            CharSequence text = "Поздравляем!" + "\n" + "Вы соображаете быстрее спутников :)" ;
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public void pauseGeoTracking(View view) {
        //проверка нажатия кнопки
        if (isStarted ==true) {
            isRecording = false;
            isDrawing = false;
            isPaused = true;
            isStarted = false;
            isStopped = false;

            pauseButton.getBackground().setColorFilter(getResources().getColor(R.color.pauseButtonColor), PorterDuff.Mode.MULTIPLY);
            startButton.getBackground().setColorFilter(null);
            stopButton.getBackground().setColorFilter(null);
        }
    }

    public void stopGeoTracking(View view) {
        //проверка нажатия кнопки
        if (isStarted ==true || isPaused == true) {
            isRecording = false;
            isDrawing = false;
            isPaused = true;
            isStarted = false;
            isStopped = true;

            stopButton.getBackground().setColorFilter(getResources().getColor(R.color.stopButtonColor), PorterDuff.Mode.MULTIPLY);
            startButton.getBackground().setColorFilter(null);
            pauseButton.getBackground().setColorFilter(null);

            dataHandler.putFinishMarker(dataStorage);
            dataHandler.trackLengthComputer(dataStorage.locationsForMap, dataStorage);

            Log.v(TAG, "distance is " + dataStorage.distance);

            //в БД вносится информация о пройденном расстоянии
            //вызов БП, запись расояния, чтение расстояния реализовать в TrackDataBase
            //здесь только вызывать методы из TrackDataBase
            SQLiteDatabase db = trackDataBase.getWritableDatabase();
            trackDataBase.distanceValues.put("DISTANCE", dataStorage.distance);





        }
    }
}
