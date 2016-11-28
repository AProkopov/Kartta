package com.bugsnguns.kartta;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    public boolean mRequestingLocationUpdates = true;
    public ArrayList<LatLng> geoLocationList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //start GeoDataService
        Intent serviceIntent = new Intent(this, GeoDataService.class);
        startService(serviceIntent);

        // Create an instance of GoogleAPIClient. Создаем ApiClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Override
    protected void onStart() {
        //соединение с API при создании Activity
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        //отключение от API при остановке Activity
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps_menu, menu);
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) throws SecurityException{
        //требование реализации интерфейса GoogleApiClient.ConnectionCallbacks
        //запрашиваем последнюю известную локацию
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    //требование реализации интерфейса GoogleApiClient.OnConnectionFailedListener
    }

    @Override
    public void onConnectionSuspended(int i) {
    //требование реализации интерфейса GoogleApiClient.ConnectionCallbacks
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //adding LocationLayout
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e ) {
            Toast.makeText(MapsActivity.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
        }

        //можно проверить наличие необходимых permissions
        /**if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //добавляет на карту точку с геопозицией и кнопку геопозиции
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(MapsActivity.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //добавляет на карту точку с геопозицией и кнопку геопозиции
                mMap.setMyLocationEnabled(true);
            }
        } */

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void startGeoTracking(View view) {
        //проверка нажатия кнопки
        Log.v("START_BUTTON onClick", "startButton clicked");
        //trackRecorder() работает в тестовом режиме, выводит текст в консоль
        GeoDataService.trackRecorder();
        //ставим маркер на текущей локации
        LatLng myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
    }

}
