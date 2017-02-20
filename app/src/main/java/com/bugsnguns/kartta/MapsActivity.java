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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    public Location mCurrentLocation;
    public LocationRequest mLocationRequest;
    public boolean mRequestingLocationUpdates = true;
    public boolean isRecording = false;
    public boolean isDrawing = false;
    public ArrayList<LatLng> geoLocationList = new ArrayList<>(); //собираем все поступающие локации
    public ArrayList<LatLng> locationsForMap = new ArrayList<>(); //собираем неповторяющиеся локации
    public ArrayList<LatLng> locationsToDraw = new ArrayList<>(); //из неповторяющихся локаций берем две последних
    public int geoLocationListSize = 0;
    public int locationsForMapSize = 0;
    public PolylineOptions routeOpts = null;
    public Polyline route;
    public LatLng startLocation = null;
    public LatLng finalLocation = null;
    public boolean isStarted = false;
    public boolean isPaused = false;
    public boolean isStopped = false;
    public Button startButton;
    public Button pauseButton;
    public Button stopButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_maps);

        startButton = (Button) findViewById(R.id.startButton);
        pauseButton = (Button) findViewById(R.id.pauseButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //start GeoDataService
        Intent serviceIntent = new Intent(this, GeoDataService.class);
        startService(serviceIntent);

        // Create an instance of GoogleAPIClient. Создаем ApiClient
        if (mGoogleApiClient == null) {
            Log.v("1", "mGoogleApiClient is null");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.v("2", "mGoogleApiClient is ok");
            Log.v("8", mGoogleApiClient.toString());
        }

        //делаем запрос о местоположении
        createLocationRequest();
        Log.v("3", "createLocationRequest ok");
    }

    //добавляем возможность запрашивать у Google Play Services информацию о местоположении
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.v("4", "createLocationRequest running ok");
    }

    //toDo написать чей метод определили
    @Override
    protected void onStart() {
        //соединение с API при создании Activity
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        //проверка. переменная testVar_onStart создана исключительно для проверки mGoogleApiClient
        boolean testVar_onStart = (mGoogleApiClient == null);
        Log.v("11", "mGoogleApiClient is" + testVar_onStart);
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
            Log.v("5", "startLocationUpdates ok");
        }
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
        mCurrentLocation = location;

        //добавляем координаты в массив
        if (isRecording) {
            geoLocationList.add(new LatLng(location.getLatitude(), location.getLongitude()));
            geoLocationListSize++;


            //тестовый метод для проверки записи в ArrayList
                String size = String.valueOf(geoLocationList.size());
                double lat = geoLocationList.get(geoLocationListSize - 1).latitude;
                double lng = geoLocationList.get(geoLocationListSize - 1).longitude;

                Log.v("List test_size", size);
                Log.v("List test_values", "latitude is " + lat + " longitude is " + lng);


            //исключаем дублирующиеся точки и создаем новый Array, точки из которого
            //уже можно будут рисовать
            if (geoLocationListSize > 1) {
                if (!DataHandler.duplication(geoLocationList.get(geoLocationList.size() - 1),
                        geoLocationList.get(geoLocationList.size() - 2))) {
                    locationsForMap.add(geoLocationList.get(geoLocationList.size() - 1));
                    locationsForMapSize++;

                    //берем последнюю пришедшую точку и добавляем ее в ArrayList, который будем
                    //передавать методу toDraw() класса DataHandler для отображения ломанной на карте
                    //должен содержать не более двух точек. для этого при каждом вызове
                    //метода toDraw класса DataHandler очищается методом clear()
                    locationsToDraw.add(locationsForMap.get(locationsForMapSize - 1));
                    locationsToDraw.add(locationsForMap.get(locationsForMapSize - 2));
                }
            } else if (geoLocationListSize == 1) {
                locationsForMap.add(geoLocationList.get(0));
                locationsForMapSize++;
            }
            //проверка наполняемости locationsForMap данными (корректность прохождения фильтра !duplication)
            Log.v("locationsForMaps_size", "" + locationsForMap.size());
            Log.v("locationsForMaps_values", "latitude is " + locationsForMap.get(locationsForMapSize -1 ).latitude
                    + " longitude is " + locationsForMap.get(locationsForMapSize -1 ).longitude);
        }

        //рисуем отрезок на карте
        if (isDrawing) {
            route.setPoints(locationsForMap);

            //установка маркера на стартовой локации
            if (startLocation == null) {
                startLocation = new LatLng(locationsForMap.get(0).latitude, locationsForMap.get(0).longitude);
                mMap.addMarker(new MarkerOptions().position(startLocation).title("Start"));
            }
        }

        if (isStopped) {
            finalLocation = new LatLng(locationsForMap.get(locationsForMap.size() - 1).latitude,
                    locationsForMap.get(locationsForMap.size() - 1).longitude);
            mMap.addMarker(new MarkerOptions().position(finalLocation).title("Finish"));

        }
    }
    //toDo написать чей метод определили
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //adding LocationLayout
        try {
            mMap.setMyLocationEnabled(true);
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
        }
    }

    //начинаем получать обновления локации. Логи были необходимы для поиска ошибок, решил не удалять (могут пригодиться)
    protected void startLocationUpdates() throws SecurityException{
        Log.v("6", "startLocationUpdates called ok");
        boolean testVar_startLOcationUpdates = (mGoogleApiClient == null);
        Log.v("10", "mGoogleApiClient is" + testVar_startLOcationUpdates);
        Log.v("9", mGoogleApiClient.toString());
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.v("7", "startLocationUpdates running ok");
    }

    public void startGeoTracking(View view) {
        //проверка нажатия кнопки
        Log.v("START_BUTTON onClick", "startButton clicked");
        if (isStarted == false) {
            isRecording = true;
            isDrawing = true;
            isStarted = true;
            isPaused = false;
            isStopped = false;

            startButton.setBackgroundColor(getResources().getColor(R.color.startButtonColor));
            pauseButton.setBackgroundResource(android.R.drawable.btn_default);
            stopButton.setBackgroundResource(android.R.drawable.btn_default);
        }

    }

    public void pauseGeoTracking(View view) {
        //проверка нажатия кнопки
        Log.v("PAUSE_BUTTON onClick", "PauseButton clicked");

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
        Log.v("STOP_BUTTON onClick", "StopButton clicked");

        if (isStarted ==true || isPaused == true) {
            isRecording = false;
            isDrawing = false;
            isPaused = true;
            isStarted = false;
            isStopped = true;

            stopButton.setBackgroundColor(getResources().getColor(R.color.stopButtonColor));
            pauseButton.setBackgroundResource(android.R.drawable.btn_default);
            startButton.setBackgroundResource(android.R.drawable.btn_default);
        }
    }


}
