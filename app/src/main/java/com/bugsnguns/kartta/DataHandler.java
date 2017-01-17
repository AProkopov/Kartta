package com.bugsnguns.kartta;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;


/**
 * Created by Antonio on 14.01.2017.
 */

public class DataHandler {
    //класс содержит методы для обработки и анализа данных

    public static boolean duplication (LatLng currentLocation, LatLng previousLocation) {

        /* переменные в закоменированном блоке созданы для перехода к сравнению локаций

        double currentLatitude = currentLocation.latitude;
        double currentLongitude = currentLocation.longitude;

        double preveousLatitude = previousLocation.latitude;
        double previousLongitude = previousLocation.longitude; */

        //если объекты одинаковые, то и хеш-коды одинаковые
        if (currentLocation.hashCode() == previousLocation.hashCode()) {
            return true;
        } else return false;

    }

    public static void toDraw(Polyline polyline, ArrayList<LatLng> list) {
        polyline.setPoints(list);
        list.clear();
    }
}
