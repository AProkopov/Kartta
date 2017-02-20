package com.bugsnguns.kartta;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Antonio on 20.02.2017.
 */

public class DataStorage {
    //переменная содержит все поступающие LatLng (без какой-либо фильтрации)
    public ArrayList<LatLng> geoLocationList;

    //переменная содержит неповторяющиеся LatLng из geoLocationList
    public ArrayList<LatLng> locationsForMap;

    //всегда содержит информацию о последнем известном местоположении
    public Location mCurrentLocation;

    //содержит информацию о размере ArrayList, в котором хранятся гео-точки
    public int geoLocationListSize;

    //конструктор, задающий корректные значения переменных при создании экземпляра класса
    public DataStorage () {
        geoLocationList = null;
        locationsForMap = null;
        mCurrentLocation = null;
        geoLocationListSize = 0;
    }



}
