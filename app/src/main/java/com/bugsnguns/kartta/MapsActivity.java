package com.bugsnguns.kartta;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
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
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
//import java.util.ArrayList;
//import static java.security.AccessController.getContext;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    protected static GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    //private Location mLastLocation;
    //public Location mCurrentLocation;
    public LocationRequest mLocationRequest;
    public boolean mRequestingLocationUpdates = true;
    public boolean isRecording = false;
    public boolean isDrawing = false;
    //public ArrayList<LatLng> geoLocationList = new ArrayList<>(); //собираем все поступающие локации
    //public ArrayList<LatLng> locationsForMap = new ArrayList<>(); //собираем неповторяющиеся локации
    //public ArrayList<LatLng> locationsToDraw = new ArrayList<>(); //из неповторяющихся локаций берем две последних
    //public int geoLocationListSize = 0;
    //public int locationsForMapSize = 0;
    public PolylineOptions routeOpts = null;
    public Polyline route;
    //public LatLng startLocation = null;
    //public LatLng finalLocation = null;
    public boolean isStarted = false;
    public boolean isPaused = false;
    public boolean isStopped = false;
    public boolean onceStarded = false;
    public Button startButton;
    public Button pauseButton;
    public Button stopButton;
    public DataStorage dataStorage;
    public DataHandler dataHandler;
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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //start GeoDataService
        Intent serviceIntent = new Intent(this, GeoDataService.class);
        startService(serviceIntent);
        Log.v(TAG, "GeoDataService is running");

        // Create an instance of GoogleAPIClient. Создаем ApiClient
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

    //toDo написать чей метод определили
    @Override
    protected void onStart() {
        //соединение с API при создании Activity
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    //toDo написать чей метод определили
    @Override
    protected void onStop() {
        //отключение от API при остановке Activity
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    //toDo написать чей метод определили
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps_menu, menu);
        return true;
    }

    //toDo написать чей метод определили
    @Override
    public void onConnected(Bundle bundle) throws SecurityException{
        //требование реализации интерфейса GoogleApiClient.ConnectionCallbacks
        //запрашиваем последнюю известную локацию
        dataStorage.mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        Log.v(TAG, "onConnected() works fine");

    }

    //toDo написать чей метод определили
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    //требование реализации интерфейса GoogleApiClient.OnConnectionFailedListener
    }
    //toDo написать чей метод определили
    @Override
    public void onConnectionSuspended(int i) {
    //требование реализации интерфейса GoogleApiClient.ConnectionCallbacks
    }
    //toDo написать чей метод определили
    @Override
    public void onLocationChanged (Location location) {

        //требование реализации интерфейса LocationListener
        dataStorage.mCurrentLocation = location;

        //вызывается метод объекта dataHandler, записывающий данные о поступающих местоположениях
        //в ArrayList объекта dataStorage
        dataHandler.receiveData(dataStorage, isRecording, onceStarded);
        dataHandler.makeUniqueLocations(dataStorage, dataHandler);
        dataHandler.toDrawRoute(route, dataStorage.locationsForMap, isDrawing);
    }

    //toDo написать чей метод определили
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //adding LocationLayout
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

        //проверка нажатия кнопки
        if (isStarted == false) {
            onceStarded = true;
            isRecording = true;
            isDrawing = true;
            isStarted = true;
            isPaused = false;
            isStopped = false;

            startButton.setBackgroundColor(getResources().getColor(R.color.startButtonColor));
            pauseButton.setBackgroundResource(android.R.drawable.btn_default);
            stopButton.setBackgroundResource(android.R.drawable.btn_default);

            Log.v(TAG, "startButton is pressed");

            dataHandler.putStartMarker(dataStorage);

            Log.v(TAG, "putStartMarker method is called");
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

            pauseButton.setBackgroundColor(getResources().getColor(R.color.pauseButtonColor));
            startButton.setBackgroundResource(android.R.drawable.btn_default);
            stopButton.setBackgroundResource(android.R.drawable.btn_default);
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

            stopButton.setBackgroundColor(getResources().getColor(R.color.stopButtonColor));
            pauseButton.setBackgroundResource(android.R.drawable.btn_default);
            startButton.setBackgroundResource(android.R.drawable.btn_default);

            dataHandler.putFinishMarker(dataStorage);
        }
    }


}
