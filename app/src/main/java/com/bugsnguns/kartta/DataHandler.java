package com.bugsnguns.kartta;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import java.util.ArrayList;


/**
 * Created by Antonio on 14.01.2017.
 */

public class DataHandler {
    //класс содержит методы для обработки и анализа данных

    public static boolean duplication (LatLng currentLocation, LatLng previousLocation) {

        //если объекты одинаковые, то и хеш-коды одинаковые
        if (currentLocation.hashCode() == previousLocation.hashCode()) {
            return true;
        } else return false;

    }

    public static void toDraw(Polyline polyline, ArrayList<LatLng> list) {
        polyline.setPoints(list);
        list.clear();
    }

    //сохраняет новую информацию о местоположении в массив
    public void dataReceiver (Location location, DataStorage dataStorage, boolean isRecording) {
        dataStorage.mCurrentLocation = location;

        //добавляем координаты в массив
        if (isRecording) {
            dataStorage.geoLocationList.add(new LatLng(location.getLatitude(), location.getLongitude()));
            dataStorage.geoLocationListSize++;

        }
    }
}
