package com.bugsnguns.kartta;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import java.util.ArrayList;

/**
 * Created by Antonio on 20.02.2017.
 */

public class DataStorage {
    //переменная содержит все поступающие LatLng (без какой-либо фильтрации)
    public ArrayList<LatLng> geoLocationList;

    //переменная содержит информацию о последнем местоположении
    public Location mLastLocation;

    //переменная содержит неповторяющиеся LatLng из geoLocationList
    public ArrayList<LatLng> locationsForMap;

    //всегда содержит информацию о текущем местоположении
    public Location mCurrentLocation;

    //содержит информацию о размере ArrayList, в котором хранятся гео-точки
    public int geoLocationListSize;

    //содержит информацию о размере ArrayList, в котором хранятся гео-точки для построения Polyline
    public int locationsForMapSize;

    //содержит информацию о стартовой точке (ставится маркер начала маршрута)
    public LatLng startLocation;

    //содержит информацию о финишной точке (ставится макер в конце маршрута)
    public LatLng finalLocation;

    //содержит информацию о финишном маркере
    public Marker finishMarker;

    public double distance;

    //конструктор, задающий корректные значения переменных при создании экземпляра класса
    public DataStorage () {
        geoLocationList = new ArrayList<>();
        locationsForMap = new ArrayList<>();
        mCurrentLocation = null;
        startLocation = null;
        finalLocation = null;
        mLastLocation = null;
        finishMarker = null;
        geoLocationListSize = 0;
        locationsForMapSize = 0;
        distance = 0.0;
    }



}
